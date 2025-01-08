<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router'; // 新增 useRouter
import { useStore } from 'vuex';

const route = useRoute();
const store = useStore();
const taskId = route.params.task_id as string; // 获取任务ID
const userId = Number(store.state.user.id); // 获取用户ID
const token = store.state.user.token; // 获取token
const task = ref<any>(null); // 存储任务对象
// const isTaskAreaFocused = ref(false);
// const isAIResponseFocused = ref(false);
interface Question {
  hints: string[];
  question: string;
}

interface aiFeedback {
  questions: Question[];}

interface StudentResponse {
  id: number;
  responseText: string;
  submissionDate: string;
  aiFeedback: aiFeedback;
}
const conversation = ref<StudentResponse[]>([]);

// const aiResponse = ref<{ text: string, type: string, content: string[] }[]>([]);

// const aiImages = ref<{ [key: string]: string[] }>({});
// const selectedResponse = ref<string | null>(null);

//依据路由传过来的taskId来找到对应的任务
const fetchTask = async (id: string) => {
  try {
    const response = await axios.get(`http://localhost:3001/api/tasks/${id}`, {
      headers: {              // 在请求头中添加 Authorization
        Authorization: `Bearer ${token}`,
      },
    });
    task.value = response.data.task; // 假设后端返回任务对象
  } catch (error) {
    console.error('获取任务失败:', error);
  }
};



const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

// // AI回复点击逻辑
// function handleAIResponseClick(response: { text: string; type: string; content: string[] }) {
//   if (selectedResponse.value === response.text) {
//     selectedResponse.value = null;
//     if (response.type === 'image') {
//       aiImages.value[response.text] = [];
//     }
//   } else {
//     selectedResponse.value = response.text;
//     if (response.type === 'image') {
//       aiImages.value[response.text] = response.content;
//     }
//   }
//   isAIResponseFocused.value = true;
//   isTaskAreaFocused.value = false;
// }

const expandedResponse = ref<number | null>(null)

const toggleHints = (responseId: number) => {
  expandedResponse.value = expandedResponse.value === responseId ? null : responseId
}

const fetchHistory = async (taskId: string, userId: number) => {
  try {
    const response = await axios.get('http://localhost:3001/api/ai/conversation', {
      params: { taskId, userId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    conversation.value = response.data.StudentResponses; // 假设后端返回对话数组
    console.log(conversation.value);
    // 在数据加载完成后执行滚动
    await nextTick(() => {
      scrollToBottom();
    });
  } catch (error) {
    console.error('获取AI回复失败:', error);
  }
}

const router = useRouter(); // 新增

function goToTaskDetails() {
  router.push(`/task/${taskId}`); // 跳转至 TaskDetails
}


const scrollContainer = ref(null);
const scrollToBottom = () => {
      if(scrollContainer.value){
        scrollContainer.value.scrollTop = scrollContainer.value.scrollHeight;
      }
    };

onMounted(() => {
  fetchTask(taskId);
  fetchHistory(taskId, userId);
  // 移除这里的 scrollToBottom() 调用
});
</script>

<template>
  <div class="container">
    <!-- 新增“进入学习”按钮 -->
    <button class="learnB" @click="goToTaskDetails">进入学习</button>

    <div class="task-area">
      <div class="task-images-container">
        <div v-if='task' class="task-images">
          <img 
            v-for="image in task.images" 
            :key="image.imageOrder" 
            :src="image.imageUrl" 
            alt="任务图片"
            class="task-image"
          />
        </div>
      </div>
    </div>

    <!-- Conversation Section -->
    <div class="conversation-wrapper" ref="scrollContainer">
      <!-- 将 ref 移到这个元素上 -->
      <div class="conversation-container">
        <div v-for="response in conversation" :key="response.id" class="conversation-thread">
          <!-- Student Message -->
          <div class="message student-message">
            <div class="message-content">
              <div class="message-text">{{ response.responseText }}</div>
              <div class="timestamp">{{ formatDate(response.submissionDate) }}</div>
            </div>
            <img class="avatar" :src="$store.state.user.avatar" alt="Student Avatar" />
          </div>

          <!-- AI Feedback -->
          <div v-for="q in response.aiFeedback.questions" :key="q.question" class="message ai-message">
            <img class="avatar" src="../../assets/image/robot.png" alt="AI Avatar" />
            <div class="message-content">
              <div class="message-text">{{ q.question }}</div>
              <div class="hints-container" v-if="q.hints">
                <div class="hints-toggle" @click="toggleHints(response.id)">
                  展示提示
                </div>
                <div v-if="expandedResponse === response.id" class="hints-list">
                  <div v-for="(hint, index) in q.hints" :key="index" class="hint">
                    <img :src="'data:image/png;base64,' + hint" :alt="'Hint Icon ' + index" class="hint-image" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.container {
  height: 85vh;
  display: flex;
  flex-direction: column;
  background-color: rgb(151, 218, 247);
  overflow: auto;
}

.learnB{
  margin: 10px;
  padding: 10px;
  background-color: rgb(107, 182, 236);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.task-area {
  min-height: 200px;
  max-height: 30vh;
  width: 100%;
  background-color: rgb(151, 218, 247);
  padding: 20px 0;
  box-shadow: 0 2px 4px rgb(151, 218, 247);
}

.task-images-container {
  max-width: 1000px;
  margin: 0 auto;
  height: 100%;
}

.task-images {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  height: 100%;
  padding: 0 20px;
}

.task-image {
  max-height: 100%;
  max-width: 30%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.conversation-wrapper {
  flex: 1;
  max-height: 70vh;
  overflow-y: auto;
  background-color: rgb(151, 218, 247);
  padding: 20px 0;
  
}

.conversation-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  
}

.conversation-thread {
  margin-bottom: 24px;
}

.message {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  margin-bottom: 16px;
  max-width: 90vw;
  gap: 12px;
  
}

.student-message {
  display: flex;
  flex-direction: row;
  align-self: flex-start;
  margin-left: 20vw;
  
}

.message-content {
  flex: 1;
  max-width: 70%;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.student-message .message-content {
  background-color: rgb(107, 182, 236);
}
.ai-message{
  flex-direction: row;
  align-self: flex-end;
  max-width: 50vw;
}
.ai-message .message-content {
  background-color: rgb(107, 182, 236);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-text {
  font-size: 16px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.timestamp {
  font-size: 12px;
  color: #666;
  text-align: right;
}

.hints-container {
  margin-top: 8px;
  border-top: 1px solid #eee;
  padding-top: 8px;
}

.hints-toggle {
  cursor: pointer;
  color: #2196f3;
  font-size: 14px;
  padding: 4px 0;
}

.hints-toggle:hover {
  text-decoration: underline;
}

.hints-list {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hint {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.hint-image {
  max-width: 100%;
  height: auto;
  display: block;
}

/* Responsive Design */
@media (max-width: 768px) {
  .task-area {
    min-height: 150px;
  }

  .task-images {
    gap: 10px;
    padding: 0 10px;
  }

  .conversation-container {
    padding: 0 12px;
  }

  .message-content {
    max-width: 85%;
  }

  .avatar {
    width: 32px;
    height: 32px;
  }

  .message-text {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .task-area {
    min-height: 120px;
  }

  .task-images {
    gap: 5px;
    padding: 0 5px;
  }

  .conversation-container {
    padding: 0 8px;
  }

  .message-content {
    max-width: 90%;
  }

  .avatar {
    width: 28px;
    height: 28px;
  }
}
</style>