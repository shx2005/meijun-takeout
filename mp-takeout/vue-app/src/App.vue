<!-- src/App.vue -->
<script setup lang="ts">
import { RouterView } from 'vue-router'
import { ElContainer, ElHeader, ElMain, ElAside, ElMenu, ElMenuItem } from 'element-plus'
</script>

<template>
  <el-container style="height: 100vh; width: 100%;">
    <!-- 顶部导航栏 -->
    <el-header style="background-color: #409EFF; color: white; display: flex; align-items: center; justify-content: space-between;">
      <h2 style="margin: 0;">MeiJun 外卖系统</h2>
      <div>
        <el-menu
          default-active="/login"
          mode="horizontal"
          background-color="#409EFF"
          text-color="white"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/dashboard">首页</el-menu-item>
          <el-menu-item index="/login">登录</el-menu-item>
        </el-menu>
      </div>
    </el-header>

    <!-- 页面主体内容 -->
    <el-main style="padding: 20px;">
      <router-view />
    </el-main>
  </el-container>
</template>

<style>
body {
  margin: 0;
  font-family: Arial, sans-serif;
}
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
</style>
