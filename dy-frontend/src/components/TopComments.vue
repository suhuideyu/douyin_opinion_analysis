<template>
  <el-table :data="data" stripe border style="width:100%;" max-height="500">
    <el-table-column type="index" label="#" width="50" align="center" />
    <el-table-column prop="nickname" label="昵称" width="140" />
    <el-table-column prop="region" label="地区" width="80" />
    <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
    <el-table-column prop="likes" label="点赞" width="90" align="center" sortable />
    <el-table-column prop="publishTime" label="时间" width="160" />
  </el-table>
</template>

<script setup>
defineProps({ data: { type: Array, default: () => [] } })
</script>
