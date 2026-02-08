package com.github.alphafoxz.foxden.common.encrypt.filter

import cn.hutool.core.io.IoUtil
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.encrypt.utils.EncryptUtils
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.http.MediaType
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * 解密请求参数工具类
 *
 * @author wdhcr
 */
class DecryptRequestBodyWrapper(
    request: HttpServletRequest,
    privateKey: String,
    headerFlag: String
) : HttpServletRequestWrapper(request) {

    private val body: ByteArray

    init {
        // 获取 AES 密码 采用 RSA 加密
        val headerRsa = request.getHeader(headerFlag)
        val decryptAes = EncryptUtils.decryptByRsa(headerRsa, privateKey)
        // 解密 AES 密码
        val aesPassword = EncryptUtils.decryptByBase64(decryptAes)
        super.setCharacterEncoding(Constants.UTF8)
        val readBytes = IoUtil.readBytes(super.getInputStream(), false)
        val requestBody = String(readBytes, StandardCharsets.UTF_8)
        // 解密 body 采用 AES 加密
        val decryptBody = EncryptUtils.decryptByAes(requestBody, aesPassword)
        body = decryptBody.toByteArray(StandardCharsets.UTF_8)
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(getInputStream()))
    }

    override fun getContentLength(): Int {
        return body.size
    }

    override fun getContentLengthLong(): Long {
        return body.size.toLong()
    }

    override fun getContentType(): String {
        return MediaType.APPLICATION_JSON_VALUE
    }

    override fun getInputStream(): ServletInputStream {
        val bais = ByteArrayInputStream(body)
        return object : ServletInputStream() {
            override fun read(): Int = bais.read()

            override fun available(): Int = body.size

            override fun isFinished(): Boolean = false

            override fun isReady(): Boolean = false

            override fun setReadListener(readListener: ReadListener?) {
                // Not implemented
            }
        }
    }
}
