package com.github.alphafoxz.foxden.common.core.utils.file

import cn.hutool.core.io.FileUtil
import jakarta.servlet.http.HttpServletResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 文件处理工具类
 */
object FileUtils : FileUtil() {

    /**
     * 下载文件名重新编码
     */
    fun setAttachmentResponseHeader(response: HttpServletResponse, realFileName: String) {
        val percentEncodedFileName = percentEncode(realFileName)
        val contentDispositionValue = "attachment; filename=$percentEncodedFileName;filename*=utf-8''$percentEncodedFileName"
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename")
        response.setHeader("Content-disposition", contentDispositionValue)
        response.setHeader("download-filename", percentEncodedFileName)
    }

    /**
     * 百分号编码工具方法
     */
    fun percentEncode(s: String): String {
        val encode = URLEncoder.encode(s, StandardCharsets.UTF_8)
        return encode.replace("\\+".toRegex(), "%20")
    }
}
