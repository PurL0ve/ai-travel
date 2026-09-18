<template>
  <div class="order-list-container">
    <header class="header">
      <div class="header-content">
        <h1>我的订单</h1>
        <div class="user-info">
          <span>{{ store.user?.username }}</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </header>

    <div class="tabs">
      <el-button 
        v-for="tab in tabs" 
        :key="tab.value"
        @click="activeTab = tab.value"
        :class="{ active: activeTab === tab.value }"
      >
        {{ tab.label }}
      </el-button>
    </div>

    <div class="orders-container">
      <el-card v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-id">订单号: {{ order.id }}</span>
          <span :class="['status', statusClass(order.status)]">{{ order.status }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-info">
              <h4>{{ item.productName }}</h4>
              <p>{{ item.description }}</p>
            </div>
            <div class="item-price">
              <span>¥{{ item.price }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <span class="total">总计: ¥{{ order.totalAmount }}</span>
          <div class="actions">
            <el-button v-if="order.status === '待支付'" type="primary" @click="payOrder(order.id)">支付</el-button>
            <el-button v-if="order.status === '待支付' || order.status === '已支付'" @click="cancelOrder(order.id)">
              {{ order.status === '已支付' ? '申请退款' : '取消订单' }}
            </el-button>
            <el-button @click="viewDetail(order.id)">详情</el-button>
          </div>
        </div>
      </el-card>

      <div v-if="filteredOrders.length === 0" class="empty-state">
        <p>暂无订单</p>
      </div>
    </div>

    <nav class="nav-bar">
      <el-button @click="goTo('/')">规划</el-button>
      <el-button @click="goTo('/orders')" :class="{ active: $route.path === '/orders' }">订单</el-button>
      <el-button @click="goTo('/trips')">我的行程</el-button>
      <el-button v-if="store.isAdmin" @click="goTo('/dashboard')">管理看板</el-button>
    </nav>

    <el-dialog title="订单详情" v-model="showDetail" width="600px">
      <div v-if="currentOrder">
        <div class="detail-section">
          <h3>订单信息</h3>
          <p>订单号: {{ currentOrder.id }}</p>
          <p>状态: {{ currentOrder.status }}</p>
          <p>创建时间: {{ currentOrder.createdAt }}</p>
        </div>
        <div class="detail-section">
          <h3>商品明细</h3>
          <div v-for="item in currentOrder.items" :key="item.id" class="detail-item">
            <span>{{ item.productName }}</span>
            <span>¥{{ item.price }} x {{ item.quantity }}</span>
          </div>
        </div>
        <div class="detail-section">
          <p>总计: ¥{{ currentOrder.totalAmount }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from '../utils/axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const orders = ref([])
const activeTab = ref('all')
const showDetail = ref(false)
const currentOrder = ref(null)

// 后端订单状态：待支付 / 已支付 / 已取消 / 已退款
const tabs = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待支付' },
  { value: 'paid', label: '已支付' },
  { value: 'cancelled', label: '已取消' },
  { value: 'refunded', label: '已退款' }
]

const statusMap = {
  pending: '待支付',
  paid: '已支付',
  cancelled: '已取消',
  refunded: '已退款'
}

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === statusMap[activeTab.value])
})

function statusClass(status) {
  if (status === '待支付') return 'PENDING'
  if (status === '已支付') return 'PAID'
  if (status === '已取消') return 'CANCELLED'
  if (status === '已退款') return 'REFUNDED'
  return ''
}

async function loadOrders() {
  if (!store.user?.id) return
  try {
    const response = await axios.get(`/order/list/${store.user.id}`)
    const list = response.data || []
    orders.value = list.map(o => ({
      id: o.orderNo,
      status: o.status,
      totalAmount: o.totalPrice,
      createdAt: o.createdAt,
      items: [{
        id: o.productId,
        productName: o.productName || '产品',
        description: o.remark || '',
        price: o.totalPrice,
        quantity: o.quantity
      }]
    }))
  } catch (error) {
    console.error(error)
  }
}

async function payOrder(orderNo) {
  try {
    await axios.put(`/order/pay/${orderNo}`)
    loadOrders()
    alert('支付成功')
  } catch (error) {
    console.error(error)
    alert('支付失败')
  }
}

async function cancelOrder(orderNo) {
  if (!confirm('确定要取消/退款该订单吗？')) return
  try {
    await axios.put(`/order/cancel/${orderNo}`)
    loadOrders()
    alert('操作成功')
  } catch (error) {
    console.error(error)
    alert('操作失败')
  }
}

async function viewDetail(orderNo) {
  try {
    const response = await axios.get(`/order/detail/${orderNo}`)
    const o = response.data
    currentOrder.value = {
      id: o.orderNo,
      status: o.status,
      totalAmount: o.totalPrice,
      createdAt: o.createdAt,
      items: [{
        id: o.productId,
        productName: o.productName || '产品',
        price: o.totalPrice,
        quantity: o.quantity
      }]
    }
    showDetail.value = true
  } catch (error) {
    console.error(error)
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
  loadOrders()
})
</script>

<style scoped>
.order-list-container {
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

.tabs {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: white;
  flex-wrap: wrap;
}

.tabs .el-button {
  padding: 8px 16px;
}

.tabs .active {
  background: #667eea;
  color: white;
}

.orders-container {
  max-width: 1200px;
  margin: 16px auto;
  padding: 0 16px;
}

.order-card {
  margin-bottom: 16px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-id {
  color: #666;
  font-size: 14px;
}

.status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.status.PENDING {
  background: #fef08a;
  color: #854d0e;
}

.status.PAID {
  background: #bbf7d0;
  color: #166534;
}

.status.CANCELLED {
  background: #e5e7eb;
  color: #4b5563;
}

.status.REFUNDED {
  background: #fecaca;
  color: #991b1b;
}

.order-items {
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.item-info h4 {
  margin: 0;
}

.item-info p {
  margin: 4px 0 0;
  color: #666;
  font-size: 14px;
}

.item-price {
  text-align: right;
}

.item-price .quantity {
  color: #999;
  font-size: 12px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.actions {
  display: flex;
  gap: 8px;
}

.total {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.detail-section {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
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