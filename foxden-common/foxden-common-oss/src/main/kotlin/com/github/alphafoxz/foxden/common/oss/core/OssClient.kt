package com.github.alphafoxz.foxden.common.oss.core

import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.file.FileUtils
import com.github.alphafoxz.foxden.common.oss.constant.OssConstant
import com.github.alphafoxz.foxden.common.oss.entity.UploadResult
import com.github.alphafoxz.foxden.common.oss.enums.AccessPolicyType
import com.github.alphafoxz.foxden.common.oss.exception.OssException
import com.github.alphafoxz.foxden.common.oss.properties.OssProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * S3 存储协议客户端
 * 支持阿里云、腾讯云、七牛云、MinIO 等兼容 S3 协议的云厂商
 */
class OssClient(
    /**
     * 服务商配置key
     */
    val configKey: String,

    /**
     * 配置属性
     */
    private val properties: OssProperties
) {
    private val log = LoggerFactory.getLogger(OssClient::class.java)

    /**
     * Amazon S3 异步客户端
     */
    private val client: S3AsyncClient

    /**
     * S3 预签名 URL 生成器
     */
    private val presigner: S3Presigner

    init {
        try {
            // 创建认证信息
            val credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
            )

            val endpoint = properties.endpoint ?: throw OssException("endpoint不能为空")

            // 判断是否为 MinIO（非云服务商需要使用路径样式访问）
            val isStyle = !OssConstant.CLOUD_SERVICE.any { endpoint.contains(it) }

            // 获取region配置
            val region = properties.region ?: "us-east-1"

            // 创建 S3 异步客户端
            client = S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .forcePathStyle(isStyle)
                .httpClient(
                    NettyNioAsyncHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(60))
                        .build()
                )
                .build()

            // 创建预签名器
            val config = S3Configuration.builder()
                .chunkedEncodingEnabled(false)
                .pathStyleAccessEnabled(isStyle)
                .build()

            val domain = properties.domain
            val presignerEndpoint = if (StringUtils.isNotEmpty(domain)) {
                URI.create(domain)
            } else {
                URI.create(endpoint)
            }

            presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .endpointOverride(presignerEndpoint)
                .serviceConfiguration(config)
                .build()

        } catch (e: Exception) {
            if (e is OssException) {
                throw e
            }
            throw OssException("配置错误! 请检查系统配置:[${e.message}]")
        }
    }

    /**
     * 上传文件
     */
    fun upload(filePath: Path, key: String, md5Digest: String?, contentType: String?): UploadResult {
        try {
            val putRequest = PutObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(key)
                .contentMD5(md5Digest)
                .contentType(contentType)
                .build()

            val uploadResult = client.putObject(putRequest, filePath).get()
            val eTag = uploadResult.eTag()

            return UploadResult(
                url = "${getDomain()}/${StringUtils.SLASH}$key",
                filename = key,
                eTag = eTag ?: ""
            )
        } catch (e: Exception) {
            throw OssException("上传文件失败，请检查配置信息:[${e.message}]")
        } finally {
            try {
                Files.deleteIfExists(filePath)
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * 上传 InputStream
     */
    fun upload(inputStream: InputStream, key: String, length: Long, contentType: String?): UploadResult {
        val bais = if (inputStream !is ByteArrayInputStream) {
            ByteArrayInputStream(IoUtil.readBytes(inputStream))
        } else {
            inputStream
        }

        return try {
            val fileContent = IoUtil.readBytes(bais)

            val putRequest = PutObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(key)
                .contentType(contentType)
                .build()

            val body = BlockingInputStreamAsyncRequestBody.builder()
                .contentLength(length)
                .build()

            body.writeInputStream(ByteArrayInputStream(fileContent))

            val uploadResult = client.putObject(putRequest, body).get()
            val eTag = uploadResult.eTag()

            UploadResult(
                url = "${getUrl()}/${StringUtils.SLASH}$key",
                filename = key,
                eTag = eTag ?: ""
            )
        } catch (e: Exception) {
            throw OssException("上传文件失败，请检查配置信息:[${e.message}]")
        }
    }

    /**
     * 下载文件到临时目录
     */
    fun fileDownload(path: String): Path {
        val tempFilePath = Files.createTempFile("oss-download", null)
        val getRequest = GetObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(path)
            .build()

        val response = client.getObject(getRequest, tempFilePath).get()
        return tempFilePath
    }

    /**
     * 删除文件
     */
    fun delete(path: String) {
        try {
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(path)
                .build()
            client.deleteObject(deleteRequest).join()
        } catch (e: Exception) {
            throw OssException("删除文件失败，请检查配置信息:[${e.message}]")
        }
    }

    /**
     * 获取私有访问链接
     */
    fun getPrivateUrl(objectKey: String, expiredTime: Duration): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(objectKey)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiredTime)
            .getObjectRequest(getObjectRequest)
            .build()

        val url = presigner.presignGetObject(presignRequest).url()
        return url.toString()
    }

    /**
     * 上传字节数组
     */
    fun uploadSuffix(data: ByteArray, suffix: String, contentType: String?): UploadResult {
        return upload(
            ByteArrayInputStream(data),
            getPath(properties.prefix, suffix),
            data.size.toLong(),
            contentType
        )
    }

    /**
     * 上传文件
     */
    fun uploadSuffix(file: java.io.File, suffix: String): UploadResult {
        return upload(
            file.toPath(),
            getPath(properties.prefix, suffix),
            null,
            getContentType(suffix)
        )
    }

    /**
     * 根据文件后缀获取Content-Type
     */
    private fun getContentType(suffix: String): String {
        return when (suffix.lowercase()) {
            ".jpg", ".jpeg" -> "image/jpeg"
            ".png" -> "image/png"
            ".gif" -> "image/gif"
            ".bmp" -> "image/bmp"
            ".webp" -> "image/webp"
            ".pdf" -> "application/pdf"
            ".doc" -> "application/msword"
            ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ".xls" -> "application/vnd.ms-excel"
            ".xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ".txt" -> "text/plain"
            ".html", ".htm" -> "text/html"
            ".xml" -> "text/xml"
            ".json" -> "application/json"
            ".zip" -> "application/zip"
            ".rar" -> "application/x-rar-compressed"
            ".7z" -> "application/x-7z-compressed"
            ".tar" -> "application/x-tar"
            ".gz" -> "application/gzip"
            ".mp3" -> "audio/mpeg"
            ".mp4" -> "video/mp4"
            ".avi" -> "video/x-msvideo"
            ".mov" -> "video/quicktime"
            ".wmv" -> "video/x-ms-wmv"
            else -> "application/octet-stream"
        }
    }

    /**
     * 获取文件输入流
     */
    fun getObjectContent(path: String): InputStream {
        val tempFilePath = fileDownload(path)
        val inputStream = Files.newInputStream(tempFilePath)
        try {
            Files.deleteIfExists(tempFilePath)
        } catch (ignored: Exception) {
        }
        return inputStream
    }

    /**
     * 获取端点URL
     */
    fun getEndpoint(): String {
        val header = getIsHttps()
        return header + properties.endpoint
    }

    /**
     * 获取域名URL
     */
    fun getDomain(): String {
        val domain = properties.domain
        val endpoint = properties.endpoint ?: return ""
        val header = getIsHttps()

        if (OssConstant.CLOUD_SERVICE.any { endpoint.contains(it) }) {
            return if (StringUtils.isNotEmpty(domain)) {
                header + (domain ?: "")
            } else {
                header + endpoint
            }
        }

        if (StringUtils.isNotEmpty(domain)) {
            val domainValue = domain ?: ""
            return if (domainValue.startsWith(Constants.HTTPS) || domainValue.startsWith(Constants.HTTP)) {
                domainValue
            } else {
                header + domainValue
            }
        }

        return header + endpoint
    }

    /**
     * 获取访问URL
     */
    fun getUrl(): String {
        val domain = properties.domain
        val endpoint = properties.endpoint ?: return ""
        val header = getIsHttps()

        if (OssConstant.CLOUD_SERVICE.any { endpoint.contains(it) }) {
            return header + (if (StringUtils.isNotEmpty(domain)) {
                domain ?: ""
            } else {
                properties.bucketName + "." + endpoint
            })
        }

        if (StringUtils.isNotEmpty(domain)) {
            val domainValue = domain ?: ""
            return if (domainValue.startsWith(Constants.HTTPS) || domainValue.startsWith(Constants.HTTP)) {
                "$domainValue/${StringUtils.SLASH}${properties.bucketName}"
            } else {
                "$header$domainValue/${StringUtils.SLASH}${properties.bucketName}"
            }
        }

        return "$header$endpoint/${StringUtils.SLASH}${properties.bucketName}"
    }

    /**
     * 生成文件路径
     */
    fun getPath(prefix: String?, suffix: String): String {
        val uuid = IdUtil.fastSimpleUUID()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePath = LocalDate.now().format(formatter)
        val path = if (StringUtils.isNotEmpty(prefix)) {
            "$prefix${StringUtils.SLASH}$datePath${StringUtils.SLASH}$uuid"
        } else {
            "$datePath${StringUtils.SLASH}$uuid"
        }
        return path + suffix
    }

    /**
     * 移除基础URL
     */
    fun removeBaseUrl(path: String): String {
        return path.replace("${getUrl()}${StringUtils.SLASH}", "")
    }

    /**
     * 获取协议头
     */
    fun getIsHttps(): String {
        return if (OssConstant.IS_HTTPS == properties.isHttps) {
            Constants.HTTPS
        } else {
            Constants.HTTP
        }
    }

    /**
     * 获取AWS区域
     */
    fun of(): Region {
        val region = properties.region
        return if (StringUtils.isNotEmpty(region)) {
            Region.of(region)
        } else {
            Region.US_EAST_1
        }
    }

    /**
     * 获取访问策略类型
     */
    fun getAccessPolicy(): AccessPolicyType {
        return AccessPolicyType.getByType(properties.accessPolicy ?: "0")
    }

    /**
     * 检查配置是否相同
     */
    fun checkPropertiesSame(other: OssProperties): Boolean {
        return properties == other
    }
}
