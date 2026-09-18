<template>
  <div class="dashboard-container">
    <header class="header">
      <div class="header-content">
        <h1>运营看板</h1>
        <div class="user-info">
          <span>{{ store.user?.username }}</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </header>

    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalUsers }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalOrders }}</span>
          <span class="stat-label">总订单数</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon">🤖</div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.aiCalls }}</span>
          <span class="stat-label">AI调用次数</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon">¥</div>
        <div class="stat-content">
          <span class="stat-value">{{ stats.totalRevenue }}</span>
          <span class="stat-label">总收入</span>
        </div>
      </el-card>
    </div>

    <div class="charts-row">
      <el-card class="chart-card">
        <h3>热门目的地</h3>
        <div ref="destinationChart" class="chart"></div>
      </el-card>
      <el-card class="chart-card">
        <h3>产品销量</h3>
        <div ref="salesChart" class="chart"></div>
      </el-card>
    </div>

    <div class="charts-row">
      <el-card class="chart-card full-width">
        <h3>AI调用趋势</h3>
        <div ref="aiChart" class="chart"></div>
      </el-card>
    </div>

    <nav class="nav-bar">
      <el-button @click="goTo('/')">规划</el-button>
      <el-button @click="goTo('/orders')">订单</el-button>
      <el-button @click="goTo('/trips')">我的行程</el-button>
      <el-button @click="goTo('/dashboard')" :class="{ active: $route.path === '/dashboard' }">管理看板</el-button>
    </nav>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

const destinationChart = ref(null)
const salesChart = ref(null)
const aiChart = ref(null)

const stats = reactive({
  totalUsers: 0,
  totalOrders: 0,
  aiCalls: 0,
  totalRevenue: 0
})

const destinationData = ref([])
const salesData = ref([])
const aiData = ref([])

async function loadStats() {
  try {
    const response = await axios.get('/admin/stats')
    Object.assign(stats, response.data)
  } catch (error) {
    console.error(error)
  }
}

async function loadChartsData() {
  try {
    const [destRes, salesRes, aiRes] = await Promise.all([
      axios.get('/admin/destinations'),
      axios.get('/admin/sales'),
      axios.get('/admin/ai-calls')
    ])
    destinationData.value = destRes.data
    salesData.value = salesRes.data
    aiData.value = aiRes.data
  } catch (error) {
    console.error(error)
  }
}

function initCharts() {
  if (destinationChart.value) {
    const chart = echarts.init(destinationChart.value)
    chart.setOption({
      xAxis: {
        type: 'category',
        data: destinationData.value.map(d => d.name)
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: destinationData.value.map(d => d.count),
        type: 'bar',
        color: '#667eea'
      }]
    })
  }

  if (salesChart.value) {
    const chart = echarts.init(salesChart.value)
    chart.setOption({
      series: [{
        type: 'pie',
        data: salesData.value.map(d => ({
          value: d.sales,
          name: d.name
        })),
        color: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe']
      }]
    })
  }

  if (aiChart.value) {
    const chart = echarts.init(aiChart.value)
    chart.setOption({
      xAxis: {
        type: 'category',
        data: aiData.value.map(d => d.date)
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: aiData.value.map(d => d.count),
        type: 'line',
        smooth: true,
        color: '#667eea'
      }]
    })
  }
}

function handleLogout() {
  store.logout()
  router.push('/login')
}

function goTo(path) {
  router.push(path)
}

onMounted(async () => {
  await loadStats()
  await loadChartsData()
  await nextTick()
  initCharts()

  window.addEventListener('resize', () => {
    if (destinationChart.value) {
      echarts.init(destinationChart.value).resize()
    }
    if (salesChart.value) {
      echarts.init(salesChart.value).resize()
    }
    if (aiChart.value) {
      echarts.init(aiChart.value).resize()
    }
  })
})
</script>

<style scoped>
.dashboard-container {
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
  max-width: 1400px;
  margin: 0 auto;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px;
  max-width: 1400px;
  margin: 0 auto;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 40px;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 16px;
  padding: 0 16px;
  max-width: 1400px;
  margin: 16px auto;
}

.chart-card {
  padding: 16px;
}

.chart-card h3 {
  margin: 0 0 16px 0;
}

.chart {
  height: 300px;
}

.full-width {
  grid-column: 1 / -1;
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
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>