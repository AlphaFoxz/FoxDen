DROP TABLE IF EXISTS "sys_user" CASCADE;

CREATE TABLE "sys_user" (
    "dept_id" BIGINT DEFAULT NULL,
    "user_name" TEXT NOT NULL,
    "nick_name" TEXT NOT NULL,
    "user_type" TEXT DEFAULT NULL,
    "email" TEXT DEFAULT NULL,
    "phonenumber" TEXT DEFAULT NULL,
    "sex" TEXT DEFAULT NULL,
    "avatar" BIGINT DEFAULT NULL,
    "password" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "login_ip" TEXT DEFAULT NULL,
    "login_date" TIMESTAMP(6) DEFAULT NULL,
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

COMMENT ON TABLE "sys_user" IS '用户信息表';
COMMENT ON COLUMN "sys_user"."dept_id" IS '部门ID';
COMMENT ON COLUMN "sys_user"."user_name" IS '用户账号';
COMMENT ON COLUMN "sys_user"."nick_name" IS '用户昵称';
COMMENT ON COLUMN "sys_user"."user_type" IS '用户类型（sys_user系统用户）';
COMMENT ON COLUMN "sys_user"."email" IS '用户邮箱';
COMMENT ON COLUMN "sys_user"."phonenumber" IS '手机号码';
COMMENT ON COLUMN "sys_user"."sex" IS '用户性别（0男 1女 2未知）';
COMMENT ON COLUMN "sys_user"."avatar" IS '头像地址';
COMMENT ON COLUMN "sys_user"."password" IS '密码';
COMMENT ON COLUMN "sys_user"."status" IS '帐号状态（0正常 1停用）';
COMMENT ON COLUMN "sys_user"."login_ip" IS '最后登陆IP';
COMMENT ON COLUMN "sys_user"."login_date" IS '最后登陆时间';
COMMENT ON COLUMN "sys_user"."remark" IS '备注';
COMMENT ON COLUMN "sys_user"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_user"."id" IS '主键';
COMMENT ON COLUMN "sys_user"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_user"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_user"."tenant_id" IS '租户编号';

