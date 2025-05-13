import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/manager/home'
        },
        {
            path: '/manager',
            // 使用箭头函数返回 Promise
            component: () => import('../views/Manager.vue'),
            children: [
                {
                    path: 'home', meta: {name: '主页'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Home.vue'),
                },
                {
                    path: 'admin', meta: {name: '管理员信息'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Admin.vue'),
                },
                {
                    path: 'user', meta: {name: '普通用户信息'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/User.vue'),
                },
                {
                    path: 'person', meta: {name: '个人信息'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Person.vue'),
                },
                {
                    path: 'updatePassword', meta: {name: '修改密码'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/UpdatePassword.vue'),
                },
                {
                    path: 'notice', meta: {name: '系统公告'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Notice.vue'),
                },
                {
                    path: 'introduction', meta: {name: '旅游攻略'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Introduction.vue'),
                },
                {
                    path: 'category', meta: {name: '旅游攻略'},
                    // 使用箭头函数返回 Promise
                    component: () => import('../views/Category.vue'),
                },

            ]
        },
        {
            path: '/notFound',
            // 使用箭头函数返回 Promise
            component: () => import('../views/404.vue'),
        },
        {
            path: '/front',
            // 使用箭头函数返回 Promise
            component: () => import('../views/Front.vue'),
        },
        {
            path: '/front/introductionDetail',
            // 使用箭头函数返回 Promise
            component: () => import('../views/IntroductionDetail.vue'),
        },
        {
            path: '/login',
            //
            component: () => import('../views/Login.vue'),
        },
        {
            path: '/register',
            //
            component: () => import('../views/Register.vue'),
        },
        {
            path: '/front/personalPage',
            //
            component: () => import('../views/PersonalPage.vue'),
        },
        {
            path: '/front/personalMsg',
            //
            component: () => import('../views/PersonalMsg.vue'),
        },

        {
            path: '/:pathMatch(.*)',
            redirect: '/notFound'
        }
    ]
})

export default router