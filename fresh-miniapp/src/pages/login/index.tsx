import React, { useState } from 'react'
import { View, Text, Input, Button } from '@tarojs/components'
import Taro from '@tarojs/taro'
import classnames from 'classnames'
import styles from './index.module.scss'
import { useUserStore } from '@/store/useUserStore'

const LoginPage: React.FC = () => {
  const { login } = useUserStore()
  const [phone, setPhone] = useState('')
  const [code, setCode] = useState('')
  const [agreed, setAgreed] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const [focusField, setFocusField] = useState('')

  const handleSendCode = () => {
    if (!phone) {
      Taro.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test(phone)) {
      Taro.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }
    if (countdown > 0) return

    console.log('[Login] send code to:', phone)
    Taro.showToast({ title: '验证码已发送', icon: 'success' })
    setCountdown(60)
    const timer = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(timer)
          return 0
        }
        return prev - 1
      })
    }, 1000)
  }

  const handleLogin = () => {
    if (!phone) {
      Taro.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test(phone)) {
      Taro.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }
    if (!code) {
      Taro.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    if (!agreed) {
      Taro.showToast({ title: '请先同意用户协议', icon: 'none' })
      return
    }

    const mockToken = 'mock-token-' + Date.now()
    const mockUser = {
      id: 1,
      username: phone,
      nickname: '用户' + phone.slice(-4),
      phone,
      avatar: '👤',
      gender: 0,
      integral: 0,
      level: 1
    }

    login(mockToken, mockUser)
    console.log('[Login] login success:', mockUser)

    Taro.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      Taro.navigateBack()
    }, 1000)
  }

  const handleWechatLogin = () => {
    if (!agreed) {
      Taro.showToast({ title: '请先同意用户协议', icon: 'none' })
      return
    }
    console.log('[Login] wechat login')
    Taro.showToast({ title: '微信登录开发中', icon: 'none' })
  }

  return (
    <View className={styles.page}>
      <View className={styles.logoSection}>
        <View className={styles.logo}>🥬</View>
        <Text className={styles.appName}>鲜达生鲜</Text>
        <Text className={styles.appSlogan}>新鲜直达 · 品质保障</Text>
      </View>

      <View className={styles.formSection}>
        <View className={styles.formItem}>
          <Text className={styles.formLabel}>手机号</Text>
          <View className={classnames(styles.inputWrap, focusField === 'phone' && styles.focus)}>
            <Text className={styles.inputIcon}>📱</Text>
            <Input
              className={styles.input}
              type='number'
              placeholder='请输入手机号'
              value={phone}
              maxLength={11}
              onFocus={() => setFocusField('phone')}
              onBlur={() => setFocusField('')}
              onInput={(e) => setPhone(e.detail.value)}
            />
          </View>
        </View>

        <View className={styles.formItem}>
          <Text className={styles.formLabel}>验证码</Text>
          <View className={classnames(styles.inputWrap, focusField === 'code' && styles.focus)}>
            <Text className={styles.inputIcon}>🔐</Text>
            <Input
              className={styles.input}
              type='number'
              placeholder='请输入验证码'
              value={code}
              maxLength={6}
              onFocus={() => setFocusField('code')}
              onBlur={() => setFocusField('')}
              onInput={(e) => setCode(e.detail.value)}
            />
            <Button
              className={classnames(styles.codeBtn, countdown > 0 && styles.disabled)}
              onClick={handleSendCode}
            >
              {countdown > 0 ? `${countdown}s` : '获取验证码'}
            </Button>
          </View>
        </View>

        <Button className={styles.loginBtn} onClick={handleLogin}>
          登录
        </Button>

        <View className={styles.agreement} onClick={() => setAgreed(!agreed)}>
          <View className={classnames(styles.checkbox, agreed && styles.checked)}>
            {agreed && <Text className={styles.checkIcon}>✓</Text>}
          </View>
          <Text className={styles.agreementText}>
            我已阅读并同意
            <Text className={styles.link}>《用户协议》</Text>
            和
            <Text className={styles.link}>《隐私政策》</Text>
          </Text>
        </View>
      </View>

      <View className={styles.otherLogin}>
        <View className={styles.otherTitle}>
          <View className={styles.line} />
          <Text className={styles.otherText}>其他登录方式</Text>
          <View className={styles.line} />
        </View>
        <View className={styles.otherIcons}>
          <View className={styles.otherItem} onClick={handleWechatLogin}>
            <View className={styles.otherIcon}>💬</View>
            <Text className={styles.otherName}>微信</Text>
          </View>
        </View>
      </View>
    </View>
  )
}

export default LoginPage
