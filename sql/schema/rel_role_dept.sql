DROP TABLE IF EXISTS "rel_role_dept" CASCADE;

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

