<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="text-center mb-4">登录</h2>
      <div>
        <label>邮箱</label>
        <input v-model="form.email" placeholder="请输入邮箱" />
      </div>
      <div>
        <label>密码</label>
        <input type="password" v-model="form.password" placeholder="请输入密码" />
      </div>
      <button @click="handleLogin">登录</button>
      <p class="text-center mt-4">
        还没有账号？<a href="/register">立即注册</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

const form = reactive({
  email: '',
  password: ''
})

async function handleLogin() {
  try {
    const response = await axios.post('/user/login', form)
    if (response.code === 200 && response.data) {
      store.setToken(response.data.accessToken)
      store.setUser(response.data.user)
      router.push('/')
    } else {
      alert(response.msg || '登录失败')
    }
  } catch (error) {
    console.error(error)
    alert(error.response?.data?.msg || '登录失败')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.text-center {
  text-align: center;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
}
</style>