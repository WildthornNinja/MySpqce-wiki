package com.myspace.wiki.controller;

import com.myspace.wiki.domain.Test;
import com.myspace.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @RestController 返回字符串
 * @RestController =@Controller+@ResponseBody（返回一个字符串火JSON对象）
 * @Controller 返回一个页面
 *
 */
@RestController
public class TestController {

    @Value("${test.hello:TEST}")
    private String testHello;

    @Resource
    private TestService testService;

    /**
     * Http 请求方式 ：GET,POST,PUT,DELETE
     * @RequestMapping注解支持所有的请求方式
     * @GetMapping
     * @PutMapping
     * @PostMapping
     * @DeleteMapping
     * @RequestMapping(value = "/hello",method = RequestMethod.GET)
     * @return
     */
    @GetMapping("/hello")
    public String hello(){
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World! POST  "+name;
    }

    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }



}
