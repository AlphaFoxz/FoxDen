package com.github.alphafoxz.foxden.common.log.aspect

import cn.hutool.core.map.MapUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.time.StopWatch
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessStatus
import com.github.alphafoxz.foxden.common.log.event.OperLogEvent
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.http.HttpMethod
import org.springframework.validation.BindingResult
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 * 操作日志记录处理
 */
@Aspect
@AutoConfiguration
class LogAspect {
    private val log = LoggerFactory.getLogger(LogAspect::class.java)

    companion object {
        /**
         * 排除敏感属性字段
         */
        val EXCLUDE_PROPERTIES = arrayOf("password", "oldPassword", "newPassword", "confirmPassword")
    }

    /**
     * 计时 key
     */
    private val keyCache = ThreadLocal<StopWatch>()

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    fun doBefore(joinPoint: JoinPoint, controllerLog: Log) {
        val stopWatch = StopWatch()
        keyCache.set(stopWatch)
        stopWatch.start()
    }

    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    fun doAfterReturning(joinPoint: JoinPoint, controllerLog: Log, jsonResult: Any?) {
        handleLog(joinPoint, controllerLog, null, jsonResult)
    }

    /**
     * 拦截异常操作
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    fun doAfterThrowing(joinPoint: JoinPoint, controllerLog: Log, e: Exception) {
        handleLog(joinPoint, controllerLog, e, null)
    }

    protected fun handleLog(joinPoint: JoinPoint, controllerLog: Log, e: Exception?, jsonResult: Any?) {
        try {
            val operLog = OperLogEvent()
            operLog.tenantId = LoginHelper.getTenantId()
            operLog.status = BusinessStatus.SUCCESS.ordinal

            // 请求的地址
            val ip = ServletUtils.getClientIP()
            operLog.operIp = ip
            operLog.operUrl = StringUtils.substring(ServletUtils.getRequest().requestURI, 0, 255)

            val loginUser = LoginHelper.getLoginUser()
            operLog.operName = loginUser?.username
            operLog.deptName = loginUser?.deptName

            if (e != null) {
                operLog.status = BusinessStatus.FAIL.ordinal
                operLog.errorMsg = StringUtils.substring(e.message, 0, 3800)
            }

            // 设置方法名称
            val className = joinPoint.target.javaClass.name
            val methodName = (joinPoint.signature as MethodSignature).method.name
            operLog.method = "$className.$methodName()"
            // 设置请求方式
            operLog.requestMethod = ServletUtils.getRequest().method

            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult)

            // 设置消耗时间
            val stopWatch = keyCache.get()
            stopWatch.stop()
            operLog.costTime = stopWatch.time

            // 发布事件保存数据库
            SpringUtils.context().publishEvent(operLog)
        } catch (exp: Exception) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.message)
        } finally {
            keyCache.remove()
        }
    }

    /**
     * 获取注解中对方法的描述信息
     */
    @Throws(Exception::class)
    fun getControllerMethodDescription(joinPoint: JoinPoint, log: Log, operLog: OperLogEvent, jsonResult: Any?) {
        // 设置action动作
        operLog.businessType = log.businessType.ordinal
        // 设置标题
        operLog.title = log.title
        // 设置操作人类别
        operLog.operatorType = log.operatorType.ordinal

        // 是否需要保存request，参数和值
        if (log.isSaveRequestData) {
            setRequestValue(joinPoint, operLog, log.excludeParamNames)
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData && ObjectUtil.isNotNull(jsonResult)) {
            operLog.jsonResult = StringUtils.substring(JsonUtils.toJsonString(jsonResult), 0, 3800)
        }
    }

    /**
     * 获取请求的参数，放到log中
     */
    @Throws(Exception::class)
    private fun setRequestValue(joinPoint: JoinPoint, operLog: OperLogEvent, excludeParamNames: Array<String>) {
        val paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest())
        val requestMethod = operLog.requestMethod

        if (MapUtil.isEmpty(paramsMap) && setOf("PUT", "POST", "DELETE").contains(requestMethod)
        ) {
            val params = argsArrayToString(joinPoint.args, excludeParamNames)
            operLog.operParam = StringUtils.substring(params, 0, 3800)
        } else {
            MapUtil.removeAny(paramsMap, *EXCLUDE_PROPERTIES)
            MapUtil.removeAny(paramsMap, *excludeParamNames)
            operLog.operParam = StringUtils.substring(JsonUtils.toJsonString(paramsMap), 0, 3800)
        }
    }

    /**
     * 参数拼装
     */
    private fun argsArrayToString(paramsArray: Array<Any?>, excludeParamNames: Array<String>): String {
        val params = StringJoiner(" ")
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString()
        }
        val excludeList = excludeParamNames.toMutableList()
        excludeList.addAll(EXCLUDE_PROPERTIES.toList())

        for (o in paramsArray) {
            if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                val str: String? = if (o is List<*>) {
                    val list1 = mutableListOf<Map<String, Any?>>()
                    for (obj in o) {
                        if (obj != null) {
                            val str1 = JsonUtils.toJsonString(obj)
                            val dict: Map<String, Any?>? = JsonUtils.parseMap(str1)
                            if (dict != null && MapUtil.isNotEmpty(dict)) {
                                for (key in excludeList) {
                                    @Suppress("UNCHECKED_CAST")
                                    (dict as MutableMap<Any?, Any?>).remove(key)
                                }
                                list1.add(dict)
                            }
                        }
                    }
                    JsonUtils.toJsonString(list1)
                } else {
                    // o is non-null due to ObjectUtil.isNotNull(o) check above
                    val str1 = JsonUtils.toJsonString(o!!)
                    val dict = JsonUtils.parseMap(str1)
                    if (dict != null && MapUtil.isNotEmpty(dict)) {
                        for (key in excludeList) {
                            @Suppress("UNCHECKED_CAST")
                            (dict as MutableMap<Any?, Any?>).remove(key)
                        }
                        JsonUtils.toJsonString(dict)
                    } else {
                        str1
                    }
                }
                params.add(str)
            }
        }
        return params.toString()
    }

    /**
     * 判断是否需要过滤的对象
     */
    fun isFilterObject(o: Any?): Boolean {
        if (o == null) return false
        val clazz = o.javaClass
        if (clazz.isArray) {
            return MultipartFile::class.java.isAssignableFrom(clazz.componentType)
        } else if (Collection::class.java.isAssignableFrom(clazz)) {
            val collection = o as Collection<*>
            for (value in collection) {
                return value is MultipartFile
            }
        } else if (Map::class.java.isAssignableFrom(clazz)) {
            val map = o as Map<*, *>
            for (value in map.values) {
                return value is MultipartFile
            }
        }
        return o is MultipartFile || o is HttpServletRequest || o is HttpServletResponse || o is BindingResult
    }
}
