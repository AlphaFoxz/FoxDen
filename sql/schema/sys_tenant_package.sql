DROP TABLE IF EXISTS "sys_tenant_package" CASCADE;

CREATE TABLE "sys_tenant_package" (
    "package_name" TEXT NOT NULL,
    "menu_ids" TEXT DEFAULT NULL,
    "remark" TEXT DEFAULT NULL,
    "menu_check_strictly" BIT(1) DEFAULT NULL,
    "status" TEXT NOT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "create_dept" BIGINT NOT NULL,
    "create_by" BIGINT NOT NULL,
    "create_time" TIMESTAMPTZ(0) NOT NULL,
    "update_by" BIGINT NOT NULL,
    "update_time" TIMESTAMPTZ(0) NOT NULL,
    "tenant_id" TEXT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "sys_tenant_package" IS '租户套餐表';
COMMENT ON COLUMN "sys_tenant_package"."package_name" IS '套餐名称';
COMMENT ON COLUMN "sys_tenant_package"."menu_ids" IS '关联菜单id';
COMMENT ON COLUMN "sys_tenant_package"."remark" IS '备注';
COMMENT ON COLUMN "sys_tenant_package"."menu_check_strictly" IS '菜单树选择项是否关联显示';
COMMENT ON COLUMN "sys_tenant_package"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "sys_tenant_package"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_tenant_package"."id" IS '主键';
COMMENT ON COLUMN "sys_tenant_package"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_tenant_package"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_tenant_package"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_tenant_package"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_tenant_package"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_tenant_package"."tenant_id" IS '租户编号';

