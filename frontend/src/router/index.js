import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Layout from '../views/Layout.vue'
import UserManagement from '../views/UserManagement.vue'
import RoleManagement from '../views/RoleManagement.vue'
import Profile from '../views/Profile.vue'
import QuestionBankManage from '../views/QuestionBankManage.vue'
import QuestionManage from '../views/QuestionManage.vue'
import AiQuestionGenerate from '../views/AiQuestionGenerate.vue'
import ExamStart from '../views/ExamStart.vue'
import ExamPage from '../views/ExamPage.vue'
import ExamResult from '../views/ExamResult.vue'
import ExamList from '../views/ExamList.vue'
import WrongQuestionBook from '../views/WrongQuestionBook.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: Layout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: {
          title: '仪表板'
        }
      },
      {
        path: '/system/user',
        name: 'UserManagement',
        component: UserManagement,
        meta: {
          title: '用户管理'
        }
      },
      {
        path: '/system/role',
        name: 'RoleManagement',
        component: RoleManagement,
        meta: {
          title: '角色管理'
        }
      },
      {
        path: '/question-bank-manage',
        name: 'QuestionBankManage',
        component: QuestionBankManage,
        meta: {
          title: '题库管理'
        }
      },
      {
        path: '/question-manage',
        name: 'QuestionManage',
        component: QuestionManage,
        meta: {
          title: '题目管理'
        }
      },
      {
        path: '/ai-question-generate',
        name: 'AiQuestionGenerate',
        component: AiQuestionGenerate,
        meta: {
          title: 'AI智能题目提取'
        }
      },
      {
        path: '/exam-start',
        name: 'ExamStart',
        component: ExamStart,
        meta: {
          title: '开始考试'
        }
      },
      {
        path: '/exam-list',
        name: 'ExamList',
        component: ExamList,
        meta: {
          title: '考试记录'
        }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: {
          title: '个人中心'
        }
      },
      {
        path: '/wrong-question-book',
        name: 'WrongQuestionBook',
        component: WrongQuestionBook,
        meta: {
          title: '错题本'
        }
      }
    ]
  },
  // 考试页面和结果页面不在Layout中，独立显示
  {
    path: '/exam/:id',
    name: 'ExamPage',
    component: ExamPage,
    meta: {
      requiresAuth: true,
      title: '考试中'
    },
    beforeEnter: (to, from, next) => {
      const examId = to.params.id
      if (!examId || examId === 'record' || isNaN(Number(examId))) {
        console.error('无效的考试ID:', examId)
        next('/exam-list')
      } else {
        next()
      }
    }
  },
  {
    path: '/exam-result/:id',
    name: 'ExamResult',
    component: ExamResult,
    meta: {
      requiresAuth: true,
      title: '考试结果'
    },
    beforeEnter: (to, from, next) => {
      const examId = to.params.id
      if (!examId || examId === 'record' || isNaN(Number(examId))) {
        console.error('无效的考试ID:', examId)
        next('/exam-list')
      } else {
        next()
      }
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth) {
    if (token) {
      next()
    } else {
      next('/login')
    }
  } else {
    if (token && to.path === '/login') {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router 