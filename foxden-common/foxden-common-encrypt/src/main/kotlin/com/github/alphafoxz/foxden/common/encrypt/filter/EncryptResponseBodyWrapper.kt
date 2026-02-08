package com.github.alphafoxz.foxden.common.encrypt.filter

import cn.hutool.core.util.RandomUtil
import com.github.alphafoxz.foxden.common.encrypt.utils.EncryptUtils
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

/**
 * 加密响应参数包装类
 *
 * @author Michelle.Chung
 */
class EncryptResponseBodyWrapper(
    response: HttpServletResponse
) : HttpServletResponseWrapper(response) {

    private val byteArrayOutputStream = ByteArrayOutputStream()
    private val servletOutputStream = super.getOutputStream()
    private val printWriter: PrintWriter = PrintWriter(
        OutputStreamWriter(byteArrayOutputStream)
    )

    override fun getWriter(): PrintWriter {
        return printWriter
    }

    override fun flushBuffer() {
        servletOutputStream.flush()
        printWriter.flush()
    }

    override fun reset() {
        byteArrayOutputStream.reset()
    }

    @Throws(IOException::class)
    fun getResponseData(): ByteArray {
        flushBuffer()
        return byteArrayOutputStream.toByteArray()
    }

    @Throws(IOException::class)
    fun getContent(): String {
        flushBuffer()
        return byteArrayOutputStream.toString()
    }

    /**
     * 获取加密内容
     *
     * @param servletResponse response
     * @param publicKey       RSA公钥 (用于加密 AES 秘钥)
     * @param headerFlag      请求头标志
     * @return 加密内容
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getEncryptContent(
        servletResponse: HttpServletResponse,
        publicKey: String,
        headerFlag: String
    ): String {
        // 生成秘钥
        val aesPassword = RandomUtil.randomString(32)
        // 秘钥使用 Base64 编码
        val encryptAes = EncryptUtils.encryptByBase64(aesPassword)
        // Rsa 公钥加密 Base64 编码
        val encryptPassword = EncryptUtils.encryptByRsa(encryptAes, publicKey)

        // 设置响应头
        // vue版本需要设置
        servletResponse.addHeader("Access-Control-Expose-Headers", headerFlag)
        servletResponse.setHeader("Access-Control-Allow-Origin", "*")
        servletResponse.setHeader("Access-Control-Allow-Methods", "*")
        servletResponse.setHeader(headerFlag, encryptPassword)
        servletResponse.characterEncoding = "UTF-8"

        // 获取原始内容
        val originalBody = getContent()
        // 对内容进行加密
        return EncryptUtils.encryptByAes(originalBody, aesPassword)
    }

    override fun getOutputStream(): ServletOutputStream {
        return object : ServletOutputStream() {
            override fun isReady(): Boolean = false

            override fun setWriteListener(writeListener: WriteListener?) {
                // Not implemented
            }

            override fun write(b: Int) {
                byteArrayOutputStream.write(b)
            }

            override fun write(b: ByteArray) {
                byteArrayOutputStream.write(b)
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                byteArrayOutputStream.write(b, off, len)
            }
        }
    }
}
