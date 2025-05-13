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

    <div style="position: relative; margin: 0; padding: 0">
      <img :src="data.introductionData.img" style="width: 100%; height: 600px; display: block; margin: 0; padding: 0;"/>
      <div style="position: absolute; bottom: 50px; left: 460px; display: flex; align-items: center;">
        <div style="margin-right: 10px">
          <img :src="data.introductionData.userAvatar" @click="navTo('/front/personalPage?id=' + data.introductionData.userId)" style="cursor: pointer; width: 120px; height: 120px; border-radius: 50%"/>
        </div>
      </div>
      <div style="position: absolute; bottom: 120px; left: 620px; display: flex; align-items: center">
        <div style="display: flex; align-items: center;">
          <span style="color: white; font-size: 28px; font-weight: bold">{{ data.introductionData.title }}</span>
        </div>
      </div>
      <div style="padding: 20px 20px; background-color: rgba(255,255,255,0.66); border-bottom: 1px solid #ccc; box-shadow: 0 4px 4px -2px rgba(0, 0, 0, 0.3); height: 80px">
        <div style="margin-bottom: 20px; display: flex; align-items: center; justify-content: center; grid-gap: 20px">
          <div class="username" style="font-size: 14px; display: flex; align-items: center;margin-left: 10px; margin-right: 10px;">
          <div  @click="navTo('/front/personalPage?id=' + data.introductionData.userId)" style="color: #36e8fa">{{ data.introductionData.userName }}</div>
          </div>
          <div style="color: #666666; margin-right: 150px; font-size: 13px">{{ data.introductionData.time }}</div>
          <div style="margin-left: 50px">所属类型: {{ data.introductionData.categoryTitle }}</div>
          <div style="margin-left: 10px"><el-button @click="handleClick" :style="{ backgroundColor: isCollected ? 'gold' : '' }" circle><el-icon><Star /></el-icon></el-button>{{data.introductionData.collectionCount}}</div>
        </div>
      </div>
    </div>

    <div class="travel-info-container">
      <div class="info-item">
        <el-icon><Clock /></el-icon>
        <span>出发时间</span>
        <span>/</span>
        <span>{{ formatDate(data.introductionData.startTime) }}</span>
      </div>
      <div class="info-item">
        <el-icon><MoonNight /></el-icon>
        <span>出行天数</span>
        <span>/</span>
        <span>{{ data.introductionData.duration }}天</span>
      </div>
      <div class="info-item">
        <el-icon><Coin /></el-icon>
        <span>人均费用</span>
        <span>/</span>
        <span>{{ data.introductionData.cost }}元</span>
      </div>
    </div>

    <div style="width: 65%; margin: 10px auto">
      <div v-html="data.introductionData.content" style="margin-top: 30px; padding: 0 50px"></div>
    </div>

  </div>
</template>




<script setup>
import {reactive, ref} from "vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import request from "@/utils/request.js";

const isCollected = ref(false);

const handleClick = () => {
  isCollected.value = !isCollected.value;

}
const navTo = (url) => {
  location.href = url;
}

const formatDate = (dateStr) => {
  if (dateStr) {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  return '';
}

const logout = () => {
  localStorage.removeItem("user");
  location.href = "/login";
}
const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}"),
  introductionId: router.currentRoute.value.query.id,
  introductionData: {}
})

const loadIntroduction = () => {
  request.get('/introduction/selectById/' + data.introductionId).then(res => {
    if (res.code === '200') {
      data.introductionData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadIntroduction()

</script>

<style scoped>
.username :hover {
  border-bottom: #36e8fa 1px solid;
  transform: scale(1.05); /* 放大 1.2 倍 */
  cursor: pointer;
}
.travel-info-container {
  padding: 0 10px;
  display: flex;
  width: 650px;
  height: 120px;
  border: 1px dashed #aac2f6;
  margin: 20px auto;
  justify-content: space-between; /* 让子元素一左一右一中分布 */
  align-items: center; /* 垂直居中 */
}

*:focus {
  outline: none;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666666;
}

.info-item el-icon {
  font-size: 20px;
  color: #aac2f6;
  margin-right: 5px;
}

.info-item span:nth-child(3) {
  color: #aac2f6;
  margin: 0 5px;
}
</style>