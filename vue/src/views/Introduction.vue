<template>
  <div>

    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 240px; margin-right:5px " v-model="data.title"
                :prefix-icon="Search" placeholder="请输入攻略标题查询"></el-input>
      <el-select clearable v-model="data.selectedCategoryTitle" placeholder="请选择景区类型"
                 style="margin-right: 5px; width: 240px" @change="load">
        <el-option
            v-for="item in data.categoryData"
            :key="item.id"
            :label="item.title"
            :value="item.title"
        />
      </el-select>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button @click="reset">重 置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="info" @click="handleAdd" v-if="data.user.role === 'USER'">发 布</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" style="  width: 100%"
                :header-cell-style="{color:'#333', backgroundColor:'#eaf4ff'}">
        <el-table-column label="攻略主图">
          <template #default="scope">
            <el-image v-if="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"
                      :src="scope.row.img" style="width: 50px; height: 50px; border-radius: 5px; display: block">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="攻略标题"/>
        <el-table-column label="出发时间">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="出行天数"/>
        <el-table-column prop="cost" label="人均花费"/>
        <el-table-column prop="categoryTitle" label="景区类型"/>
        <el-table-column prop="content" label="攻略内容">
          <template v-slot="scope">
            <el-button type="primary" @click="viewContent(scope.row.content)">点击查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="发布用户"/>
        <el-table-column prop="time" label="时间"/>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="primary" circle icon="Edit" @click="handleEdit(scope.row)"></el-button>
            <el-button type="danger" circle icon="Delete" @click="del(scope.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div>
      <el-pagination
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[5, 10, 20]"
          :total="data.total"
          @current-change="load"
          @size-change="load"
      />
    </div>

    <el-dialog title="旅游攻略信息" v-model="data.formVisible" width="60%" destroy-on-close>
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px"
               style="padding: 20px 30px 30px 0">  <!-- 上右下左 -->
        <el-form-item prop="img" label="攻略主图">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: data.user.token }"
              :on-success="handleFileSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="title" label="攻略标题">
          <el-input v-model="data.form.title" autocomplete="off"/>
        </el-form-item>
        <div style="display: flex">
          <el-form-item prop="startTime" label="出发时间">
            <div class="block">
              <el-date-picker
                  v-model="data.form.startTime"
                  type="date"
                  placeholder="选择一天"
                  size="default"
              />
            </div>
          </el-form-item>
          <el-form-item prop="duration" label="出行天数">
            <el-input v-model="data.form.duration" autocomplete="off"/>
          </el-form-item>
          <el-form-item prop="cost" label="人均花费">
            <el-input v-model="data.form.cost" autocomplete="off"/>
          </el-form-item>
        </div>
        <el-form-item prop="title" label="景区类型">
          <el-select v-model="data.form.categoryId" placeholder="选择景区类型" style="width: 240px">
            <el-option
                v-for="item in data.categoryData"
                :key="item.id"
                :label="item.title"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="content" label="攻略内容">
          <div style="border: 1px solid #ccc; width: 100%">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="data.form.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保 存</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="攻略信息" v-model="data.viewVisible" width="60%" style="margin-top: auto" destroy-on-close>
      <div v-html="data.content" style="padding: 0 20px"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import {getCurrentInstance, onBeforeUnmount, reactive, ref, shallowRef} from 'vue'
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import '@wangeditor/editor/dist/css/style.css'

const { appContext } = getCurrentInstance();
const $baseUrl = appContext?.config.globalProperties.$baseUrl;

const data = reactive({
  user: JSON.parse(localStorage.getItem("user") || "{}"),
  pageNum: 1,
  pageSize: 5,
  total: 0,
  tableData: [],
  title: null,
  content: null,
  viewVisible: false,
  selectedCategoryTitle: null,
  duration: null,
  cost: null,
  startTime: null,
  form: {},
  rules: {
    title: [
      {required: true, message: '请填写标题', trigger: 'blur'},
    ],
    content: [
      {required: true, message: '请填写内容', trigger: 'blur'}
    ],
    startTime: [
      {required: true, message: '请填写出发时间', trigger: 'blur'},
    ],
    duration: [
      {required: true, message: '请填写出行天数', trigger: 'blur'}
    ],
    cost: [
      {required: true, message: '请填写人均花费', trigger: 'blur'},
    ]
  },
  formVisible: false,
  categoryData: []
})

const editorRef = shallowRef()
const mode = 'default'
const editorConfig = {MENU_CONF: {}}

editorConfig.MENU_CONF['uploadImage'] = {
  headers: {
    token: data.user.token,
  },
  server: $baseUrl + '/files/wang/upload',
  fieldName: 'file'
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

const formRef = ref({})

const load = () => {
  request.get('/introduction/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      total: data.total,
      title: data.title,
      categoryTitle: data.selectedCategoryTitle,
      duration: data.duration,
      cost: data.cost,
      startTime: data.startTime
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list
      data.total = res.data?.total
    } else {
      ElMessage.error(res.msg)
    }
  })
}
load()

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

const viewContent = (content) => {
  data.content = content
  data.viewVisible = true
}
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true
}

const add = () => {
  request.post('/introduction/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success("新增成功")
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  request.put('/introduction/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success("更新成功")
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const save = () => {
  formRef.value.validate(valid => {
    if (valid) {
      data.form.id ? update() : add()
    }
  })
}

const del = (id) => {
  ElMessageBox.confirm('删除后与无法恢复，您确认删除吗？', '确认删除', {type: 'warning'}).then(res => {
    request.delete('/introduction/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
  })
}
const reset = () => {
  data.title = null
  data.content = null
  load()
}
const handleFileSuccess = (res) => {
  data.form.img = res.data;
}

const loadCategory = () => {
  request.get('/category/selectAll', {}).then(res => {
    if (res.code === '200') {
      data.categoryData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadCategory()
</script>
