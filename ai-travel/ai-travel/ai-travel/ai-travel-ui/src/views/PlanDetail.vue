<template>
  <div class="plan-detail-container">
    <header class="header">
      <div class="header-content">
        <el-button @click="goBack">返回</el-button>
        <h1>行程详情</h1>
        <div class="user-info">
          <span>{{ store.user?.username }}</span>
        </div>
      </div>
    </header>

    <div class="level-selector">
      <el-button
        v-for="level in levels"
        :key="level.value"
        @click="changeLevel(level.value)"
        :class="{ active: store.level === level.value }"
      >
        {{ level.label }}
      </el-button>
    </div>

    <div v-if="plan" class="plan-summary">
      <el-card>
        <div class="summary-item">
          <span class="label">目的地</span>
          <span class="value">{{ plan.destination }}</span>
        </div>
        <div class="summary-item">
          <span class="label">天数</span>
          <span class="value">{{ plan.days }}天</span>
        </div>
        <div class="summary-item">
          <span class="label">预算类型</span>
          <span class="value">{{ budgetLabel(plan.budget) }}</span>
        </div>
        <div v-if="plan.estimatedCost" class="summary-item">
          <span class="label">预估费用</span>
          <span class="value">¥{{ plan.estimatedCost }}</span>
        </div>
      </el-card>
    </div>

    <div v-if="plan?.dailySchedule?.length" class="timeline-container">
      <div v-for="day in plan.dailySchedule" :key="day.day" class="day-section">
        <h3>第{{ day.day }}天</h3>
        <el-card class="day-card">
          <div v-if="day.morning" class="schedule-block">
            <h4>上午</h4>
            <p>{{ day.morning }}</p>
          </div>
          <div v-if="day.afternoon" class="schedule-block">
            <h4>下午</h4>
            <p>{{ day.afternoon }}</p>
          </div>
          <div v-if="day.evening" class="schedule-block">
            <h4>晚上</h4>
            <p>{{ day.evening }}</p>
          </div>
          <div v-if="day.highlights" class="schedule-block">
            <h4>亮点</h4>
            <p>{{ day.highlights }}</p>
          </div>
          <div v-if="day.tips" class="schedule-block">
            <h4>小贴士</h4>
            <p>{{ day.tips }}</p>
          </div>
        </el-card>
      </div>
    </div>

    <div v-if="plan?.transportation || plan?.accommodation" class="extra-info">
      <el-card v-if="plan.transportation">
        <h3>交通建议</h3>
        <p v-if="plan.transportation.toDestination">前往：{{ plan.transportation.toDestination }}</p>
        <p v-if="plan.transportation.localTransport">当地：{{ plan.transportation.localTransport }}</p>
        <p v-if="plan.transportation.tips">{{ plan.transportation.tips }}</p>
      </el-card>
      <el-card v-if="plan.accommodation">
        <h3>住宿建议</h3>
        <p v-if="plan.accommodation.recommendation">{{ plan.accommodation.recommendation }}</p>
        <p v-if="plan.accommodation.area">区域：{{ plan.accommodation.area }}</p>
        <p v-if="plan.accommodation.priceRange">价格：{{ plan.accommodation.priceRange }}</p>
      </el-card>
    </div>

    <div v-if="products.length" class="products-section">
      <h3>推荐产品</h3>
      <div class="products-grid">
        <el-card v-for="product in products" :key="product.productId" class="product-card">
          <h4>{{ product.productName }}</h4>
          <p>{{ product.description }}</p>
          <div class="product-info">
            <span class="price">¥{{ product.price }}</span>
            <span class="score">匹配度: {{ Math.round((product.matchScore || 0) * 100) }}%</span>
          </div>
          <el-button type="primary" @click="addToOrder(product)">加入订单</el-button>
        </el-card>
      </div>
    </div>

    <nav class="nav-bar">
      <el-button @click="goTo('/')">规划</el-button>
      <el-button @click="goTo('/orders')">订单</el-button>
      <el-button @click="goTo('/trips')">我的行程</el-button>
      <el-button v-if="store.isAdmin" @click="goTo('/dashboard')">管理看板</el-button>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const products = ref([])

const levels = [
  { value: 'economy', label: '经济型' },
  { value: 'standard', label: '舒适型' },
  { value: 'luxury', label: '豪华型' }
]

const plan = computed(() => store.plan)

const budgetLabels = {
  economy: '经济型',
  standard: '舒适型',
  luxury: '豪华型'
}

function budgetLabel(budget) {
  return budgetLabels[budget] || budget
}

async function changeLevel(level) {
  store.setLevel(level)
  await loadProducts()
}

async function loadProducts() {
  if (!plan.value?.destination) return
  try {
    const response = await axios.post('/ai/match-products', {
      destination: plan.value.destination,
      days: plan.value.days,
      budget: store.level,
      interests: plan.value.interests
    })
    products.value = response.data?.products || []
    console.log('AI 返回的推荐产品列表:', JSON.stringify(products.value))
  } catch (error) {
    console.error(error)
  }
}

async function addToOrder(product) {
  console.log('本次提交的 productId:', product.productId, '完整对象:', product)
  if (!store.user?.id) {
    alert('请先登录')
    router.push('/login')
    return
  }
  try {
    await axios.post('/order/create', {
      userId: store.user.id,
      productId: product.productId,
      quantity: 1
    })
    alert('已加入订单')
  } catch (error) {
    console.error(error)
    alert('添加失败')
  }
}

function goBack() {
  router.push('/')
}

function goTo(path) {
  router.push(path)
}

onMounted(() => {
  if (!plan.value) {
    router.push('/')
    return
  }
  loadProducts()
})
</script>

<style scoped>
.plan-detail-container {
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

.level-selector {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 16px;
  background: white;
}

.level-selector .el-button.active {
  background: #667eea;
  color: white;
}

.plan-summary,
.timeline-container,
.extra-info,
.products-section {
  max-width: 1200px;
  margin: 16px auto;
  padding: 0 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.summary-item:last-child {
  border-bottom: none;
}

.day-section {
  margin-bottom: 16px;
}

.day-card {
  margin-top: 8px;
}

.schedule-block {
  margin-bottom: 12px;
}

.schedule-block h4 {
  margin: 0 0 4px;
  color: #667eea;
}

.extra-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.product-info {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.score {
  color: #666;
  font-size: 12px;
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
</style>