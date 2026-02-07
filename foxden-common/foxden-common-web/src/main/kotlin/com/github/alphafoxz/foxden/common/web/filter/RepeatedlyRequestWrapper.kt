package com.github.alphafoxz.foxden.common.web.filter

import cn.hutool.core.io.IoUtil
import com.github.alphafoxz.foxden.common.core.constant.Constants
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 构建可重复读取inputStream的request
 */
class RepeatedlyRequestWrapper(request: HttpServletRequest, response: ServletResponse) : HttpServletRequestWrapper(request) {
    private val body: ByteArray

    init {
        request.characterEncoding = Constants.UTF8
        response.characterEncoding = Constants.UTF8
        body = IoUtil.readBytes(request.inputStream, false)
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(getInputStream()))
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        return SimpleServletInputStream(body)
    }
}
