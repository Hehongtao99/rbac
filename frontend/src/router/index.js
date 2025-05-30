import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Layout from '../views/Layout.vue'
import UserManagement from '../views/UserManagement.vue'
import RoleManagement from '../views/RoleManagement.vue'
import OrganizationManagement from '../views/OrganizationManagement.vue'
import TeacherManagement from '../views/TeacherManagement.vue'
import StudentManagement from '../views/StudentManagement.vue'
import NoticeManagement from '../views/NoticeManagement.vue'
import NoticeView from '../views/NoticeView.vue'
import CourseManagement from '../views/CourseManagement.vue'
import CourseTemplateManagement from '../views/CourseTemplateManagement.vue'
import CourseApplication from '../views/teacher/CourseApplication.vue'
import MyApplications from '../views/teacher/MyApplications.vue'
import CourseApplicationReview from '../views/admin/CourseApplicationReview.vue'
import ScheduleManagement from '../views/teacher/ScheduleManagement.vue'
import AllSchedules from '../views/admin/AllSchedules.vue'
import Profile from '../views/Profile.vue'

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
          path: '/system/organization',
          name: 'OrganizationManagement',
          component: OrganizationManagement,
          meta: {
            title: '组织管理'
          }
        },
        {
          path: '/system/teacher',
          name: 'TeacherManagement',
          component: TeacherManagement,
          meta: {
            title: '教师管理'
          }
        },
        {
          path: '/system/student',
          name: 'StudentManagement',
          component: StudentManagement,
          meta: {
            title: '学生管理'
          }
        },
        {
          path: '/notice/management',
          name: 'NoticeManagement',
          component: NoticeManagement,
          meta: {
            title: '通知管理'
          }
        },
        {
          path: '/notice/view',
          name: 'NoticeView',
          component: NoticeView,
          meta: {
            title: '通知公告'
          }
        },
        {
          path: '/course/management',
          name: 'CourseManagement',
          component: CourseManagement,
          meta: {
            title: '开课实例管理'
          }
        },
        {
          path: '/course/template',
          name: 'CourseTemplateManagement',
          component: CourseTemplateManagement,
          meta: {
            title: '课程模板管理'
          }
        },
        {
          path: '/course/application',
          name: 'CourseApplication',
          component: CourseApplication,
          meta: {
            title: '申请开课'
          }
        },
        {
          path: '/course/my-applications',
          name: 'MyApplications',
          component: MyApplications,
          meta: {
            title: '我的申请'
          }
        },
        {
          path: '/course/application-review',
          name: 'CourseApplicationReview',
          component: CourseApplicationReview,
          meta: {
            title: '开课申请审批'
          }
        },
        {
          path: '/course/schedule',
          name: 'ScheduleManagement',
          component: ScheduleManagement,
          meta: {
            title: '课程表管理'
          }
        },
        {
          path: '/course/all-schedules',
          name: 'AllSchedules',
          component: AllSchedules,
          meta: {
            title: '所有课程表'
          }
        },
      {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: {
          title: '个人中心'
        }
      }
    ]
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