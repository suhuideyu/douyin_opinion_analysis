<template>
  <div class="dashboard-page">
    <div class="video-selector">
      <span style="font-weight:600;color:var(--text-secondary);font-size:14px;">当前视频</span>
      <el-select v-model="appStore.currentVideo" @change="onVideoChange" style="width:280px;" size="large">
        <el-option v-for="v in videoList" :key="v.videoId" :label="v.videoId" :value="v.videoId" />
      </el-select>
      <el-button v-if="userStore.token" type="primary" @click="refreshData" size="large">刷新分析</el-button>
    </div>

    <StatsCards :summary="summary" />

    <div class="chart-row">
      <div class="chart-card"><div class="card-header"><h3>地区分布</h3></div><RegionMap :data="regionData" /></div>
      <div class="chart-card"><div class="card-header"><h3>情感分析</h3></div><SentimentPie :data="sentimentData" /></div>
    </div>

    <div class="chart-row" style="grid-template-columns:1fr;" v-if="userStore.token && (topicText || topicLoading)">
      <div class="chart-card">
        <div class="card-header"><h3>核心话题</h3></div>
        <div v-if="topicLoading" style="padding:20px;text-align:center;color:#999;font-size:14px;">
          🤔 AI 正在分析评论数据中，请稍候...
        </div>
        <pre v-else style="font-size:14px;line-height:2;color:var(--text-primary);white-space:pre-wrap;font-family:inherit;margin:0;">{{ topicText }}</pre>
      </div>
    </div>

    <div class="chart-row" style="grid-template-columns:1fr;">
      <div class="chart-card"><div class="card-header"><h3>词云</h3></div><WordCloud :data="wordcloudData" /></div>
    </div>

    <div class="chart-row" style="grid-template-columns:1fr;">
      <div class="chart-card"><div class="card-header"><h3>评论流向</h3></div><SankeyChart :data="sankeyData" /></div>
    </div>

    <div class="chart-row" style="grid-template-columns:1fr;">
      <div class="chart-card"><div class="card-header"><h3>评论时间趋势</h3></div><TrendLine :data="trendData" /></div>
    </div>

    <div class="card">
      <div class="card-header"><h3>高赞评论排行 Top 20</h3></div>
      <TopComments :data="topComments" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore, useAppStore } from '../stores/user'
import { analysisApi } from '../api'
import StatsCards from '../components/StatsCards.vue'
import RegionMap from '../components/RegionMap.vue'
import SentimentPie from '../components/SentimentPie.vue'
import TrendLine from '../components/TrendLine.vue'
import TopComments from '../components/TopComments.vue'
import WordCloud from '../components/WordCloud.vue'
import SankeyChart from '../components/SankeyChart.vue'

const userStore = useUserStore()
const appStore = useAppStore()
const videoList = ref([])
const summary = ref({}); const regionData = ref([]); const sentimentData = ref([])
const trendData = ref([]); const topComments = ref([])
const wordcloudData = ref([]); const sankeyData = ref({ nodes: [], links: [] }); const topicText = ref(''); const topicLoading = ref(false)

async function loadVideoList() {
  try {
    const res = await analysisApi.videoList(); videoList.value = res.data || []
  } catch (e) { /* */ }
}

async function loadAll() {
  if (!appStore.currentVideo) return
  const v = appStore.currentVideo
  if (userStore.token) {
    try { summary.value = (await analysisApi.summary(v)).data || {} } catch (e) { summary.value = {} }
    try { regionData.value = (await analysisApi.region(v)).data || [] } catch (e) { regionData.value = [] }
    try { sentimentData.value = (await analysisApi.sentiment(v)).data || [] } catch (e) { sentimentData.value = [] }
    try { trendData.value = (await analysisApi.trend(v)).data || [] } catch (e) { trendData.value = [] }
    try { topComments.value = (await analysisApi.topComments(v)).data || [] } catch (e) { topComments.value = [] }
    try { wordcloudData.value = (await analysisApi.wordcloud(v)).data || [] } catch (e) { wordcloudData.value = [] }
    try { sankeyData.value = (await analysisApi.sankey(v)).data || { nodes: [], links: [] } } catch (e) { sankeyData.value = { nodes: [], links: [] } }
    topicLoading.value = true
    try { topicText.value = (await analysisApi.topic(v)).data || '' } catch (e) { topicText.value = '' }
    finally { topicLoading.value = false }
  }
}

function onVideoChange() { loadAll() }
async function refreshData() { if (!appStore.currentVideo) return; await analysisApi.refresh(appStore.currentVideo); loadAll() }

onMounted(async () => { await loadVideoList(); if (appStore.currentVideo) loadAll() })
</script>
