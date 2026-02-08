DROP TABLE IF EXISTS "rel_user_role" CASCADE;

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

