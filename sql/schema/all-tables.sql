DROP TABLE IF EXISTS "rel_role_dept" CASCADE;
DROP TABLE IF EXISTS "rel_role_menu" CASCADE;
DROP TABLE IF EXISTS "rel_user_post" CASCADE;
DROP TABLE IF EXISTS "rel_user_role" CASCADE;
DROP TABLE IF EXISTS "sys_client" CASCADE;
DROP TABLE IF EXISTS "sys_config" CASCADE;
DROP TABLE IF EXISTS "sys_dept" CASCADE;
DROP TABLE IF EXISTS "sys_dict_data" CASCADE;
DROP TABLE IF EXISTS "sys_dict_type" CASCADE;
DROP TABLE IF EXISTS "sys_logininfor" CASCADE;
DROP TABLE IF EXISTS "sys_menu" CASCADE;
DROP TABLE IF EXISTS "sys_notice" CASCADE;
DROP TABLE IF EXISTS "sys_oper_log" CASCADE;
DROP TABLE IF EXISTS "sys_oss" CASCADE;
DROP TABLE IF EXISTS "sys_oss_config" CASCADE;
DROP TABLE IF EXISTS "sys_post" CASCADE;
DROP TABLE IF EXISTS "sys_role" CASCADE;
DROP TABLE IF EXISTS "sys_social" CASCADE;
DROP TABLE IF EXISTS "sys_tenant" CASCADE;
DROP TABLE IF EXISTS "sys_tenant_package" CASCADE;
DROP TABLE IF EXISTS "sys_user" CASCADE;

