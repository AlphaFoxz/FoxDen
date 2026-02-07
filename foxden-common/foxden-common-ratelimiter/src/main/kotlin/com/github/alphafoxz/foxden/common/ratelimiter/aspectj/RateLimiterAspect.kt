package com.github.alphafoxz.foxden.common.ratelimiter.aspectj

import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.common.ratelimiter.annotation.RateLimiter
import com.github.alphafoxz.foxden.common.ratelimiter.enums.LimitType
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RateType
import org.slf4j.LoggerFactory
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer
import org.springframework.expression.Expression
import org.springframework.expression.ParserContext
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.lang.reflect.Method

/**
 * 限流处理
 *
 * @author Lion Li
 */
@Aspect
class RateLimiterAspect {
    private val log = LoggerFactory.getLogger(RateLimiterAspect::class.java)

    /**
     * 定义spel表达式解析器
     */
    private val parser = SpelExpressionParser()

    /**
     * 定义spel解析模版
     */
    private val parserContext: ParserContext = TemplateParserContext()

    /**
     * 方法参数解析器
     */
    private val pnd: ParameterNameDiscoverer = DefaultParameterNameDiscoverer()

    @Before("@annotation(rateLimiter)")
    fun doBefore(point: JoinPoint, rateLimiter: RateLimiter) {
        val time = rateLimiter.time
        val count = rateLimiter.count
        val timeout = rateLimiter.timeout
        try {
            val combineKey = getCombineKey(rateLimiter, point)
            val rateType = RateType.OVERALL
            if (rateLimiter.limitType == LimitType.CLUSTER) {
                // For cluster, use PER_CLIENT
            }
            val number = RedisUtils.rateLimiter(combineKey, rateType, count, time, timeout)
            if (number == -1L) {
                var message = rateLimiter.message
                if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                    message = MessageUtils.message(StringUtils.substring(message, 1, message.length - 1)) ?: message
                }
                throw ServiceException(message)
            }
            log.info("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", count, number, combineKey)
        } catch (e: Exception) {
            if (e is ServiceException) {
                throw e
            } else {
                throw RuntimeException("服务器限流异常，请稍候再试", e)
            }
        }
    }

    private fun getCombineKey(rateLimiter: RateLimiter, point: JoinPoint): String {
        var key = rateLimiter.key
        // 判断 key 不为空 和 不是表达式
        if (StringUtils.isNotBlank(key) && StringUtils.containsAny(key, "#")) {
            val signature = point.signature as MethodSignature
            val targetMethod: Method = signature.method
            val args = point.args
            val context = MethodBasedEvaluationContext(point.target, targetMethod, args, pnd)
            context.setBeanResolver(BeanFactoryResolver(SpringUtils.getBeanFactory()))
            val expression: Expression
            if (StringUtils.startsWith(key, parserContext.expressionPrefix)
                && StringUtils.endsWith(key, parserContext.expressionSuffix)) {
                expression = parser.parseExpression(key, parserContext)
            } else {
                expression = parser.parseExpression(key)
            }
            key = expression.getValue(context, String::class.java) ?: key
        }
        val stringBuffer = StringBuilder(GlobalConstants.RATE_LIMIT_KEY)
        stringBuffer.append(ServletUtils.getRequest().requestURI).append(":")
        if (rateLimiter.limitType == LimitType.IP) {
            // 获取请求ip
            stringBuffer.append(ServletUtils.getClientIP()).append(":")
        } else if (rateLimiter.limitType == LimitType.CLUSTER) {
            // 获取客户端实例id
            stringBuffer.append(RedisUtils.getClient().id).append(":")
        }
        return stringBuffer.append(key).toString()
    }
}
