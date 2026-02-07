package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysOssService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysOssBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo

/**
 * Oss 业务层处理
 */
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
@Service
class SysOssServiceImpl(): SysOssService {

    override fun queryPageList(bo: SysOssBo, pageQuery: com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery): com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo<SysOssVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun queryById(ossId: Long): SysOssVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun upload(file: MultipartFile): SysOssVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun upload(fileName: String, content: InputStream): SysOssVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun upload(fileName: String, content: ByteArray): SysOssVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectByIds(ossId: Long): SysOssVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun deleteWithValidByIds(ossIds: Array<Long>): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun getPresignedObjectUrl(ossId: Long): String? {
        // TODO: 实现业务逻辑
        return null
    }
}
