import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from "@ant-design/icons-vue";
import axios from "axios";

//axios配置全局baseURL
axios.defaults.baseURL = process.env.VUE_APP_SERVER;

// 集成第三方组件，一般都在main.ts里增添配置
const  app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

//全局使用图标
const  icons: any = Icons;
for (const i in icons){
    app.component(i, icons[i]);
}

//环境日志打印
console.log('环境',process.env.NODE_ENV);
console.log('服务端',process.env.VUE_APP_SERVER);