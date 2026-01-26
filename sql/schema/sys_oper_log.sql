DROP TABLE IF EXISTS "sys_oper_log" CASCADE;

CREATE TABLE "sys_oper_log" (
    "title" TEXT DEFAULT NULL,
    "business_type" INT DEFAULT NULL,
    "method" TEXT DEFAULT NULL,
    "request_method" TEXT DEFAULT NULL,
    "operator_type" INT DEFAULT NULL,
    "oper_name" TEXT DEFAULT NULL,
    "dept_name" TEXT DEFAULT NULL,
    "oper_url" TEXT DEFAULT NULL,
    "oper_ip" TEXT DEFAULT NULL,
    "oper_location" TEXT DEFAULT NULL,
    "oper_param" TEXT DEFAULT NULL,
    "json_result" TEXT DEFAULT NULL,
    "status" INT DEFAULT NULL,
    "error_msg" TEXT DEFAULT NULL,
    "oper_time" TIMESTAMP(6) NOT NULL,
    "cost_time" BIGINT DEFAULT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "tenant_id" TEXT NOT NULL,
    PRIMARY KEY ("id")
);

CREATE INDEX "idx_sys_oper_log_bt" ON "sys_oper_log" ("business_type");
CREATE INDEX "idx_sys_oper_log_s" ON "sys_oper_log" ("status");
CREATE INDEX "idx_sys_oper_log_ot" ON "sys_oper_log" ("oper_time");

COMMENT ON TABLE "sys_oper_log" IS '操作日志记录';
COMMENT ON COLUMN "sys_oper_log"."title" IS '模块标题';
COMMENT ON COLUMN "sys_oper_log"."business_type" IS '业务类型（0其它 1新增 2修改 3删除）';
COMMENT ON COLUMN "sys_oper_log"."method" IS '方法名称';
COMMENT ON COLUMN "sys_oper_log"."request_method" IS '请求方式';
COMMENT ON COLUMN "sys_oper_log"."operator_type" IS '操作类别（0其它 1后台用户 2手机端用户）';
COMMENT ON COLUMN "sys_oper_log"."oper_name" IS '操作人员';
COMMENT ON COLUMN "sys_oper_log"."dept_name" IS '部门名称';
COMMENT ON COLUMN "sys_oper_log"."oper_url" IS '请求URL';
COMMENT ON COLUMN "sys_oper_log"."oper_ip" IS '主机地址';
COMMENT ON COLUMN "sys_oper_log"."oper_location" IS '操作地点';
COMMENT ON COLUMN "sys_oper_log"."oper_param" IS '请求参数';
COMMENT ON COLUMN "sys_oper_log"."json_result" IS '返回参数';
COMMENT ON COLUMN "sys_oper_log"."status" IS '操作状态（0正常 1异常）';
COMMENT ON COLUMN "sys_oper_log"."error_msg" IS '错误消息';
COMMENT ON COLUMN "sys_oper_log"."oper_time" IS '操作时间';
COMMENT ON COLUMN "sys_oper_log"."cost_time" IS '消耗时间';
COMMENT ON COLUMN "sys_oper_log"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_oper_log"."id" IS '主键';
COMMENT ON COLUMN "sys_oper_log"."tenant_id" IS '租户编号';

