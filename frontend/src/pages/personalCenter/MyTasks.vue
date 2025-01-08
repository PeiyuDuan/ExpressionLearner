<template>
  <el-table :data="myTasks" height="500" style="width: 100%" @row-click="navigateToTask" class="custom-table">
    <el-table-column 
      prop="id" 
      label="任务编号" 
      align="center"
      header-align="center"
      width=1000
    />
    <el-table-column 
      prop="taskName" 
      label="名称" 
      align="center"
      header-align="center"
    />
  </el-table>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import $ from 'jquery'
import {useStore} from "vuex";
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();
const myTasks = ref([])
const refreshMyTasks = () => {
  $.ajax({
    url: `http://localhost:3001/api/tasks/user/${store.state.user.id}`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token
    },
    success(resp) {
      if (resp.success === "true") {
        myTasks.value = resp.data;
        
      } else {
        console.log(resp);
        myTasks.value = resp;
        console.log(resp.data);
        console.log("234")
        console.log(myTasks.value);
      }
    },
    error(resp) {
      console.log(resp);
      myTasks.value = resp.data;
    }
  })
}
const navigateToTask = (row) => {
    router.push(`/personalCenter/${row.id}`);
  };
onMounted(()=>{
  refreshMyTasks()
})

</script>

<style scoped>
.custom-table :deep(.el-table__cell) {
  text-align: center;
}

.custom-table :deep(.el-table__header) th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}

.custom-table :deep(.el-table__row) {
  cursor: pointer;
}

.custom-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.custom-table :deep(.el-table__header-wrapper), 
.custom-table :deep(.el-table__body-wrapper) {
  margin: 0;
  padding: 0;
}

.custom-table :deep(.el-table__header) th,
.custom-table :deep(.el-table__cell) {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center; /* 确保文字居中 */
}

.custom-table :deep(.el-table__header .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.custom-table :deep(.el-table__body-wrapper .el-table__row) {
  display: flex;
  flex-wrap: nowrap; /* 让两列在同一行 */
}
.custom-table :deep(.el-table__cell) {
  flex: 1;
  text-align: center;
}

.custom-table :deep(.el-table__header-wrapper thead tr) {
  display: flex;
  flex-wrap: nowrap;
}

.custom-table :deep(.el-table__header-wrapper thead tr th) {
  flex: 1;
  text-align: center;
}
</style>
