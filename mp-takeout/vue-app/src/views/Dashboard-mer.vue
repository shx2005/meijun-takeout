<template>
  <el-container>
    <el-aside width="200px" class="dashboard-aside">
      <el-menu :default-active="activeMenu" @select="handleMenuSelect">
        <el-menu-item index="orders">订单管理</el-menu-item>
        <el-menu-item index="staff">员工管理</el-menu-item>
        <el-menu-item index="stores">门店管理</el-menu-item>
        <el-menu-item index="users">用户管理</el-menu-item>
        <el-menu-item index="coupons">优惠券</el-menu-item>
        <el-menu-item index="promotions">促销活动</el-menu-item>
        <el-menu-item index="stat">销售统计</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <!-- 订单管理 -->
      <div v-if="activeMenu === 'orders'">
        <el-card>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-input v-model="orderId" placeholder="输入订单ID查询" style="width: 200px;" />
            <el-button type="primary" @click="fetchOrderById">查询订单</el-button>
          </div>
          <el-table v-if="order" :data="[order]" style="margin-top: 16px;">
            <el-table-column prop="id" label="订单ID" />
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="totalAmount" label="总价" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-select v-model="orderStatus" placeholder="修改状态" size="small" style="width: 120px;">
                  <el-option v-for="(label, value) in orderStatusMap" :key="value" :label="label" :value="value" />
                </el-select>
                <el-button size="small" @click="updateOrderStatus(scope.row.id)">更新</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <!-- 员工管理 -->
      <div v-else-if="activeMenu === 'staff'">
        <el-card>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>员工列表</span>
            <el-button type="primary" @click="fetchStaff">刷新</el-button>
          </div>
          <el-table :data="staffList" style="margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="姓名"/>
            <el-table-column prop="phone" label="电话"/>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteStaff(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <!-- 门店管理 -->
      <div v-else-if="activeMenu === 'stores'">
        <el-card>
          <span>门店列表</span>
          <el-table :data="storeList" style="margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="门店名"/>
            <el-table-column prop="address" label="地址"/>
          </el-table>
        </el-card>
      </div>
      <!-- 用户管理 -->
      <div v-else-if="activeMenu === 'users'">
        <el-card>
          <span>用户列表</span>
          <el-table :data="userList" style="margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="用户名"/>
            <el-table-column prop="phone" label="电话"/>
          </el-table>
        </el-card>
      </div>
      <!-- 优惠券管理 -->
      <div v-else-if="activeMenu === 'coupons'">
        <el-card>
          <span>优惠券列表</span>
          <el-table :data="couponList" style="margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="名称"/>
            <el-table-column prop="discount" label="折扣"/>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteCoupon(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <!-- 促销活动管理 -->
      <div v-else-if="activeMenu === 'promotions'">
        <el-card>
          <span>促销活动列表</span>
          <el-table :data="promotionList" style="margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="活动名"/>
            <el-table-column prop="description" label="描述"/>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deletePromotion(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <!-- 销售统计 -->
      <div v-else-if="activeMenu === 'stat'">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card>
              <div>销售总额</div>
              <div style="font-size: 2em; color: #409EFF;">￥{{ salesTotal }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div>今日流量</div>
              <div style="font-size: 2em; color: #67C23A;">{{ traffic }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div>热销商品</div>
              <el-table :data="salesList" size="small" style="margin-top: 8px;">
                <el-table-column prop="name" label="商品"/>
                <el-table-column prop="sales" label="销量"/>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const activeMenu = ref('orders')
const orderId = ref('')
const order = ref(null)
const orderStatus = ref('')
const orderStatusMap = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已完成',
  4: '已取消'
}

const staffList = ref([])
const storeList = ref([])
const userList = ref([])
const couponList = ref([])
const promotionList = ref([])
const salesList = ref([])
const salesTotal = ref(0)
const traffic = ref(0)

const fetchOrderById = async () => {
  if (!orderId.value) return;
  try {
    const res = await request.get(`/merchants/${orderId.value}`);
    if (res.data.code === 200) { // 假设200表示成功
      order.value = res.data.data;
    } else {
      // 处理错误情况，例如显示错误消息
      ElMessage.error(res.data.message);
      order.value = null;
      orderStatus.value = '';
    }
  } catch (error) {
    // 处理请求错误，例如显示错误消息
    ElMessage.error('请求失败，请稍后再试');
    order.value = null;
    orderStatus.value = '';
  }
};

const updateOrderStatus = async (id: number) => {
  await request.put(`/merchants/orders/${id}/status`, null, { params: { status: orderStatus.value } })
  ElMessage.success('订单状态已更新')
  fetchOrderById()
}

const fetchStaff = async () => {
  const res = await request.get('/merchants/staff')
  staffList.value = res.data.data
}
const deleteStaff = async (id: number) => {
  await request.delete(`/merchants/staff/${id}`)
  ElMessage.success('删除成功')
  fetchStaff()
}

const fetchStores = async () => {
  const res = await request.get('/merchants/stores')
  storeList.value = res.data.data
}

const fetchUsers = async () => {
  const res = await request.get('/merchants/users')
  userList.value = res.data.data
}

const fetchCoupons = async () => {
  const res = await request.get('/merchants/coupons')
  couponList.value = res.data.data
}
const deleteCoupon = async (id: number) => {
  await request.delete(`/merchants/coupons/${id}`)
  ElMessage.success('删除成功')
  fetchCoupons()
}

const fetchPromotions = async () => {
  const res = await request.get('/merchants/promotions')
  promotionList.value = res.data.data
}
const deletePromotion = async (id: number) => {
  await request.delete(`/merchants/promotions/${id}`)
  ElMessage.success('删除成功')
  fetchPromotions()
}

const fetchStats = async () => {
  const [salesRes, totalRes, trafficRes] = await Promise.all([
    request.get('/merchants/sales'),
    request.get('/merchants/sales/total'),
    request.get('/merchants/traffic')
  ])
  salesList.value = salesRes.data.data
  salesTotal.value = totalRes.data.data
  traffic.value = trafficRes.data.data
}

const handleMenuSelect = (key: string) => {
  activeMenu.value = key
  if (key === 'orders') return
  if (key === 'staff') fetchStaff()
  if (key === 'stores') fetchStores()
  if (key === 'users') fetchUsers()
  if (key === 'coupons') fetchCoupons()
  if (key === 'promotions') fetchPromotions()
  if (key === 'stat') fetchStats()
}

onMounted(() => {
  // 默认加载员工列表
  fetchStaff()
})
</script>

<style scoped>
.dashboard-aside {
  background: #f5f7fa;
  min-height: 100vh;
  border-right: 1px solid #ebeef5;
}
.el-main {
  background: #f9f9f9;
}
</style>