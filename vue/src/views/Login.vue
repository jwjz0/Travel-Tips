<template>
  <div class="bg">
    <div
        style="box-shadow: 0 0 10px rgba(0,174,244,0.8);border-radius: 25px;margin-bottom: 15% ;width:450px; height: 500px;background-color: rgba(204,231,248,0.76);padding:40px"
    >
      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <div style="margin: 30px 0; text-align: center; font-weight: normal;font-size: 19px">登 陆</div>
        <el-form-item prop="username">
          <el-input
              size="large"
              placeholder="账号"
              v-model="data.form.username"
              autocomplete="off"
              prefix-icon="ColdDrink"
              @input="checkInput"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              size="large"
              show-password
              placeholder="密码"
              style="margin-top: 10px"
              v-model="data.form.password"
              autocomplete="off"
              prefix-icon="Lock"
              @input="checkInput"
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select size="large" style="margin-top: 10px;width: 100%; "v-model="data.form.role">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="普通用户" value="USER"></el-option>
          </el-select>
        </el-form-item>
        <div>
          <el-button
              style="border-radius: 30px;height: 60px;width: 20%;margin-top: 20px;margin-left: 40%"
              size="large"
              type="primary"
              :disabled="isDisabled"
              @click="login"
          >
            <el-icon size="large">
              <Right />
            </el-icon>
          </el-button>
        </div>
        <div style="font-size: 14px;margin-top: 20px;text-align: right">
          没有账号?请<a style="font-weight: bold;color: #1793ca" href="/register">注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watchEffect } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import router from "@/router/index.js";

const formRef = ref();

const data = reactive({
  form: {
    role:'ADMIN',
    username: "",
    password: "",
  },
  rules: {
    username: [
      { required: true, message: "请输入账号", trigger: "blur" },
    ],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
    ],
  },
});

const isDisabled = ref(true);

const checkInput = () => {
  isDisabled.value = !data.form.username || !data.form.password;
};

// 初始检查
watchEffect(() => {
  checkInput();
});

const login = () => {
  formRef.value.validate( (valid) => {
    if (valid) {
      request.post("/login", data.form).then((res) => {
        if (res.code === "200") {
          // 存储用户信息
          localStorage.setItem("user", JSON.stringify(res.data || {}));
          ElMessage.success("登陆成功");
          if ("USER" === res.data.role) {
            router.push("/front");
          } else {
            router.push("/");
          }
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  })
};
</script>

<style scoped>
.bg {
  display: flex;
  height: 100vh;
  overflow: hidden;
  justify-content: center;
  background-image: url("@/assets/imgs/bg1.png");
  background-size: cover;
  align-items: center;
}
</style>