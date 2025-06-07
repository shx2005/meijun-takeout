<style lang="scss">
	/* 注意要写在第一行，同时给style标签加入lang="scss"属性 */
	@import "@/uni_modules/uview-ui/index.scss";

	@import "@/uni_modules/uview-ui/libs/css/flex.scss";
</style>
<script>
	import regeneratorRuntime from 'lib/runtime/runtime.js';
	import instance from 'utils/request.js'
	export default {

		globalData: {
			
		},
		data() {
			return {

			}
		},
		methods: {

		},
		onLaunch: function() {
			console.log('App Launch')
			// 不再清除存储
			// uni.clearStorage('userInfo')
			// uni.clearStorage('userId')
		},
		onShow: function() {
			console.log('App Show')
			
			// 检查登录状态
			setTimeout(() => {
				const token = uni.getStorageSync('token');
				if (!token) {
					console.log('用户未登录，自动跳转到登录页面');
					
					// 延迟跳转，确保小程序已经完全加载
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/my/my',
							success: () => {
								// 跳转成功后，显示登录提示
								uni.showModal({
									title: '提示',
									content: '请先登录以获得完整体验',
									showCancel: false
								});
							}
						});
					}, 1000);
				} else {
					console.log('用户已登录，token:', token);
				}
			}, 500);
		},
		onHide: function() {
			console.log('App Hide')

		}
	}
</script>
