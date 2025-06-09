<template>
  <el-container style="height: 100vh; align-items: center; justify-content: center">
    <el-card style="width: 400px">
      <h2 style="text-align: center">登录</h2>
      <el-form @submit.prevent="handleLogin" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="验证码" prop="captcha">
          <div style="display: flex; gap: 10px">
            <el-input v-model="form.captcha" />
            <img
              :src="captchaUrl"
              alt="验证码"
              style="cursor: pointer"
              @click="fetchCaptcha"
            />
          </div>
        </el-form-item>
        <el-button type="primary" native-type="submit" style="width: 100%">登录</el-button>
      </el-form>
    </el-card>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login, getCaptchaBase64 } from '../api/auth';
import { ElMessage } from 'element-plus';
import type {AuthLoginDTO} from "../api/types.ts";

const router = useRouter();
const captchaUrl = ref('');
const form = ref<AuthLoginDTO>({
  username: '',
  password: '',
  captcha: ''
});
const formRef = ref();

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
};

const fetchCaptcha = async () => {
  try {
    const res = await getCaptchaBase64();
    captchaUrl.value = res.data.code;
  } catch (error) {
    ElMessage.error('获取验证码失败');
  }
};

const handleLogin = async () => {
  try {
    const res = await login(form.value);
    const { token, uuid } = res.data;
    localStorage.setItem('token', token);
    localStorage.setItem('uuid', uuid);
    ElMessage.success('登录成功');
    router.push('/dashboard');
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名或密码');
  }
};

fetchCaptcha();
</script>