CREATE TABLE "rel_role_dept" (
    "id" BIGSERIAL NOT NULL,
    "role_id" BIGINT NOT NULL,
    "dept_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "rel_role_dept" IS '角色和部门关联表';
COMMENT ON COLUMN "rel_role_dept"."id" IS '主键';
COMMENT ON COLUMN "rel_role_dept"."role_id" IS '角色ID';
COMMENT ON COLUMN "rel_role_dept"."dept_id" IS '部门ID';

CREATE TABLE "rel_role_menu" (
    "id" BIGSERIAL NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "rel_role_menu" IS '角色和菜单关联表';
COMMENT ON COLUMN "rel_role_menu"."id" IS '主键';
COMMENT ON COLUMN "rel_role_menu"."role_id" IS '角色ID';
COMMENT ON COLUMN "rel_role_menu"."menu_id" IS '菜单ID';

CREATE TABLE "rel_user_post" (
    "id" BIGSERIAL NOT NULL,
    "user_id" BIGINT NOT NULL,
    "post_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "rel_user_post" IS '用户与岗位关联表';
COMMENT ON COLUMN "rel_user_post"."id" IS '主键';
COMMENT ON COLUMN "rel_user_post"."user_id" IS '用户ID';
COMMENT ON COLUMN "rel_user_post"."post_id" IS '岗位ID';

CREATE TABLE "rel_user_role" (
    "id" BIGSERIAL NOT NULL,
    "user_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "rel_user_role" IS '用户和角色关联表';
COMMENT ON COLUMN "rel_user_role"."id" IS '主键';
COMMENT ON COLUMN "rel_user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "rel_user_role"."role_id" IS '角色ID';

CREATE TABLE "sys_client" (
    "client_id" TEXT NOT NULL,
    "client_key" TEXT NOT NULL,
    "client_secret" TEXT NOT NULL,
    "grant_type" TEXT NOT NULL,
    "device_type" TEXT DEFAULT NULL,
    "active_timeout" INT DEFAULT NULL,
    "timeout" INT DEFAULT NULL,
    "status" TEXT NOT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "create_dept" BIGINT NOT NULL,
    "create_by" BIGINT NOT NULL,
    "create_time" TIMESTAMPTZ(0) NOT NULL,
    "update_by" BIGINT NOT NULL,
    "update_time" TIMESTAMPTZ(0) NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "sys_client" IS '系统授权表';
COMMENT ON COLUMN "sys_client"."client_id" IS '客户端id';
COMMENT ON COLUMN "sys_client"."client_key" IS '客户端key';
COMMENT ON COLUMN "sys_client"."client_secret" IS '客户端秘钥';
COMMENT ON COLUMN "sys_client"."grant_type" IS '授权类型';
COMMENT ON COLUMN "sys_client"."device_type" IS '设备类型';
COMMENT ON COLUMN "sys_client"."active_timeout" IS 'token活跃超时时间';
COMMENT ON COLUMN "sys_client"."timeout" IS 'token固定超时';
COMMENT ON COLUMN "sys_client"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "sys_client"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_client"."id" IS '主键';
COMMENT ON COLUMN "sys_client"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_client"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_client"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_client"."update_time" IS '更新时间';

CREATE TABLE "sys_config" (
    "config_name" TEXT NOT NULL,
    "config_key" TEXT NOT NULL,
    "config_value" TEXT DEFAULT NULL,
    "config_type" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_config" IS '参数配置表';
COMMENT ON COLUMN "sys_config"."config_name" IS '参数名称';
COMMENT ON COLUMN "sys_config"."config_key" IS '参数键名';
COMMENT ON COLUMN "sys_config"."config_value" IS '参数键值';
COMMENT ON COLUMN "sys_config"."config_type" IS '系统内置（Y是 N否）';
COMMENT ON COLUMN "sys_config"."remark" IS '备注';
COMMENT ON COLUMN "sys_config"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_config"."id" IS '主键';
COMMENT ON COLUMN "sys_config"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_config"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_config"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_config"."tenant_id" IS '租户编号';

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

CREATE TABLE "sys_dict_type" (
    "dict_name" TEXT NOT NULL,
    "dict_type" TEXT NOT NULL,
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

CREATE UNIQUE INDEX "idx_sys_dict_type_tenant_type" ON "sys_dict_type" ("dict_type");

COMMENT ON TABLE "sys_dict_type" IS '字典类型表';
COMMENT ON COLUMN "sys_dict_type"."dict_name" IS '字典名称';
COMMENT ON COLUMN "sys_dict_type"."dict_type" IS '字典类型';
COMMENT ON COLUMN "sys_dict_type"."remark" IS '备注';
COMMENT ON COLUMN "sys_dict_type"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_dict_type"."id" IS '主键';
COMMENT ON COLUMN "sys_dict_type"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_dict_type"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_dict_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict_type"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_dict_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict_type"."tenant_id" IS '租户编号';

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

CREATE TABLE "sys_menu" (
    "menu_name" TEXT NOT NULL,
    "parent_id" BIGINT DEFAULT NULL,
    "order_num" INT DEFAULT NULL,
    "path" TEXT DEFAULT NULL,
    "component" TEXT DEFAULT NULL,
    "query_param" TEXT DEFAULT NULL,
    "frame" BOOLEAN DEFAULT NULL,
    "cache" BOOLEAN DEFAULT NULL,
    "menu_type" TEXT NOT NULL,
    "visible" TEXT DEFAULT NULL,
    "status" TEXT NOT NULL,
    "perms" TEXT DEFAULT NULL,
    "icon" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_menu" IS '菜单权限表';
COMMENT ON COLUMN "sys_menu"."menu_name" IS '菜单名称';
COMMENT ON COLUMN "sys_menu"."parent_id" IS '父菜单ID';
COMMENT ON COLUMN "sys_menu"."order_num" IS '显示顺序';
COMMENT ON COLUMN "sys_menu"."path" IS '路由地址';
COMMENT ON COLUMN "sys_menu"."component" IS '组件路径';
COMMENT ON COLUMN "sys_menu"."query_param" IS '路由参数';
COMMENT ON COLUMN "sys_menu"."frame" IS '是否为外链';
COMMENT ON COLUMN "sys_menu"."cache" IS '是否缓存';
COMMENT ON COLUMN "sys_menu"."menu_type" IS '菜单类型（M目录 C菜单 F按钮）';
COMMENT ON COLUMN "sys_menu"."visible" IS '显示状态（0显示 1隐藏）';
COMMENT ON COLUMN "sys_menu"."status" IS '菜单状态（0正常 1停用）';
COMMENT ON COLUMN "sys_menu"."perms" IS '权限标识';
COMMENT ON COLUMN "sys_menu"."icon" IS '菜单图标';
COMMENT ON COLUMN "sys_menu"."remark" IS '备注';
COMMENT ON COLUMN "sys_menu"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_menu"."id" IS '主键';
COMMENT ON COLUMN "sys_menu"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_menu"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_menu"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_menu"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_menu"."tenant_id" IS '租户编号';

CREATE TABLE "sys_notice" (
    "notice_title" TEXT NOT NULL,
    "notice_type" TEXT NOT NULL,
    "notice_content" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_notice" IS '通知公告表';
COMMENT ON COLUMN "sys_notice"."notice_title" IS '公告标题';
COMMENT ON COLUMN "sys_notice"."notice_type" IS '公告类型（1通知 2公告）';
COMMENT ON COLUMN "sys_notice"."notice_content" IS '公告内容';
COMMENT ON COLUMN "sys_notice"."status" IS '公告状态（0正常 1关闭）';
COMMENT ON COLUMN "sys_notice"."remark" IS '备注';
COMMENT ON COLUMN "sys_notice"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_notice"."id" IS '主键';
COMMENT ON COLUMN "sys_notice"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_notice"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_notice"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_notice"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_notice"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_notice"."tenant_id" IS '租户编号';

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

CREATE TABLE "sys_oss" (
    "file_name" TEXT NOT NULL,
    "original_name" TEXT DEFAULT NULL,
    "file_suffix" TEXT DEFAULT NULL,
    "url" TEXT NOT NULL,
    "ext1" TEXT DEFAULT NULL,
    "service" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_oss" IS 'OSS对象存储表';
COMMENT ON COLUMN "sys_oss"."file_name" IS '文件名';
COMMENT ON COLUMN "sys_oss"."original_name" IS '原名';
COMMENT ON COLUMN "sys_oss"."file_suffix" IS '文件后缀名';
COMMENT ON COLUMN "sys_oss"."url" IS 'URL地址';
COMMENT ON COLUMN "sys_oss"."ext1" IS '扩展字段';
COMMENT ON COLUMN "sys_oss"."service" IS '服务商';
COMMENT ON COLUMN "sys_oss"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_oss"."id" IS '主键';
COMMENT ON COLUMN "sys_oss"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_oss"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_oss"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_oss"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_oss"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_oss"."tenant_id" IS '租户编号';

CREATE TABLE "sys_oss_config" (
    "config_key" TEXT NOT NULL,
    "access_key" TEXT NOT NULL,
    "secret_key" TEXT NOT NULL,
    "bucket_name" TEXT NOT NULL,
    "prefix" TEXT DEFAULT NULL,
    "endpoint" TEXT NOT NULL,
    "domain" TEXT DEFAULT NULL,
    "https" BOOLEAN DEFAULT NULL,
    "region" TEXT DEFAULT NULL,
    "access_policy" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "ext1" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_oss_config" IS '对象存储配置表';
COMMENT ON COLUMN "sys_oss_config"."config_key" IS '配置key';
COMMENT ON COLUMN "sys_oss_config"."access_key" IS 'accessKey';
COMMENT ON COLUMN "sys_oss_config"."secret_key" IS '秘钥';
COMMENT ON COLUMN "sys_oss_config"."bucket_name" IS '桶名称';
COMMENT ON COLUMN "sys_oss_config"."prefix" IS '前缀';
COMMENT ON COLUMN "sys_oss_config"."endpoint" IS '访问站点';
COMMENT ON COLUMN "sys_oss_config"."domain" IS '自定义域名';
COMMENT ON COLUMN "sys_oss_config"."https" IS '是否https';
COMMENT ON COLUMN "sys_oss_config"."region" IS '域';
COMMENT ON COLUMN "sys_oss_config"."access_policy" IS '桶权限类型(0=private 1=public 2=custom)';
COMMENT ON COLUMN "sys_oss_config"."status" IS '是否默认（0=是,1=否）';
COMMENT ON COLUMN "sys_oss_config"."ext1" IS '扩展字段';
COMMENT ON COLUMN "sys_oss_config"."remark" IS '备注';
COMMENT ON COLUMN "sys_oss_config"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_oss_config"."id" IS '主键';
COMMENT ON COLUMN "sys_oss_config"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_oss_config"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_oss_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_oss_config"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_oss_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_oss_config"."tenant_id" IS '租户编号';

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

CREATE TABLE "sys_role" (
    "role_name" TEXT NOT NULL,
    "role_key" TEXT NOT NULL,
    "role_sort" INT DEFAULT NULL,
    "data_scope" TEXT DEFAULT NULL,
    "menu_check_strictly" BIT(1) DEFAULT NULL,
    "dept_check_strictly" BIT(1) DEFAULT NULL,
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

COMMENT ON TABLE "sys_role" IS '角色信息表';
COMMENT ON COLUMN "sys_role"."role_name" IS '角色名称';
COMMENT ON COLUMN "sys_role"."role_key" IS '角色权限字符串';
COMMENT ON COLUMN "sys_role"."role_sort" IS '显示顺序';
COMMENT ON COLUMN "sys_role"."data_scope" IS '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）';
COMMENT ON COLUMN "sys_role"."menu_check_strictly" IS '菜单树选择项是否关联显示';
COMMENT ON COLUMN "sys_role"."dept_check_strictly" IS '部门树选择项是否关联显示';
COMMENT ON COLUMN "sys_role"."status" IS '角色状态（0正常 1停用）';
COMMENT ON COLUMN "sys_role"."remark" IS '备注';
COMMENT ON COLUMN "sys_role"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_role"."id" IS '主键';
COMMENT ON COLUMN "sys_role"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_role"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_role"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_role"."tenant_id" IS '租户编号';

CREATE TABLE "sys_social" (
    "user_id" BIGINT NOT NULL,
    "auth_id" TEXT DEFAULT NULL,
    "source" TEXT NOT NULL,
    "open_id" TEXT NOT NULL,
    "user_name" TEXT DEFAULT NULL,
    "nick_name" TEXT DEFAULT NULL,
    "email" TEXT DEFAULT NULL,
    "avatar" TEXT DEFAULT NULL,
    "access_token" TEXT DEFAULT NULL,
    "expire_in" BIGINT DEFAULT NULL,
    "refresh_token" TEXT DEFAULT NULL,
    "access_code" TEXT DEFAULT NULL,
    "union_id" TEXT DEFAULT NULL,
    "scope" TEXT DEFAULT NULL,
    "token_type" TEXT DEFAULT NULL,
    "id_token" TEXT DEFAULT NULL,
    "mac_algorithm" TEXT DEFAULT NULL,
    "mac_key" TEXT DEFAULT NULL,
    "code" TEXT DEFAULT NULL,
    "oauth_token" TEXT DEFAULT NULL,
    "oauth_token_secret" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_social" IS '社会化关系表';
COMMENT ON COLUMN "sys_social"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_social"."auth_id" IS '平台+平台唯一id';
COMMENT ON COLUMN "sys_social"."source" IS '用户来源';
COMMENT ON COLUMN "sys_social"."open_id" IS '平台编号唯一id';
COMMENT ON COLUMN "sys_social"."user_name" IS '登录账号';
COMMENT ON COLUMN "sys_social"."nick_name" IS '用户昵称';
COMMENT ON COLUMN "sys_social"."email" IS '用户邮箱';
COMMENT ON COLUMN "sys_social"."avatar" IS '头像地址';
COMMENT ON COLUMN "sys_social"."access_token" IS '用户的授权令牌';
COMMENT ON COLUMN "sys_social"."expire_in" IS '用户的授权令牌的有效期';
COMMENT ON COLUMN "sys_social"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "sys_social"."access_code" IS '平台的授权信息';
COMMENT ON COLUMN "sys_social"."union_id" IS '用户的 unionid';
COMMENT ON COLUMN "sys_social"."scope" IS '授予的权限';
COMMENT ON COLUMN "sys_social"."token_type" IS '个别平台的授权信息';
COMMENT ON COLUMN "sys_social"."id_token" IS 'id token';
COMMENT ON COLUMN "sys_social"."mac_algorithm" IS '小米平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."mac_key" IS '小米平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."code" IS '用户的授权code';
COMMENT ON COLUMN "sys_social"."oauth_token" IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."oauth_token_secret" IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_social"."id" IS '主键';
COMMENT ON COLUMN "sys_social"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_social"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_social"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_social"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_social"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_social"."tenant_id" IS '租户编号';

CREATE TABLE "sys_tenant" (
    "contact_user_name" TEXT DEFAULT NULL,
    "contact_phone" TEXT DEFAULT NULL,
    "company_name" TEXT NOT NULL,
    "license_number" TEXT DEFAULT NULL,
    "address" TEXT DEFAULT NULL,
    "intro" TEXT DEFAULT NULL,
    "domain" TEXT DEFAULT NULL,
    "remark" TEXT DEFAULT NULL,
    "package_id" BIGINT DEFAULT NULL,
    "expire_time" TIMESTAMP(6) DEFAULT NULL,
    "account_count" INT DEFAULT NULL,
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

COMMENT ON TABLE "sys_tenant" IS '租户表';
COMMENT ON COLUMN "sys_tenant"."contact_user_name" IS '联系人';
COMMENT ON COLUMN "sys_tenant"."contact_phone" IS '联系电话';
COMMENT ON COLUMN "sys_tenant"."company_name" IS '企业名称';
COMMENT ON COLUMN "sys_tenant"."license_number" IS '统一社会信用代码';
COMMENT ON COLUMN "sys_tenant"."address" IS '地址';
COMMENT ON COLUMN "sys_tenant"."intro" IS '企业简介';
COMMENT ON COLUMN "sys_tenant"."domain" IS '域名';
COMMENT ON COLUMN "sys_tenant"."remark" IS '备注';
COMMENT ON COLUMN "sys_tenant"."package_id" IS '租户套餐编号';
COMMENT ON COLUMN "sys_tenant"."expire_time" IS '过期时间';
COMMENT ON COLUMN "sys_tenant"."account_count" IS '用户数量（-1不限制）';
COMMENT ON COLUMN "sys_tenant"."status" IS '租户状态（0正常 1停用）';
COMMENT ON COLUMN "sys_tenant"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_tenant"."id" IS '主键';
COMMENT ON COLUMN "sys_tenant"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_tenant"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_tenant"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_tenant"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_tenant"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_tenant"."tenant_id" IS '租户编号';

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

