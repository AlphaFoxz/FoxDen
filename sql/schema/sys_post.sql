DROP TABLE IF EXISTS "sys_post" CASCADE;

CREATE TABLE "sys_post" (
    "dept_id" BIGINT NOT NULL,
    "post_code" TEXT NOT NULL,
    "post_category" TEXT DEFAULT NULL,
    "post_name" TEXT NOT NULL,
    "post_sort" INT DEFAULT NULL,
    "status" TEXT NOT NULL,
    "remark" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_post" IS '岗位信息表';
COMMENT ON COLUMN "sys_post"."dept_id" IS '部门id';
COMMENT ON COLUMN "sys_post"."post_code" IS '岗位编码';
COMMENT ON COLUMN "sys_post"."post_category" IS '岗位类别编码';
COMMENT ON COLUMN "sys_post"."post_name" IS '岗位名称';
COMMENT ON COLUMN "sys_post"."post_sort" IS '显示顺序';
COMMENT ON COLUMN "sys_post"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "sys_post"."remark" IS '备注';
COMMENT ON COLUMN "sys_post"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_post"."id" IS '主键';
COMMENT ON COLUMN "sys_post"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_post"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_post"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_post"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_post"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_post"."tenant_id" IS '租户编号';

