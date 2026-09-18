<template>
  <div class="my-trips-container">
    <header class="header">
      <div class="header-content">
        <h1>我的行程</h1>
        <div class="user-info">
          <span>{{ store.user?.username }}</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </header>

    <div class="trips-container">
      <el-card v-for="trip in trips" :key="trip.id" class="trip-card">
        <div class="trip-header">
          <h3>{{ trip.destination }}</h3>
          <span class="days">{{ trip.days }}天</span>
        </div>
        <p class="budget">预算: ¥{{ trip.budget }}</p>
        <p class="created">创建时间: {{ trip.createdAt }}</p>
        <div class="trip-actions">
          <el-button @click="viewTrip(trip.id)">查看</el-button>
          <el-button @click="generateShareLink(trip.id)">分享</el-button>
          <el-button @click="exportPoster(trip.id)">海报</el-button>
        </div>
      </el-card>

      <div v-if="trips.length === 0" class="empty-state">
        <p>暂无行程</p>
        <el-button type="primary" @click="goTo('/')">创建行程</el-button>
      </div>
    </div>

    <nav class="nav-bar">
      <el-button @click="goTo('/')">规划</el-button>
      <el-button @click="goTo('/orders')">订单</el-button>
      <el-button @click="goTo('/trips')" :class="{ active: $route.path === '/trips' }">我的行程</el-button>
      <el-button v-if="store.isAdmin" @click="goTo('/dashboard')">管理看板</el-button>
    </nav>

    <el-dialog title="分享链接" v-model="showShare" width="400px">
      <p>分享链接已复制到剪贴板</p>
      <input type="text" :value="shareLink" readonly class="share-input" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const trips = ref([])
const showShare = ref(false)
const shareLink = ref('')

async function loadTrips() {
  if (!store.user?.id) return
  try {
    const response = await axios.get(`/plan/history/${store.user.id}`)
    trips.value = (response.data || []).map(p => ({
      id: p.id,
      destination: p.destination,
      days: p.days,
      budget: p.budgetType,
      createdAt: p.createdAt
    }))
  } catch (error) {
    console.error(error)
  }
}

function viewTrip(tripId) {
  router.push(`/plan?id=${tripId}`)
}

async function generateShareLink(tripId) {
  try {
    const response = await axios.post(`/trips/${tripId}/share`)
    shareLink.value = response.data.url
    await navigator.clipboard.writeText(shareLink.value)
    showShare.value = true
  } catch (error) {
    console.error(error)
    alert('生成失败')
  }
}

async function exportPoster(tripId) {
  try {
    const response = await axios.get(`/trips/${tripId}/poster`, {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(response.data)
    const a = document.createElement('a')
    a.href = url
    a.download = `trip-${tripId}.png`
    a.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error(error)
    alert('导出失败')
  }
}

function handleLogout() {
  store.logout()
  router.push('/login')
}

function goTo(path) {
  router.push(path)
}

onMounted(() => {
  loadTrips()
})
</script>

<style scoped>
.my-trips-container {
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

.trips-container {
  max-width: 1200px;
  margin: 16px auto;
  padding: 0 16px;
}

.trip-card {
  margin-bottom: 16px;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.days {
  background: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.budget {
  color: #666;
}

.created {
  color: #999;
  font-size: 14px;
}

.trip-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.share-input {
  width: 100%;
  padding: 8px;
  margin-top: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
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
</style>