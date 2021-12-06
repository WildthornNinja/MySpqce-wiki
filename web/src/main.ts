import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

// 集成第三方组件，一般都在main.ts里增添配置
createApp(App).use(store).use(router).use(Antd).mount('#app');
