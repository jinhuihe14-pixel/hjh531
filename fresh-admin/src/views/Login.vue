<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>生鲜管理后台</h2>
        <p>Fresh Admin System</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

onMounted(() => {
  const remember = localStorage.getItem('remember_login')
  if (remember) {
    const data = JSON.parse(remember)
    loginForm.username = data.username
    loginForm.password = data.password
    loginForm.remember = true
  }
})

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        if (loginForm.remember) {
          localStorage.setItem('remember_login', JSON.stringify({
            username: loginForm.username,
            password: loginForm.password
          }))
        } else {
          localStorage.removeItem('remember_login')
        }
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        const redirect = route.query.redirect
        router.push(redirect || '/')
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-title h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.login-title p {
  font-size: 14px;
  color: #999;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
}
</style>
