DROP TABLE IF EXISTS "rel_role_menu" CASCADE;

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

