<template>
  <div class="card">
    <div style="font-size: 15px">个人信息</div>
    <el-form ref="formRef" :model="data.user" label-width="80px"
             style="padding: 20px 30px 30px 0">  <!-- 上右下左 -->
      <el-form-item prop="username" label="账号">
        <el-input v-model="data.user.username" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="name" label="名称">
        <el-input v-model="data.user.name" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="phone" label="电话">
        <el-input v-model="data.user.phone" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="email" label="邮箱">
        <el-input v-model="data.user.email" autocomplete="off"/>
      </el-form-item>
      <el-form-item prop="avatar" label="头像">
        <el-upload
            :action="$baseUrl + '/files/upload'"
            :headers="{ token: data.user.token }"
            :on-success="handleFileSuccess"
            list-type="picture"
        >
          <el-button type="primary">上传头像</el-button>
        </el-upload>
      </el-form-item>
      <div style="text-align: center">
        <el-button type="primary" style="padding: 20px 30px" @click="update">保存</el-button>
      </div>
    </el-form>
  </div>
</template>
<script setup>
import {getCurrentInstance, reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  user: JSON.parse(localStorage.getItem('user') || ' {}'),

})

const { appContext } = getCurrentInstance();
const $baseUrl = appContext?.config.globalProperties.$baseUrl;

const emit = defineEmits(['updateUser']);

const handleFileSuccess = (res) => {
  data.user.avatar = res.data;
}
const update = () => {
  let url
  if (data.user.role === "ADMIN") {
    url = '/admin/update'
  }
  if (data.user.role === "USER") {
    url = '/user/update'
  }
  request.put(url, data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('更新成功')
      localStorage.setItem('user', JSON.stringify(data.user))
      router.push('/manager/home')
      emit('updateUser')
    } else {
      ElMessage.error(res.msg);
    }
  })
}
</script>
