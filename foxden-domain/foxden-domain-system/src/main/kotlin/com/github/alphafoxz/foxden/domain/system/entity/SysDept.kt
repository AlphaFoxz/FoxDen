package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.*

/**
 * 部门表 sys_dept
 */
@Entity
@Table(name = "sys_dept")
interface SysDept : CommDelFlag, CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @Column(name = "dept_id")
    @Id
    @JsonConverter(LongToStringConverter::class)
    val id: Long

    /**
     * 父部门ID
     */
    @IdView
    val parentId: Long?

    /**
     * 部门名称
     */
    val deptName: String

    /**
     * 部门类别编码
     */
    val deptCategory: String?

    /**
     * 显示顺序
     */
    val orderNum: Int?

    /**
     * 负责人
     */
    val leader: Long?

    /**
     * 联系电话
     */
    val phone: String?

    /**
     * 邮箱
     */
    val email: String?

    /**
     * 部门状态:0正常,1停用
     */
    val status: String?

    /**
     * 祖级列表
     */
    val ancestors: String?

    /**
     * 父部门
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: SysDept?

    /**
     * 子部门
     */
    @OneToMany(mappedBy = "parent")
    val children: List<SysDept>

    /**
     * 用户
     */
    @OneToMany(mappedBy = "dept")
    val users: List<SysUser>
}
