<template>
  <div class="page-container">
    <h3 class="page-title">数据采集</h3>
    <div class="card">
      <div style="max-width:560px;">
        <div style="display:flex;gap:12px;margin-bottom:12px;">
          <el-input v-model="videoId" placeholder="输入抖音视频链接或编号" size="large" style="flex:1;" />
          <el-input-number v-model="maxComments" :min="100" :max="1000" :step="100" size="large" style="width:140px;" />
          <el-button type="primary" @click="startCollect" :loading="collecting" size="large">开始采集</el-button>
        </div>
        <div v-if="statusMsg" style="margin-top:12px;">
          <el-alert :title="statusMsg" :type="statusType" show-icon :closable="false" />
        </div>
      </div>
    </div>
    <div class="card" style="color:var(--text-secondary);font-size:13px;line-height:2;">
      <div class="card-header"><h3>说明</h3></div>
      <ul style="padding-left:18px;">
        <li>请输入抖音视频链接或者纯19位数字视频编号，例如https://v.douyin.com/FF2WhCr4l-Q/或者7613237683142223214</li>
        <li>采集过程约 1~3 分钟</li>
        <li>请前往「数据清洗」处理数据</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { collectApi } from '../api'

const videoId = ref('')
const maxComments = ref(300)
const collecting = ref(false)
const statusMsg = ref('')
const statusType = ref('info')

async function startCollect() {
  if (!videoId.value) { statusMsg.value = '请输入视频编号'; statusType.value = 'warning'; return }
  collecting.value = true
  statusMsg.value = '采集中…'
  statusType.value = 'info'
  try {
    await collectApi.start({ videoId: videoId.value, maxComments: maxComments.value })
    pollStatus()
  } catch (e) { collecting.value = false; statusMsg.value = '启动失败'; statusType.value = 'error' }
}

async function pollStatus() {
  const timer = setInterval(async () => {
    try {
      const res = await collectApi.status(videoId.value)
      const s = res.data.status
      if (s === 'SUCCESS') { statusMsg.value = '采集完成！'; statusType.value = 'success'; collecting.value = false; clearInterval(timer) }
      else if (s !== 'RUNNING') { statusMsg.value = '采集失败: ' + s; statusType.value = 'error'; collecting.value = false; clearInterval(timer) }
    } catch (e) { clearInterval(timer); collecting.value = false }
  }, 3000)
}
</script>
