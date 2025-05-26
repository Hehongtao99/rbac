import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Layout from '../views/Layout.vue'
import UserManagement from '../views/UserManagement.vue'
import RoleManagement from '../views/RoleManagement.vue'
import OrganizationManagement from '../views/OrganizationManagement.vue'
import TeacherManagement from '../views/TeacherManagement.vue'
import StudentManagement from '../views/StudentManagement.vue'
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