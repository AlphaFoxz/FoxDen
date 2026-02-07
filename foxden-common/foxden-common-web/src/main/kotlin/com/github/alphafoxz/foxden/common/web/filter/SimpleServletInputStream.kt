package com.github.alphafoxz.foxden.common.web.filter

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * 简单的 ServletInputStream 实现
 */
class SimpleServletInputStream(private val bytes: ByteArray) : ServletInputStream() {
    private val inputStream = ByteArrayInputStream(bytes)

    @Throws(IOException::class)
    override fun read(): Int {
        return inputStream.read()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return inputStream.read(b)
    }

    override fun isFinished(): Boolean {
        try {
            return inputStream.available() <= 0
        } catch (e: IOException) {
            return true
        }
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setReadListener(readListener: ReadListener?) {
        // Do nothing
    }
}
