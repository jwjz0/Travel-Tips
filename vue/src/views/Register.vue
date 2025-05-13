<template>
  <div class="bg">
    <div
        style="box-shadow: 0 0 10px rgba(234,209,2,0.71);border-radius: 25px;margin-bottom: 15% ;width:450px; height: 500px;background-color: rgba(240,246,220,0.76);padding:40px"
    >
      <el-form status-icon ref="formRef" :model="data.form" :rules="data.rules">  <!-- 上右下左 -->
        <div style="margin: 30px 0; text-align: center; font-weight: normal;font-size: 19px">注 册</div>
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
        <el-form-item prop="confirmPassword">
          <el-input
              size="large"
              show-password
              placeholder="请再次输入密码"
              style="margin-top: 10px"
              v-model="data.form.confirmPassword"
              autocomplete="off"
              prefix-icon="Lock"
              @input="checkInput"
          />
        </el-form-item>
        <div>
          <el-button
              style="border-radius: 30px;height: 60px;width: 20%;margin-top: 20px;margin-left: 40%"
              size="large"
              type="primary"
              :disabled="isDisabled"
              @click="register"
          >
            <el-icon size="large">
              <Right />
            </el-icon>
          </el-button>
        </div>
      </el-form>
      <div style="color: #122727;font-size: 14px;margin-top: 20px;font-weight: lighter ;text-align: right;align-items: center;">已拥有账号?返回<a style="color: #00b3c1;font-weight: bold" href="/login">登陆</a></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watchEffect } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import router from "@/router/index.js";

const validatePass = (rule, value, callback) => {
 if (value !== data.form.password) {
    callback(new Error("两次输入的密码不匹配!"))
  } else {
    callback()
  }
}

const formRef = ref();

const data = reactive({
  form: {
    username: "",
    password: "",
    confirmPassword: "",
  },
  rules: {
    username: [
      { required: true, message: "请输入账号", trigger: "blur" },
    ],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
    ],
    confirmPassword: [
      { required: true, message:"请再次确认密码", trigger: "blur" },
      { validator: validatePass, trigger: "blur" },
    ]
  },
});



const isDisabled = ref(true);

const checkInput = () => {
  isDisabled.value = !data.form.username || !data.form.password || !data.form.confirmPassword;
};

// 初始检查
watchEffect(() => {
  checkInput();
});

const register = () => {
  formRef.value.validate( (valid) => {
    if (valid) {
      request.post("/register", data.form).then((res) => {
        if (res.code === "200") {
          // 存储用户信息
          ElMessage.success("注册成功");
          router.push("/login");
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