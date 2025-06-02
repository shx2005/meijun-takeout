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
        </view>
        
        <view class="action-buttons">
            <button class="edit-btn" @click="showEditPopup">修改信息</button>
        </view>
        
        <!-- 修改信息弹窗 -->
        <u-popup :show="isEditPopupVisible" mode="center" :round="10" @close="hideEditPopup">
            <view class="edit-popup">
                <view class="edit-title">
                    <text>修改个人信息</text>
                    <text class="popup-close" @click="hideEditPopup">×</text>
                </view>
                <view class="edit-content">
                    <view class="edit-item">
                        <text class="edit-label">姓名：</text>
                        <input class="edit-input" v-model="editForm.name" placeholder="请输入姓名" />
                    </view>
                    <view class="edit-item">
                        <text class="edit-label">性别：</text>
                        <radio-group class="radio-group" @change="onGenderChange">
                            <label class="radio-label">
                                <radio value="男" :checked="editForm.gender === '男'" />
                                <text>男</text>
                            </label>
                            <label class="radio-label">
                                <radio value="女" :checked="editForm.gender === '女'" />
                                <text>女</text>
                            </label>
                        </radio-group>
                    </view>
                    <view class="edit-item">
                        <text class="edit-label">地址：</text>
                        <textarea class="edit-textarea" v-model="editForm.address" placeholder="请输入地址"></textarea>
                    </view>
                </view>
                <view class="edit-footer">
                    <button class="edit-btn cancel-btn" @click="hideEditPopup">取消</button>
                    <button class="edit-btn submit-btn" @click="updateUserInfo">保存</button>
                </view>
            </view>
        </u-popup>
    </view>
</template>

<script>
import { getUserInfoApi, updateUserInfoApi } from '../../api/my.js'

export default {
    data() {
        return {
            user: {},
            isEditPopupVisible: false,
            editForm: {
                name: '',
                gender: '',
                address: ''
            }
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
                    console.log('用户信息API响应:', response)
                    // 修复：处理状态码为200的响应
                    if (response && (response.code === 0 || response.code === 200)) {
                        // 从response中提取用户数据
                        const userData = response.data || response
                        this.user = userData
                        // 缓存用户信息
                        uni.setStorageSync('userInfo', this.user)
                        
                        // 初始化编辑表单
                        this.initEditForm()
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
        
        // 初始化编辑表单数据
        initEditForm() {
            this.editForm = {
                name: this.user.name || '',
                gender: this.user.gender || '男',
                address: this.user.address || ''
            }
        },
        
        // 显示编辑弹窗
        showEditPopup() {
            this.initEditForm()
            this.isEditPopupVisible = true
        },
        
        // 隐藏编辑弹窗
        hideEditPopup() {
            this.isEditPopupVisible = false
        },
        
        // 性别选择变更处理
        onGenderChange(e) {
            this.editForm.gender = e.detail.value
        },
        
        // 更新用户信息
        async updateUserInfo() {
            try {
                uni.showLoading({
                    title: '保存中...'
                })
                
                // 准备更新数据
                const userInfoData = {
                        name: this.editForm.name,
                        gender: this.editForm.gender,
                        address: this.editForm.address
                };
                
                // 使用API工具方法调用更新用户信息接口
                const response = await updateUserInfoApi(userInfoData);
                
                uni.hideLoading()
                
                // 处理响应
                if (response && (response.code === 0 || response.code === 200)) {
                        uni.showToast({
                            title: '保存成功',
                            icon: 'success'
                        })
                        
                        // 更新本地用户信息
                        this.user.name = this.editForm.name
                        this.user.gender = this.editForm.gender
                        this.user.address = this.editForm.address
                        
                        // 更新缓存
                        uni.setStorageSync('userInfo', this.user)
                        
                        // 关闭弹窗
                        this.hideEditPopup()
                    } else {
                        uni.showToast({
                        title: response?.msg || '保存失败',
                        icon: 'none'
                    })
                    console.error('更新用户信息失败:', response)
                }
            } catch (error) {
                uni.hideLoading()
                uni.showToast({
                    title: '保存失败，请重试',
                    icon: 'none'
                })
                console.error('更新用户信息异常:', error)
            }
        },
        
        getIdentityText(identity) {
            switch(identity) {
                case 0:
                case "ADMIN": return '管理员'
                case 1:
                case "MERCHANT": return '商家'
                case 2:
                case "EMPLOYEE": return '员工'
                case 3:
                case "CUSTOMER": return '顾客'
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
            // 添加类型检查，确保time是字符串
            if (typeof time !== 'string') {
                // 如果是Date对象，转换为字符串
                if (time instanceof Date) {
                    return time.toISOString().replace('T', ' ').slice(0, 19)
                }
                // 其他类型直接返回
                return '未知时间格式'
            }
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
        margin-bottom: 20rpx;
        
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
                color: #333;
                font-size: 28rpx;
                flex: 1;
            }
        }
    }
    
    .action-buttons {
        padding: 20rpx 0;
        
        .edit-btn {
            background-color: #1296db;
            color: #fff;
            font-size: 28rpx;
            padding: 20rpx 0;
            border-radius: 10rpx;
            width: 100%;
        }
    }
    
    .edit-popup {
        width: 600rpx;
        background-color: #fff;
        border-radius: 12rpx;
        overflow: hidden;
        
        .edit-title {
            padding: 30rpx;
            font-size: 32rpx;
            color: #333;
            text-align: center;
            position: relative;
            border-bottom: 1rpx solid #f5f5f5;
            
            .popup-close {
                position: absolute;
                right: 30rpx;
                top: 30rpx;
                font-size: 40rpx;
                color: #999;
            }
        }
        
        .edit-content {
            padding: 30rpx;
            
            .edit-item {
                margin-bottom: 30rpx;
                
                .edit-label {
                    display: block;
                    font-size: 28rpx;
                    color: #666;
                    margin-bottom: 10rpx;
                }
                
                .edit-input, .edit-textarea {
                    width: 100%;
                    border: 1rpx solid #eee;
                    border-radius: 8rpx;
                    padding: 20rpx;
                    font-size: 28rpx;
                    color: #333;
                }
                
                .edit-textarea {
                    height: 160rpx;
                }
                
                .radio-group {
                    display: flex;
                    flex-direction: row;
                    
                    .radio-label {
                        margin-right: 30rpx;
                        font-size: 28rpx;
                        display: flex;
                        align-items: center;
                    }
                }
            }
        }
        
        .edit-footer {
            display: flex;
            border-top: 1rpx solid #f5f5f5;
            
            .edit-btn {
                flex: 1;
                border: none;
                border-radius: 0;
                font-size: 28rpx;
                padding: 25rpx 0;
                text-align: center;
                
                &.cancel-btn {
                    background-color: #f5f5f5;
                    color: #666;
                }
                
                &.submit-btn {
                    background-color: #1296db;
                    color: #fff;
                }
            }
        }
    }
}
</style> 