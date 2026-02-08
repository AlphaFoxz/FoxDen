DROP TABLE IF EXISTS "sys_dict_data" CASCADE;

CREATE TABLE "sys_dict_data" (
    "dict_sort" INT DEFAULT NULL,
    "dict_label" TEXT NOT NULL,
    "dict_value" TEXT NOT NULL,
    "dict_type" TEXT NOT NULL,
    "css_class" TEXT DEFAULT NULL,
    "list_class" TEXT DEFAULT NULL,
    "default" BOOLEAN DEFAULT NULL,
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

COMMENT ON TABLE "sys_dict_data" IS '字典数据表';
COMMENT ON COLUMN "sys_dict_data"."dict_sort" IS '字典排序';
COMMENT ON COLUMN "sys_dict_data"."dict_label" IS '字典标签';
COMMENT ON COLUMN "sys_dict_data"."dict_value" IS '字典键值';
COMMENT ON COLUMN "sys_dict_data"."dict_type" IS '字典类型';
COMMENT ON COLUMN "sys_dict_data"."css_class" IS '样式属性（其他样式扩展）';
COMMENT ON COLUMN "sys_dict_data"."list_class" IS '表格回显样式';
COMMENT ON COLUMN "sys_dict_data"."default" IS '是否默认';
COMMENT ON COLUMN "sys_dict_data"."remark" IS '备注';
COMMENT ON COLUMN "sys_dict_data"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_dict_data"."id" IS '主键';
COMMENT ON COLUMN "sys_dict_data"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_dict_data"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_dict_data"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict_data"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_dict_data"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict_data"."tenant_id" IS '租户编号';

