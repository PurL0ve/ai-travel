<template>
  <div class="plan-form-container">
    <header class="header">
      <div class="header-content">
        <h1>AI旅行规划</h1>
        <div class="user-info">
          <span>{{ store.user?.username }}</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </header>

    <el-card class="form-card">
      <h2>创建旅行规划</h2>
      
      <el-form :model="form" label-width="120px">
        <el-form-item label="目的地">
          <el-input v-model="form.destination" placeholder="请输入目的地城市" />
        </el-form-item>

        <el-form-item label="旅行天数">
          <el-slider v-model="form.days" :min="1" :max="14" :step="1" show-input />
        </el-form-item>

        <el-form-item label="预算范围">
          <el-slider v-model="form.budget" :min="500" :max="50000" :step="500" show-input :format-tooltip="formatBudget" />
          <span class="budget-label">¥{{ form.budget }}</span>
        </el-form-item>

        <el-form-item label="兴趣偏好">
          <el-checkbox-group v-model="form.interests">
            <el-checkbox label="美食" value="food" />
            <el-checkbox label="历史文化" value="culture" />
            <el-checkbox label="自然风光" value="nature" />
            <el-checkbox label="亲子" value="family" />
            <el-checkbox label="购物" value="shopping" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="同行人员">
          <el-select v-model="form.travelers" multiple placeholder="请选择同行人员">
            <el-option label="独自旅行" value="alone" />
            <el-option label="情侣" value="couple" />
            <el-option label="家庭" value="family" />
            <el-option label="朋友" value="friends" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="w-full" @click="generatePlan" :loading="loading">
            {{ loading ? '生成中...' : '生成旅行规划' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <nav class="nav-bar">
      <el-button @click="goTo('/')" :class="{ active: $route.path === '/' }">规划</el-button>
      <el-button @click="goTo('/orders')">订单</el-button>
      <el-button @click="goTo('/trips')">我的行程</el-button>
      <el-button v-if="store.isAdmin" @click="goTo('/dashboard')">管理看板</el-button>
    </nav>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const loading = ref(false)

const form = reactive({
  destination: '',
  days: 3,
  budget: 5000,
  interests: [],
  travelers: []
})

function formatBudget(val) {
  return `¥${val}`
}

// 用于调用 /ai/generate-plan (需要英文 key)
function getBudgetType(budget) {
  if (budget < 3000) return 'economy'
  if (budget < 10000) return 'standard'
  return 'luxury'
}

// 用于调用 /plan/create (需要中文，且必须匹配后端正则: 经济型|舒适型|豪华型)
function getBudgetTypeCn(budget) {
  if (budget < 3000) return '经济型'
  if (budget < 10000) return '舒适型'
  return '豪华型'
}

async function generatePlan() {
  if (!form.destination) {
    alert('请输入目的地')
    return
  }
  loading.value = true
  try {
    const budgetType = getBudgetType(form.budget)
    const response = await axios.post('/ai/generate-plan', {
      destination: form.destination,
      days: form.days,
      budget: budgetType,
      interests: form.interests,
      companions: form.travelers.join(',')
    })
    if (response.code === 200 && response.data) {
      store.setPlan(response.data)
      store.setLevel(budgetType)

      // 把这次行程保存到数据库，方便"我的行程"里展示
      try {
        await axios.post('/plan/create', {
          userId: store.user.id,
          destination: form.destination,
          days: form.days,
          budgetType: getBudgetTypeCn(form.budget),
          interests: form.interests
        })
      } catch (saveError) {
        console.error('保存行程失败:', saveError)
        // 保存失败不影响用户继续查看本次生成的行程详情
      }

      router.push('/plan')
    } else {
      alert(response.msg || '生成失败')
    }
  } catch (error) {
    console.error(error)
    alert(error.response?.data?.msg || '生成失败')
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  store.logout()
  router.push('/login')
}

function goTo(path) {
  router.push(path)
}
</script>

<style scoped>
.plan-form-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-card {
  max-width: 600px;
  margin: 30px auto;
  padding: 24px;
}

.budget-label {
  margin-left: 16px;
  color: #666;
}

.nav-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 12px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
}

.nav-bar .el-button {
  flex: 1;
  margin: 0 4px;
}

.nav-bar .active {
  color: #667eea;
  font-weight: bold;
}

@media (max-width: 768px) {
  .form-card {
    margin: 16px;
  }
}
</style>