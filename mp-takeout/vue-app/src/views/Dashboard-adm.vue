<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: "DashboardAdm"
})
</script>

<template>
  <el-container>
    <el-aside width="200px" class="dashboard-aside">
      <el-menu default-active="admin" class="el-menu-vertical-demo" @select="handleMenuSelect">
        <el-menu-item index="admin">管理员管理</el-menu-item>
        <el-menu-item index="employee">员工管理</el-menu-item>
        <el-menu-item index="stat">销售统计</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <div v-if="activeMenu === 'admin'">
        <el-card>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold;">管理员列表</span>
            <el-button type="primary" @click="fetchAdmins">刷新</el-button>
          </div>
          <el-table :data="adminList" style="width: 100%; margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="username" label="用户名"/>
            <el-table-column prop="email" label="邮箱"/>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteAdmin(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <div v-else-if="activeMenu === 'employee'">
        <el-card>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold;">员工列表</span>
            <el-button type="primary" @click="fetchEmployees">刷新</el-button>
          </div>
          <el-table :data="employeeList" style="width: 100%; margin-top: 16px;">
            <el-table-column prop="id" label="ID" width="60"/>
            <el-table-column prop="name" label="姓名"/>
            <el-table-column prop="phone" label="电话"/>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteEmployee(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <div v-else-if="activeMenu === 'stat'">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card>
              <div>销售总额</div>
              <div style="font-size: 2em; color: #409EFF;">${{ salesTotal }}</div>
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

const activeMenu = ref('admin')
const adminList = ref([])
const employeeList = ref([])
const salesList = ref([])
const salesTotal = ref(0)
const traffic = ref(0)

const fetchAdmins = async () => {
  const res = await request.get('/admin/all')
  adminList.value = res.data.data
}
const deleteAdmin = async (id: number) => {
  ElMessageBox.confirm('确定删除该管理员吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/admin/delete/${id}`)
      ElMessage.success('删除成功')
      fetchAdmins()
    })
}
const fetchEmployees = async () => {
  const res = await request.get('/admin/employee/all')
  employeeList.value = res.data.data
}
const deleteEmployee = async (id: number) => {
  ElMessageBox.confirm('确定删除该员工吗？', '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/admin/employee/${id}/delete`)
      ElMessage.success('删除成功')
      fetchEmployees()
    })
}
const fetchStats = async () => {
  const [salesRes, totalRes, trafficRes] = await Promise.all([
    request.get('/admin/sales'),
    request.get('/admin/sales/total'),
    request.get('/admin/traffic')
  ])
  salesList.value = salesRes.data.data
  salesTotal.value = totalRes.data.data
  traffic.value = trafficRes.data.data
}

const handleMenuSelect = (key: string) => {
  activeMenu.value = key
  if (key === 'admin') fetchAdmins()
  if (key === 'employee') fetchEmployees()
  if (key === 'stat') fetchStats()
}

onMounted(() => {
  fetchAdmins()
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