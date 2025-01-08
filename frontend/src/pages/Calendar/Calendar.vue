<template>
  <div class="todo-container">
    <el-card v-for="todo in todoList" 
             :key="todo.id" 
             class="todo-card">
      <template #header>
        <div class="card-header">
          <h2>今日待办任务</h2>
          <el-tag type="primary">{{ formatDate(todo.creationDate) }}</el-tag>
        </div>
      </template>
      
      <div class="task-content">
        <div class="task-header">
          <h3 class="task-title">{{ todo.taskName }}</h3>
          <el-button 
            type="primary" 
            class="study-button"
            @click="goToTaskDetail(todo.id)"
          >
            开始学习
          </el-button>
        </div>
        
        <div class="images-row">
          <el-image 
            v-for="image in todo.images.sort((a, b) => a.imageOrder - b.imageOrder)"
            :key="image.id"
            :src="image.imageUrl"
            fit="cover"
            class="row-image"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore();

const userId = Number(store.state.user.id); // 获取用户ID

interface Image {
  id: number
  imageUrl: string
  imageOrder: number
}

interface TodoItem {
  id: number
  taskPrompt: Record<string, unknown>
  creationDate: string
  images: Image[]
  imageDescription: string
  taskName: string
}

const todoList = ref<TodoItem[]>([])

const fetchTodoList = async () => {
  try {
    const response = await fetch(`http://localhost:3001/api/tasks/user/${userId}/review`)
    const data = await response.json()
    todoList.value = data
    console.log(todoList.value)
  } catch (error) {
    console.error('获取待办任务失败', error)
  }
}


const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const goToTaskDetail = (taskId: number) => {
  router.push(`/task/${taskId}`)
}

onMounted(() => {
  fetchTodoList()
})
</script>

<style scoped>
.todo-container {
  max-width: 2000px;
  margin: 20px auto;
  padding: 0 20px;
}

.todo-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.task-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.task-title {
  text-align: center;
  color: #409EFF;
  margin: 0;
}

.images-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  width: 100%;
  overflow-x: auto;
  padding: 10px 0;
}

.row-image {
  width: calc(33.33% - 14px);
  aspect-ratio: 16/9;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.row-image:hover {
  transform: translateY(-5px);
}

.study-button {
  padding: 10px 20px;
  font-weight: bold;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.study-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

@media (max-width: 768px) {
  .todo-container {
    padding: 0 10px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .images-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .row-image {
    width: 100%;
  }
}
</style>