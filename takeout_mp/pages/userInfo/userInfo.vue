<template>
    <view class="user-info">
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
        async getUserInfo() {
            try {
                const response = await getUserInfoApi()
                if (response && response.code === 0 && response.data) {
                    this.user = response.data
                } else {
                    uni.$showMsg(response?.msg || '获取用户信息失败')
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