<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="text-center mb-4">注册</h2>
      <div>
        <label>用户名</label>
        <input v-model="form.username" placeholder="请输入用户名" />
      </div>
      <div>
        <label>邮箱</label>
        <input v-model="form.email" placeholder="请输入邮箱" />
      </div>
      <div>
        <label>密码</label>
        <input type="password" v-model="form.password" placeholder="请输入密码" />
      </div>
      <div>
        <label>确认密码</label>
        <input type="password" v-model="form.confirmPassword" placeholder="请确认密码" />
      </div>
      <button @click="handleRegister">注册</button>
      <p class="text-center mt-4">
        已有账号？<a href="/login">立即登录</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from '../utils/axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

async function handleRegister() {
  if (!form.username || !form.email || !form.password) {
    alert('请填写完整信息')
    return
  }
  if (form.password !== form.confirmPassword) {
    alert('两次密码不一致')
    return
  }
  loading.value = true
  try {
    await axios.post('/user/register', {
      username: form.username,
      email: form.email,
      password: form.password
    })
    alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册错误:', error)
    alert('注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
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