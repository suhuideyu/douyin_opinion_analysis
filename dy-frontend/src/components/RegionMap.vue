<template>
  <div ref="chartRef" class="chart-box"></div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({ data: { type: Array, default: () => [] } })
const chartRef = ref()
let chart = null

const NAME_MAP = {
  '北京': '北京市', '天津': '天津市', '河北': '河北省', '山西': '山西省', '内蒙古': '内蒙古自治区',
  '辽宁': '辽宁省', '吉林': '吉林省', '黑龙江': '黑龙江省', '上海': '上海市', '江苏': '江苏省',
  '浙江': '浙江省', '安徽': '安徽省', '福建': '福建省', '江西': '江西省', '山东': '山东省',
  '河南': '河南省', '湖北': '湖北省', '湖南': '湖南省', '广东': '广东省', '广西': '广西壮族自治区',
  '海南': '海南省', '重庆': '重庆市', '四川': '四川省', '贵州': '贵州省', '云南': '云南省',
  '陕西': '陕西省', '甘肃': '甘肃省', '青海': '青海省', '宁夏': '宁夏回族自治区', '新疆': '新疆维吾尔自治区',
  '西藏': '西藏自治区', '香港': '香港特别行政区', '澳门': '澳门特别行政区', '中国台湾': '台湾省',
}

function initChart() {
  if (!chartRef.value) return
  if (chart) chart.dispose()
  chart = echarts.init(chartRef.value)

  const names = props.data.slice(0, 12).map(d => d.region)
  const values = props.data.slice(0, 12).map(d => d.count)

  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B', fontSize: 12 } },
    grid: { left: '3%', right: '10%', top: '3%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', axisLabel: { show: false }, splitLine: { show: false }, axisLine: { show: false }, axisTick: { show: false } },
    yAxis: { type: 'category', data: [...names].reverse(), axisLabel: { show: false }, axisLine: { show: false }, axisTick: { show: false } },
    series: [{
      type: 'bar', data: [...values].reverse(),
      itemStyle: {
        borderRadius: [0, 4, 4, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#99F6E4' }, { offset: 1, color: '#0D9488' }
        ])
      },
      label: { show: true, position: 'right', fontSize: 12, color: '#64748B', formatter: '{b}  {c}' },
    }]
  })

  loadMap()
}

async function loadMap() {
  try {
    const res = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
    const geoJson = await res.json()
    echarts.registerMap('china', geoJson)

    const mapData = []
    for (const d of props.data) {
      mapData.push({ name: NAME_MAP[d.region] || d.region, value: d.count })
    }

    chart.setOption({
      tooltip: { trigger: 'item', formatter: function(p) { return p.name + '<br/>评论数：' + (p.value || 0) + ' 条' } },
      series: [{
        type: 'map', map: 'china', roam: false, zoom: 1.2, center: [104, 36],
        top: '5%', left: '5%', right: '5%', bottom: '5%',
        data: mapData,
        itemStyle: { areaColor: '#E6FFFA', borderColor: '#CCFBF1', borderWidth: 1 },
        emphasis: { itemStyle: { areaColor: '#0D9488' }, label: { show: true, color: '#fff' } },
        label: { show: false },
      }]
    })
  } catch (e) { /* keep bar chart */ }
}

watch(() => props.data, initChart, { deep: true })
onMounted(initChart)
onBeforeUnmount(() => { if (chart) chart.dispose() })
</script>
