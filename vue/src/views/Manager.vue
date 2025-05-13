<template>
  <div style="display: flex">
    <div>
      <div style="width:188px; background: #e9f9fd">
        <el-button @click="toggleCollapse" style="height: 43px ;width: 100%; border-bottom: 1px solid rgba(0, 0, 0, 0.1); border-radius: 0; background: #f8f9fa;">
          <span v-if="isCollapse">
            <el-icon size="large"><ArrowRightBold /></el-icon>
          </span>
          <span v-else>
            <el-icon size="large"><ArrowLeftBold /></el-icon>
          </span>
        </el-button>
        <el-menu
            :collapse="isCollapse"
            style="min-height: 100vh"
            router
        >
          <el-menu-item index="/manager/home">
            <template #title>首页</template>
            <el-icon>
              <House/>
            </el-icon>
          </el-menu-item>
          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <Monitor/>
              </el-icon>
              <span style="font-size: 15px">信息管理</span>
            </template>
              <el-menu-item class="centered-menu-item" index="/manager/notice">系统公告</el-menu-item>
              <el-menu-item class="centered-menu-item" index="/manager/introduction">旅游攻略</el-menu-item>
              <el-menu-item class="centered-menu-item" index="/manager/category" v-if="data.user.role === 'ADMIN'">景点类型</el-menu-item>
          </el-sub-menu>
          <el-sub-menu v-if="data.user.role === 'ADMIN'" index="2">
            <template #title>
              <el-icon>
                <user/>
              </el-icon>
              <span>用户管理</span>
            </template>
              <el-menu-item class="centered-menu-item" index="/manager/admin">管理员信息</el-menu-item>
              <el-menu-item class="centered-menu-item" index="/manager/user">普通用户信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </div>
    <div style="flex: 1">
      <div style="border-bottom: 1px solid rgba(0, 0, 0, 0.3);height:43px;display: flex;width:100%">
        <div style="flex: 1;display:flex; align-items:center;padding-left: 10px">
        <span style="margin-right: 5px;">
          <img @click="router.push('/front')" style="cursor: pointer; text-align: left; width: 40px; height: 40px" src="@/assets/imgs/Junimo.gif" alt="">
        </span> / <span style="margin-left: 5px">{{ router.currentRoute.value.meta.name }}</span>
        </div>
        <div style="margin-left: auto;width: fit-content;display: flex; padding-right: 10px">
          <el-dropdown>
            <div style="display: flex; align-items: center;">
              <span style="margin-left: 5px">{{ data.user?.name }}</span>
              <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%"
                   :src="data.user?.avatar"/>
              <img v-else style="width: 40px; height: 40px; border-radius: 50%" src="@/assets/imgs/avatar.png"/>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/personalPage')">个人页面</el-dropdown-item>
                <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div style="height: 100vh; display: flex;background-color: #e9f9fd">
        <div style="flex: 1;width: auto;margin: 10px;background-color: #e9f9fd">
          <RouterView @updateUser="updateUser"/>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>

import router from "@/router/index.js";
import {reactive, ref} from "vue";


const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}")
})

const isCollapse = ref(true)

const logout = () => {
  localStorage.removeItem("user");
  location.href = "/login";
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem("user") || '{}');
}

// 切换折叠状态的方法
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};
</script>

<style scoped>


.el-menu {
  background-color: #ffffff; /* 浅灰色背景，比白色更有层次感 */
}

.el-menu .is-active{
  color: #1890ff; /* 更明亮的蓝色，突出选中状态 */
}

.el-sub-menu__title:hover {
  background-color: #e6f7ff; /* 淡蓝色的悬停效果，看起来更柔和 */
}

.el-menu-item:not(.is-active):hover {
  background-color: #e6f7ff; /* 与子菜单悬停效果保持一致 */
}
.el-dropdown {
  cursor: pointer;
}

.el-tooltip__trigger {
  outline: none;
}

.centered-menu-item {
  height: 40px;
  font-size: 14px;
  display: flex;
}
</style>