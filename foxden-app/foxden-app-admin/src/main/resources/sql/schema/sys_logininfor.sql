DROP TABLE IF EXISTS "sys_logininfor" CASCADE;

CREATE TABLE "sys_logininfor" (
    "user_name" TEXT DEFAULT NULL,
    "client_key" TEXT DEFAULT NULL,
    "device_type" TEXT DEFAULT NULL,
    "ipaddr" TEXT DEFAULT NULL,
    "login_location" TEXT DEFAULT NULL,
    "browser" TEXT DEFAULT NULL,
    "os" TEXT DEFAULT NULL,
    "status" TEXT NOT NULL,
    "msg" TEXT DEFAULT NULL,
    "login_time" TIMESTAMP(6) NOT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "tenant_id" TEXT NOT NULL,
    PRIMARY KEY ("id")
);

CREATE INDEX "idx_sys_logininfor_s" ON "sys_logininfor" ("status");
CREATE INDEX "idx_sys_logininfor_lt" ON "sys_logininfor" ("login_time");

COMMENT ON TABLE "sys_logininfor" IS '系统访问记录';
COMMENT ON COLUMN "sys_logininfor"."user_name" IS '用户账号';
COMMENT ON COLUMN "sys_logininfor"."client_key" IS '客户端';
COMMENT ON COLUMN "sys_logininfor"."device_type" IS '设备类型';
COMMENT ON COLUMN "sys_logininfor"."ipaddr" IS '登录IP地址';
COMMENT ON COLUMN "sys_logininfor"."login_location" IS '登录地点';
COMMENT ON COLUMN "sys_logininfor"."browser" IS '浏览器类型';
COMMENT ON COLUMN "sys_logininfor"."os" IS '操作系统';
COMMENT ON COLUMN "sys_logininfor"."status" IS '登录状态（0成功 1失败）';
COMMENT ON COLUMN "sys_logininfor"."msg" IS '提示消息';
COMMENT ON COLUMN "sys_logininfor"."login_time" IS '访问时间';
COMMENT ON COLUMN "sys_logininfor"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_logininfor"."id" IS '主键';
COMMENT ON COLUMN "sys_logininfor"."tenant_id" IS '租户编号';

