package com.github.alphafoxz.foxden.common.core.service

import com.github.alphafoxz.foxden.common.core.domain.dto.OssDTO

/**
 * OSS 服务接口
 *
 * @author Lion Li
 */
interface OssService {

    /**
     * 根据ossId字符串获取对应的OssDTO列表
     *
     * @param ossIds 以逗号分隔的ossId字符串
     * @return OssDTO列表
     */
    fun selectByIds(ossIds: String): List<OssDTO>
}
