<template>
  <div>
    <div v-if="data.user.role === 'USER'" class="card">你好同学!欢迎使用本系统</div>
    <div v-if="data.user.role === 'USER'" class="card" style="margin-top: 10px">
      <el-collapse accordion>
        <div class="card" style="height: 400px; width: 50%" id="bar"></div>
        <el-collapse-item :title="item.title" v-for="item in data.noticeData"  placement="top">
          <div>{{ item.content }}</div>
          <p>{{ item.time }}</p>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div v-else>
      <div class="card" style="height: 400px; width: 100%" id="line"></div>
      <div style="gap: 10px; display: flex">
      <div class="card" style="height: 400px; margin-top: 10px; width: 50%" id="pie"></div>
      <div class="card" style="text-align: center; justify-content: center; align-items: center; height: 400px; margin-top: 10px;; width: 50%" id="">
        <img style="width: 200px; margin-top: 10%; height: 200px;" src="@/assets/imgs/bangbu.gif" alt="">
      </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {onMounted, reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from 'echarts';
const data = reactive({
  user: JSON.parse(localStorage.getItem('user') || ' {}'),
  noticeData: []
})

const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    if(res.code == '200'){
      data.noticeData = res.data;
      if (data.noticeData.length > 6){
        data.noticeData = data.noticeData.slice(0, 6);
      }
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadNotice()

const loadPie = () => {
  request.get('/echarts/pie').then(res => {
    if (res.code == '200'){
      let chartDom = document.getElementById('pie')
      let myChart = echarts.init(chartDom)
      pieOptions.series[0].data = res.data
      myChart.setOption(pieOptions)
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const loadLine = () => {
  request.get('/echarts/line').then(res => {
    if (res.code == '200'){
      let chartDom = document.getElementById('line')
      let myChart = echarts.init(chartDom)
      lineOptions.xAxis.data = res.data.xAxis
      //将每日各攻略发布数量和总量统计
      lineOptions.legend.data.push("总数")
      res.data.title.forEach(title => {
        lineOptions.legend.data.push(title)
        lineOptions.series.push({
          name: title,
          type: 'line',
          smooth: true,
          stack: 'Total',
          data: res.data[title]
        });
      });
      lineOptions.series.push({
        name: '总数',
        type: 'line',
        smooth: true,
        stack: 'Total',
        data: res.data.total
      });

      myChart.setOption(lineOptions)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadBar = () => {
  request.get('/echarts/bar').then(res => {
    if (res.code == '200'){
      let chartDom = document.getElementById('bar')
      let myChart = echarts.init(chartDom)
      //数据衔接
      const keys = Object.keys(res.data);
      const values = Object.values(res.data);
      // 假设第一个值为总数
      const totalValue = values[0];
      const placeholderData = [];
      let accumulatedValue = 0;
      for (let i = 0; i < values.length; i++) {
        if (i === 0) {
          placeholderData.push(0);
        } else {
          // 计算当前占位高度
          const currentPlaceholder = totalValue - accumulatedValue - values[i];
          placeholderData.push(currentPlaceholder);
          // 更新累计值
          accumulatedValue += values[i];
        }
      }
      barOptions.xAxis.data = keys
      barOptions.series[0].data = placeholderData
      barOptions.series[1].data = values;
      myChart.setOption(barOptions)

    } else {
      ElMessage.error(res.msg)
    }
  })
}
onMounted(()=>{
  loadPie()
  loadBar()
  loadLine()
})

let pieOptions = {
  title: {
    text: '所有攻略数',
    left: 'left'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: '景区类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 30,
        }
      },
      labelLine: {
        show: false
      },
      data: [
      ]
    }
  ]
}
let lineOptions = {
  title: {
    text: '最近一周的攻略数'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: []
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [],

};
let barOptions = {
  title: {
    text: '已发布攻略',
    subtext: ''
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    },
    formatter: function (params) {
      let tar = params[1];
      return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value;
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    splitLine: { show: false },
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: 'Placeholder',
      type: 'bar',
      stack: 'Total',
      itemStyle: {
        borderColor: 'transparent',
        color: 'transparent'
      },
      emphasis: {
        itemStyle: {
          borderColor: 'transparent',
          color: 'transparent'
        }
      },
      data: []
    },
    {
      name: 'Life Cost',
      type: 'bar',
      stack: 'Total',
      label: {
        show: true,
        position: 'inside'
      },
      data: []
    }
  ]
}
</script>
