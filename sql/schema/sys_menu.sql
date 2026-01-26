DROP TABLE IF EXISTS "sys_menu" CASCADE;

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

