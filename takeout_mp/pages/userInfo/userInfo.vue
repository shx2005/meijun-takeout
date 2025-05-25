<template>
    <view class="user-info">
        <view class="auto-login-btn" v-if="!user.id" @click="autoLogin">一键登录测试账号</view>
        
        <view class="info-card">
            <view class="info-item">
                <text class="label">用户ID：</text>
                <text class="value">{{ user.id || '无' }}</text>
            </view>
            <view class="info-item">
                <text class="label">姓名：</text>
                <text class="value">{{ user.name || '未填写' }}</text>
            </view>
            <view class="info-item">
                <text class="label">用户名：</text>
                <text class="value">{{ user.username || '未填写' }}</text>
            </view>
            <view class="info-item">
                <text class="label">身份：</text>
                <text class="value">{{ getIdentityText(user.identity) }}</text>
            </view>
            <view class="info-item">
                <text class="label">手机号：</text>
                <text class="value">{{ formatPhoneNum(user.phoneNum) || '未绑定' }}</text>
            </view>
            <view class="info-item">
                <text class="label">性别：</text>
                <text class="value">{{ user.gender || '未填写' }}</text>
            </view>
            <view class="info-item">
                <text class="label">地址：</text>
                <text class="value">{{ user.address || '未填写' }}</text>
            </view>
            <view class="info-item" v-if="user.merchantId">
                <text class="label">商家ID：</text>
                <text class="value">{{ user.merchantId }}</text>
            </view>
            <view class="info-item">
                <text class="label">账号状态：</text>
                <text class="value">{{ getStatusText(user.status) }}</text>
            </view>
            <view class="info-item">
                <text class="label">注册时间：</text>
                <text class="value">{{ formatTime(user.createTime) }}</text>
            </view>
            <view class="info-item">
                <text class="label">更新时间：</text>
                <text class="value">{{ formatTime(user.updateTime) }}</text>
            </view>
        </view>
    </view>
</template>

<script>
import { getUserInfoApi } from '../../api/my.js'

export default {
    data() {
        return {
            user: {}
        }
    },
    onShow() {
        this.getUserInfo()
    },
    methods: {
        autoLogin() {
            uni.showLoading({
                title: '登录中...'
            })
            
            const requestBody = {
                username: "17344402975",
                password: "20050311",
                identity: "CUSTOMER"
            }
            
            uni.request({
                url: 'http://localhost:8080/api/v1/auth/login',
                method: 'POST',
                header: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'userType': '3'
                },
                data: requestBody,
                success: (res) => {
                    console.log('自动登录响应:', res)
                    uni.hideLoading()
                    
                    if (res.statusCode === 200 && res.data) {
                        // 提取登录响应中的数据
                        let userData = null
                        let token = null
                        
                        if (res.data.data) {
                            // 正常格式响应
                            userData = res.data.data
                            token = userData.token
                        } else if (res.data.token) {
                            // 直接包含token的格式
                            userData = res.data
                            token = res.data.token
                        }
                        
                        if (token) {
                            // 保存token到本地存储
                            uni.setStorageSync('token', token)
                            uni.setStorageSync('phoneNumber', "17344402975")
                            
                            // 直接使用登录响应中的用户信息
                            if (userData) {
                                console.log('直接使用登录响应中的用户信息:', userData)
                                this.user = {
                                    id: userData.id,
                                    username: userData.username || "17344402975",
                                    name: userData.name,
                                    phoneNum: userData.phoneNum || "17344402975",
                                    identity: userData.identity || 3,
                                    status: userData.status || 1,
                                    createTime: userData.createTime,
                                    updateTime: userData.updateTime
                                }
                                
                                // 缓存用户信息
                                uni.setStorageSync('userInfo', this.user)
                            }
                            
                            uni.$showMsg('登录成功', 'success')
                        } else {
                            uni.$showMsg('登录成功但未获取到token')
                        }
                    } else {
                        uni.$showMsg('登录失败: ' + (res.data?.msg || '未知错误'))
                    }
                },
                fail: (err) => {
                    uni.hideLoading()
                    console.error('登录请求失败:', err)
                    uni.$showMsg('网络请求失败')
                }
            })
        },
        
        async getUserInfo() {
            try {
                // 先检查本地缓存中是否有用户信息
                const cachedUserInfo = uni.getStorageSync('userInfo')
                if (cachedUserInfo) {
                    console.log('使用缓存的用户信息:', cachedUserInfo)
                    this.user = cachedUserInfo
                    return
                }
                
                // 获取token
                const token = uni.getStorageSync('token')
                if (!token) {
                    console.log('未登录状态，不获取用户信息')
                    return
                }
                
                // 尝试调用API获取用户信息
            try {
                const response = await getUserInfoApi()
                if (response && response.code === 0 && response.data) {
                    this.user = response.data
                        // 缓存用户信息
                        uni.setStorageSync('userInfo', this.user)
                } else {
                        console.error('API获取用户信息失败:', response)
                        // 如果API调用失败，尝试自动登录一次
                        this.autoLogin()
                    }
                } catch (apiError) {
                    console.error('API获取用户信息异常:', apiError)
                    // API异常，尝试自动登录
                    this.autoLogin()
                }
            } catch (error) {
                console.error('获取用户信息失败', error)
                uni.$showMsg('获取用户信息失败，请重试')
            }
        },
        
        getIdentityText(identity) {
            switch(identity) {
                case 0: return '管理员'
                case 1: return '商家'
                case 2: return '员工'
                case 3: return '顾客'
                default: return '未知'
            }
        },
        getStatusText(status) {
            switch(status) {
                case 0: return '禁用'
                case 1: return '正常'
                default: return '未知'
            }
        },
        formatTime(time) {
            if (!time) return '未记录'
            return time.replace('T', ' ').slice(0, 19)
        },
        formatPhoneNum(phone) {
            if (!phone) return ''
            return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2")
        }
    }
}
</script>

<style lang="scss" scoped>
.user-info {
    padding: 20rpx;
    background-color: #f3f2f7;
    min-height: 100vh;
    
    .auto-login-btn {
        background-color: #1296db;
        color: #fff;
        padding: 20rpx 30rpx;
        border-radius: 10rpx;
        text-align: center;
        margin-bottom: 20rpx;
        font-size: 30rpx;
    }
    
    .info-card {
        background: #ffffff;
        border-radius: 12rpx;
        padding: 30rpx;
        
        .info-item {
            display: flex;
            padding: 20rpx 0;
            border-bottom: 1rpx solid #f5f5f5;
            
            &:last-child {
                border-bottom: none;
            }
            
            .label {
                color: #666;
                width: 160rpx;
                font-size: 28rpx;
            }
            
            .value {
                flex: 1;
                color: #333;
                font-size: 28rpx;
            }
        }
    }
}
</style> 