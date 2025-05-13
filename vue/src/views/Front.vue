<template>
  <div style="background-color: #f7fdfd">
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
              <el-dropdown-item @click="router.push('/manager/introduction')">发布攻略</el-dropdown-item>
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

    <!-- 数据渲染区域开始 -->
    <div style="margin-bottom: 20px">
      <el-carousel
          height="600px"
      >
        <el-carousel-item v-for="item in data.introductionData" :key="item">
          <div style="position: relative; cursor: pointer;" @click="navTo('/front/introductionDetail?id=' + item.id)">
            <img :src="item.img" style="height: 600px; width: 100%;"/>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div style="width: 80%; margin: auto;">

      <div style="width: 100%; margin: auto">
        <div
            style="font-size: 20px; line-height: 30px; height: 30px; border-left: 2px solid #36e8fa; padding-left: 5px">
          旅游攻略
        </div>
        <div class="content" @click="navTo('/front/introductionDetail?id=' + item.id)"
             style="margin-top: 20px; grid-gap: 20px; display: flex; cursor: pointer"
             v-for="item in data.introductionData">
          <div style="flex: 1">
            <img :src="item.img" style="width: 100%; height: 250px; display: block; border-radius: 5px">
          </div>
          <div style="flex: 3">
            <div class="title" style="font-size: 20px; font-weight: bold">{{ item.title }}</div>
            <div class="line5"
                 style="margin-top: 10px; font-size: 15px; color: #666666; line-height: 30px; height: 150px; text-align: justify">
              {{ item.description }}
            </div>
            <div style="display: flex; align-items: center; grid-gap: 10px; margin-top: 20px">
              <img :src="item.userAvatar" style="width: 30px; height: 30px; border-radius: 10px">
              <div style="font-size: 15px">{{ item.userName }}</div>
              <div style="color: #666666;">{{ item.time }}</div>
              <div>
                <el-icon>
                  <Star/>
                </el-icon>
                {{ item.collectionCount }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
          style="margin-top: 20px; font-size: 20px; line-height: 30px; height: 30px; border-left: 2px solid #e65656; padding-left: 5px">
        旅游攻略
      </div>
      <div style="margin-top: 20px">
        <el-row :gutter="20">
          <el-col :span="6" style="justify-content: center; margin-bottom: 20px" v-for="item in data.introductionData">
            <div @click="navTo('/front/introductionDetail?id=' + item.id)" style="cursor: pointer;">
              <img :src="item.img" style="width: 100%; height: 250px; border-radius: 10px">
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
</template>

<script setup>
import {reactive} from 'vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}"),
  introductionData: []
})

const logout = () => {
  localStorage.removeItem("user");
  location.href = "/login";
}

const loadIntroduction = () => {
  request.get('/introduction/selectAll', {}).then(res => {
    if (res.code === '200') {
      data.introductionData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadIntroduction()
const navTo = (url) => {
  location.href = url;
}
</script>
<style>
.line5 {
  work-break: break-all;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 5; /* 超出几行省略 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

*:focus {
  outline: none;
}

.el-carousel__item {
  line-height: 200px;
  margin: 0;
  text-align: center;
}

.el-menu-item {
  position: relative;
  transition: background-color 0.3s ease; /* 添加过渡效果 */
}

.content {
  transition: all 0.3s ease;
}

.content:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); /* 添加阴影效果 */
  transform: translateY(-5px); /* 向上移动 */
}
.content:hover .title {
  color: #FF6600; /* 标题醒目颜色 */
}


.el-menu-item::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 3px; /* 颜色块高度 */
  background-color: #000; /* 颜色块颜色 */
  opacity: 0; /* 初始隐藏 */
  transition: opacity 0.3s ease; /* 添加过渡效果 */
}

.el-dropdown {
  cursor: pointer;
}

.el-menu-item:hover::after {
  opacity: 1; /* 鼠标移动时显示 */
}

.tourist:hover {
  border-bottom: #00b3c1 1px solid;
  cursor: pointer;
  color: #00b3c1;
}

.el-menu-item.is-active {
  background-color: #c4f0f6; /* 选中时的背景颜色 */
}
</style>
