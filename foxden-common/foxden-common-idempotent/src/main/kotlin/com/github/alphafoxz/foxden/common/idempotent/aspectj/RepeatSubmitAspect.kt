package com.github.alphafoxz.foxden.common.idempotent.aspectj

import cn.dev33.satoken.SaManager
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.crypto.SecureUtil
import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.validation.BindingResult
import org.springframework.web.multipart.MultipartFile
import java.time.Duration

/**
 * 防止重复提交(参考美团GTIS防重系统)
 *
 * @author Lion Li
 */
@Aspect
class RepeatSubmitAspect {

    companion object {
        private val KEY_CACHE = ThreadLocal<String>()
    }

    @Around("@annotation(repeatSubmit)")
    fun doBefore(point: ProceedingJoinPoint, repeatSubmit: RepeatSubmit): Any? {
        // 如果注解不为0 则使用注解数值
        val interval = repeatSubmit.timeUnit.toMillis(repeatSubmit.interval)

        if (interval < 1000) {
            throw ServiceException("重复提交间隔时间不能小于'1'秒")
        }
        val request = ServletUtils.getRequest()
        val nowParams = argsArrayToString(point.args)

        // 请求地址（作为存放cache的key值）
        val url = request.requestURI

        // 唯一值（没有消息头则使用请求地址）
        var submitKey = StringUtils.trimToEmpty(request.getHeader(SaManager.getConfig().tokenName))

        submitKey = SecureUtil.md5(submitKey + ":" + nowParams)
        // 唯一标识（指定key + url + 消息头）
        val cacheRepeatKey = GlobalConstants.REPEAT_SUBMIT_KEY + url + submitKey
        if (RedisUtils.setObjectIfAbsent(cacheRepeatKey, "", Duration.ofMillis(interval))) {
            KEY_CACHE.set(cacheRepeatKey)
        } else {
            var message = repeatSubmit.message
            if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                message = MessageUtils.message(StringUtils.substring(message, 1, message.length - 1)) ?: message
            }
            throw ServiceException(message)
        }

        return point.proceed()
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(repeatSubmit)", returning = "jsonResult")
    fun doAfterReturning(joinPoint: org.aspectj.lang.JoinPoint, repeatSubmit: RepeatSubmit, jsonResult: Any) {
        if (jsonResult is R<*>) {
            try {
                val r = jsonResult as R<*>
                // 成功则不删除redis数据 保证在有效时间内无法重复提交
                if (r.code == R.SUCCESS) {
                    return
                }
                RedisUtils.deleteObject(KEY_CACHE.get())
            } finally {
                KEY_CACHE.remove()
            }
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(repeatSubmit)", throwing = "e")
    fun doAfterThrowing(joinPoint: org.aspectj.lang.JoinPoint, repeatSubmit: RepeatSubmit, e: Exception) {
        RedisUtils.deleteObject(KEY_CACHE.get())
        KEY_CACHE.remove()
    }

    /**
     * 参数拼装
     */
    private fun argsArrayToString(paramsArray: Array<Any?>): String {
        val params = StringBuilder()
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString()
        }
        for (o in paramsArray) {
            if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                if (params.isNotEmpty()) {
                    params.append(" ")
                }
                params.append(JsonUtils.toJsonString(o!!))
            }
        }
        return params.toString()
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    private fun isFilterObject(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        val clazz = o.javaClass
        if (clazz.isArray) {
            return MultipartFile::class.java.isAssignableFrom(clazz.componentType)
        } else if (Collection::class.java.isAssignableFrom(clazz)) {
            val collection = o as Collection<*>
            for (value in collection) {
                if (value is MultipartFile) {
                    return true
                }
            }
        } else if (Map::class.java.isAssignableFrom(clazz)) {
            val map = o as Map<*, *>
            for (value in map.values) {
                if (value is MultipartFile) {
                    return true
                }
            }
        }
        return o is MultipartFile || o is HttpServletRequest || o is HttpServletResponse || o is BindingResult
    }
}
