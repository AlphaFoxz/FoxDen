DROP TABLE IF EXISTS "sys_dept" CASCADE;

CREATE TABLE "sys_dept" (
    "parent_id" BIGINT DEFAULT NULL,
    "ancestors" TEXT DEFAULT NULL,
    "dept_name" TEXT NOT NULL,
    "dept_category" TEXT DEFAULT NULL,
    "order_num" INT DEFAULT NULL,
    "leader" BIGINT DEFAULT NULL,
    "phone" TEXT DEFAULT NULL,
    "email" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_dept" IS '部门表';
COMMENT ON COLUMN "sys_dept"."parent_id" IS '父部门ID';
COMMENT ON COLUMN "sys_dept"."ancestors" IS '祖级列表';
COMMENT ON COLUMN "sys_dept"."dept_name" IS '部门名称';
COMMENT ON COLUMN "sys_dept"."dept_category" IS '部门类别编码';
COMMENT ON COLUMN "sys_dept"."order_num" IS '显示顺序';
COMMENT ON COLUMN "sys_dept"."leader" IS '负责人';
COMMENT ON COLUMN "sys_dept"."phone" IS '联系电话';
COMMENT ON COLUMN "sys_dept"."email" IS '邮箱';
COMMENT ON COLUMN "sys_dept"."status" IS '部门状态（0正常 1停用）';
COMMENT ON COLUMN "sys_dept"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_dept"."id" IS '主键';
COMMENT ON COLUMN "sys_dept"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_dept"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_dept"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dept"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_dept"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dept"."tenant_id" IS '租户编号';

