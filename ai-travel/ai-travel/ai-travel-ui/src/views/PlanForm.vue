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
          <el-input v-model="form.destination" placeholder="请输入目的地城市" @input="onDestinationChange" />
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

    <div v-if="recommendedTrips.length" class="recommend-card">
      <h3>为您推荐的相关行程</h3>
      <p class="recommend-hint">根据您输入的目的地，找到以下历史相关行程</p>
      <div class="recommend-list">
        <el-card v-for="trip in recommendedTrips" :key="trip.id" class="recommend-item" @click="viewTrip(trip.id)">
          <div class="recommend-header">
            <h4>{{ trip.destination }}</h4>
            <span class="recommend-days">{{ trip.days }}天</span>
          </div>
          <p class="recommend-budget">预算类型: {{ trip.budget }}</p>
          <p class="recommend-created">创建于: {{ formatDate(trip.createdAt) }}</p>
        </el-card>
      </div>
    </div>

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
const recommendedTrips = ref([])
let debounceTimer = null

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

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.split('T')[0] || dateStr.split(' ')[0] || dateStr
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

function onDestinationChange() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    loadRecommendedTrips()
  }, 400)
}

async function loadRecommendedTrips() {
  const keyword = form.destination.trim()
  if (!keyword || !store.user?.id) {
    recommendedTrips.value = []
    return
  }
  try {
    const response = await axios.get(`/plan/history/${store.user.id}`)
    const list = response.data || []
    recommendedTrips.value = list
      .filter(p => p.destination && p.destination.includes(keyword))
      .map(p => ({
        id: p.id,
        destination: p.destination,
        days: p.days,
        budget: p.budgetType,
        createdAt: p.createdAt
      }))
      .slice(0, 5)
  } catch (error) {
    console.error('获取推荐行程失败:', error)
    recommendedTrips.value = []
  }
}

function viewTrip(tripId) {
  router.push(`/plan?id=${tripId}`)
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

.recommend-card {
  max-width: 600px;
  margin: 0 auto 30px;
  padding: 0 24px;
}

.recommend-card h3 {
  margin: 0 0 4px;
  color: #333;
}

.recommend-hint {
  color: #999;
  font-size: 13px;
  margin: 0 0 12px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.recommend-item:hover {
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.2);
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommend-header h4 {
  margin: 0;
}

.recommend-days {
  background: #667eea;
  color: white;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.recommend-budget,
.recommend-created {
  margin: 4px 0 0;
  color: #666;
  font-size: 13px;
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
  .recommend-card {
    padding: 0 16px;
  }
}
</style>