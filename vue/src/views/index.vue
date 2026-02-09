<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24" style="padding-left: 20px">
        <h2>FoxDen 多租户管理系统</h2>
        <p>
          FoxDen 是基于 Spring Boot、Kotlin 和 Jimmer ORM 构建的现代化多租户 SaaS 系统
          <br />
          * 前端开发框架 Vue3、TS、PrimeVue<br />
          * 后端开发框架 Spring Boot 3.5.10<br />
          * 开发语言 Kotlin 2.3.0<br />
          * ORM 框架 Jimmer 0.10.6<br />
          * 权限认证框架 Sa-Token 1.44.0<br />
          * 数据库 PostgreSQL<br />
          * 缓存 Redis (Redisson 3.35.0)<br />
          * 分布式锁 Lock4j 2.2.4<br />
          * 社交登录 JustAuth 1.16.7 支持 20+ 平台<br />
          * API 文档 SpringDoc OpenAPI 2.8.2<br />
          * Excel 处理 EasyExcel 4.0.3<br />
          * 对象存储 MinIO、阿里云 OSS、腾讯云 COS<br />
          * 多租户支持 内置租户隔离与管理<br />
          * 数据权限 自动数据过滤<br />
          * 多种认证策略 密码、短信、邮箱、OAuth、微信小程序<br />
          * 构建工具 Gradle (Kotlin DSL)<br />
        </p>
        <p><b>当前版本:</b> <span>v1.0.0</span></p>
        <p>
          <el-tag type="success">&yen;Apache 2.0 开源</el-tag>
        </p>
        <p>
          <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://github.com/AlphaFoxz/FoxDen')"
            >访问 GitHub</el-button
          >
          <el-button
            type="primary"
            icon="Document"
            plain
            @click="goTarget('https://github.com/AlphaFoxz/FoxDen/blob/main/README.md')"
            >文档</el-button
          >
        </p>
      </el-col>
    </el-row>
    <el-divider />
  </div>
</template>

<script setup name="Index" lang="ts">
const goTarget = (url: string) => {
  window.open(url, '__blank');
};
</script>

<style lang="scss" scoped>
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: 'open sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
