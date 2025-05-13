<template>
  <div style="background-color: #eff5fa">
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

    <!-- 个人主页图片 -->
    <div>
      <img style="width: 100%; height: 500px; background-color: #abe9f3; margin-bottom: 25px"
           src="@/assets/imgs/img.png"/>
    </div>

    <!-- 个人信息 -->
    <div style="height: 100vh; grid-gap: 50px; display: flex;justify-content: center;">
      <!-- 个人头像、个人昵称、发布数量 -->
      <div class="card" style="width: 300px; height: 400px; justify-content: center; padding:0">
        <div>
          <img :src="data.currentUser.avatar"
               style="margin-top: 50px; margin-left: 100px; width: 100px; height: 100px; border-radius: 50%">
        </div>
        <div style="margin-top: 10px; text-align: center; font-size: 20px">{{ data.currentUser.name }}</div>
        <div style="text-align: center; margin-top: 6px; font-size: 15px; color: #666666">
          发布数量:{{ data.introductionData.length }}
        </div>
        <div v-if="data.user.id !== data.currentUser.id" style="text-align: center; margin-top: 10px; font-size: 15px; color: #666666; display: flex; justify-content: center;">
          <el-button @click="follow" style="padding: 5px 8px"><el-icon><Plus /></el-icon>关注</el-button>
          <el-button @click="msg" style="padding: 5px 8px"><el-icon><ChatLineRound /></el-icon>私信</el-button>
        </div>
        <div style="text-align: center; margin-top: 30px; font-size: 15px; color: #666666; display: flex; justify-content: center;grid-gap: 80px">
          <div>
            <div>
              111
            </div>
            <div style="color: #666666">
              关注
            </div>
          </div>
          <div>
            <div>
              222
            </div>
            <div>
              粉丝
            </div>
          </div>
        </div>
      </div>

      <!-- 个人帖子 -->
      <div class="card" style="width: 1280px">
        <div>
          <el-row :gutter="20">
            <el-col :span="6" style="justify-content: center; margin-bottom: 20px"
                    v-for="item in data.introductionData">
              <div @click="navTo('/front/introductionDetail?id=' + item.id)" style="cursor: pointer;">
                <img :src="item.img" style="width: 300px; height: 250px; border-radius: 10px">
                <div style="font-size: 16px; font-weight: bold">{{ item.title }}</div>
                <div style="display: flex; align-items: center; grid-gap: 10px; line-height: 30px; height: 30px">
                  <img :src="item.userAvatar" style="width: 30px;height: 30px; border-radius: 10px">
                  <div style="font-size: 15px">{{ item.userName }}</div>
                  <div style="color: #666666;">{{ item.time }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}"),
  currentUser: [],
  userId: router.currentRoute.value.query.id,
  introductionData: []
})

const loadIntroduction = () => {
  const userId = data.userId;
  request.get('/introduction/selectAll', {
    params: {
      userId: userId
    }
  }).then(res => {
    if (res.code === '200') {
      data.introductionData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadIntroduction()

const follow = () => {
  //关注
}

const msg = () => {
  //私信
}

const navTo = (url) => {
  location.href = url;
}

const logout = () => {
  localStorage.removeItem("user");
  location.href = "/login";
}

const loadUser = () => {
  request.get('/user/selectById/' + data.userId).then(res => {
    if (res.code === '200') {
      data.currentUser = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadUser()


</script>

<style scoped>
*:focus {
  outline: none;
}
</style>
