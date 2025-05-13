<template>
  <div>
    <!-- 头部区域开始 -->
    <div
        style="position: fixed; top: 0; left: 0; z-index: 100;border-bottom: 1px solid #ccc; box-shadow: 0 4px 4px -2px rgba(0, 0, 0, 0.3);; display: flex; justify-content: center; align-items: center; width: 100%; height: 58px; background-color: #ffffff">
      <div>
        <el-menu default-active='/front' style="margin-left: 100px; display: flex; border-right: none" router>
          <el-menu-item index="/front">首页</el-menu-item>
          <el-menu-item v-if="'ADMIN' === data.user.role" @click="router.push('/manager/home')">后台系统
          </el-menu-item>
        </el-menu>
      </div>
      <div v-if="!data.user.role"
           style="margin-left: auto; margin-right: 100px; width: fit-content;display: flex; padding-right: 10px; align-items: center">
        <div style="display: flex; align-items: center;font-size: 15px; grid-gap: 6px">
          <div class="tourist" @click="router.push('/login')">登录</div>
          <div style="color: #36e8fa">/</div>
          <div class="tourist" @click="router.push('/register')">注册</div>
        </div>
      </div>
      <div v-else
           style="margin-left: auto; margin-right: 100px; width: fit-content;display: flex; padding-right: 10px; align-items: center">
        <el-dropdown>
          <div  style="display: flex; align-items: center;">
            <span style="margin-left: 5px">{{ data.user?.name }}</span>
            <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%"
                 :src="data.user?.avatar"/>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navTo('/front/personalPage?id=' + data.user.id)">个人页面</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/person')">编辑信息</el-dropdown-item>
              <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-badge @click="router.push('/front/personalMsg')" style="cursor: pointer; margin-left: 10px" class="item"
                  is-dot :offset="[5, 5]">
          <el-icon size="large">
            <Bell/>
          </el-icon>
        </el-badge>
      </div>
    </div>
    <!-- 头部区域结束 -->

    <!-- 数据渲染 -->
    <div class="card" style="height: 800px; width: 1000px; display: flex; margin: 80px auto; border: 1px solid #ccc; border-radius: 8px; overflow: hidden;">
      <!-- 聊天用户选择列表区域 -->
      <div class="user-list" style="width: 200px; border-right: 1px solid #ccc; padding: 10px; overflow-y: auto;">
        <div class="user-item" @click="selectChatUser({ id: -1, name: '系统通知', avatar: '@/assets/imgs/avatar.png' })">
          <img src="@/assets/imgs/avatar.png" alt="系统通知头像" class="user-avatar">
          <span class="user-name">系统通知</span>
        </div>
        <div v-for="(user, index) in data.users" :key="index" class="user-item" @click="selectChatUser(user)">
          <img :src="user.avatar || '@/assets/imgs/avatar.png'" alt="用户头像" class="user-avatar">
          <span class="user-name">{{ user.name }}</span>
        </div>
      </div>
      <!-- 聊天区域 -->
      <div class="chat-container" style="flex: 1; display: flex; flex-direction: column;">
        <!-- 聊天记录显示区域 -->
        <div class="chat-history" style="flex: 1; overflow-y: auto; padding: 10px;">
          <div v-for="(message, index) in currentChatMessages" :key="index" class="message">
            <img v-if="message.sender!== data.user.id" src="@/assets/imgs/avatar.png" alt="对方头像" class="avatar">
            <div class="message-content" :class="{ 'own-message': message.sender === data.user.id }">
              <span>{{ message.content }}</span>
            </div>
            <img v-if="message.sender === data.user.id" :src="data.user.avatar" alt="当前用户头像" class="avatar">
          </div>
        </div>
        <!-- 输入框和发送按钮 -->
        <div class="chat-input" style="display: flex; padding: 5px; border-top: 1px solid #ccc;">
          <el-input
              v-model="newMessage"
              placeholder="请输入消息"
              style="flex: 1; margin-right: 10px; height: 60px; padding: 8px; display: flex; align-items: flex-start; box-sizing: border-box;"              @keyup.enter="sendMessage"
              ref="inputRef"
          ></el-input>
          <el-button style="height: 60px; width: 60px" @click="sendMessage"><el-icon size="large"><Position /></el-icon></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import router from "@/router/index.js";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}"),
  users: []
})

const loadUsers = () => {
  request.get('/user/selectAll').then(res => {
    if (res.code === '200') {
      data.users = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
}
loadUsers();

// 系统通知默认的两条静态信息
const systemNotificationMessages = ref([
  { sender: -1, content: '您有新的私信消息，请查收！' },
  { sender: -1, content: '有人收藏了您的攻略，快去看看吧！' }
])

// 当前聊天对象
const currentChatUser = ref(null);
// 当前聊天记录
const currentChatMessages = ref([]);

// 选择聊天用户的方法
const selectChatUser = (user) => {
  currentChatUser.value = user;
  if (user.id === -1) {
    currentChatMessages.value = systemNotificationMessages.value;
  } else if (user.id === 2) {
    // 假设默认聊天对象的 id 为 2
    // 这里可以根据实际情况添加默认聊天对象的消息逻辑
    currentChatMessages.value = [];
  } else {
    currentChatMessages.value = [];
  }
}

// 新消息输入框绑定的值
const newMessage = ref('')
// 输入框的 ref
const inputRef = ref(null)

// 发送消息的方法
const sendMessage = () => {
  if (newMessage.value.trim()) {
    const newMsg = { sender: data.user.id, content: newMessage.value };
    currentChatMessages.value.push(newMsg);
    newMessage.value = '';

    // 判断当前聊天对象是否为系统通知
    if (currentChatUser.value && currentChatUser.value.id === -1) {
      const systemReply = { sender: -1, content: '你好呀' };
      currentChatMessages.value.push(systemReply);
    }

    // 消息发送后让输入框保持聚焦
    inputRef.value.focus()
  }
}

onMounted(() => {
  // 页面加载时自动选择系统通知
  selectChatUser({ id: -1, name: '系统通知', avatar: '@/assets/imgs/avatar.png' });
  // 页面加载时让输入框聚焦
  inputRef.value.focus()
});
</script>

<style scoped>
.message {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.message-content {
  padding: 8px 12px;
  border-radius: 8px;
  max-width: 70%;
  min-height: 40px;
  display: flex;
  align-items: center;
  margin: 0 5px;
}

.message-content.own-message {
  background-color: #d9fdd3;
  margin-left: auto;
}

.message-content:not(.own-message) {
  background-color: #e4f4ff;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-list {
  background-color: #f9f9f9;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-item:hover {
  background-color: #eaeaea;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
}

.chat-container {
  background-color: #fff;
}
</style>
