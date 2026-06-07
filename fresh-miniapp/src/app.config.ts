export default defineAppConfig({
  pages: [
    'pages/home/index',
    'pages/category/index',
    'pages/cart/index',
    'pages/mine/index',
    'pages/detail/index',
    'pages/orderList/index',
    'pages/orderDetail/index',
    'pages/address/index',
    'pages/login/index'
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#07C160',
    navigationBarTitleText: '鲜达生鲜',
    navigationBarTextStyle: 'white',
    backgroundColor: '#F7F8FA'
  },
  tabBar: {
    color: '#86909C',
    selectedColor: '#07C160',
    backgroundColor: '#FFFFFF',
    borderStyle: 'white',
    list: [
      {
        pagePath: 'pages/home/index',
        text: '首页'
      },
      {
        pagePath: 'pages/category/index',
        text: '分类'
      },
      {
        pagePath: 'pages/cart/index',
        text: '购物车'
      },
      {
        pagePath: 'pages/mine/index',
        text: '我的'
      }
    ]
  }
})
