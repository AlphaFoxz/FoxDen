DROP TABLE IF EXISTS "rel_user_post" CASCADE;

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

