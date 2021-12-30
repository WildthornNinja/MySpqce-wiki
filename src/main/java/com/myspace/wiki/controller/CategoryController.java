package com.myspace.wiki.controller;


import com.myspace.wiki.request.CategoryQueryReq;
import com.myspace.wiki.request.CategorySaveReq;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.response.CategoryQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq categoryQueryReq) {
        CommonResp<PageResp<CategoryQueryResp>> response = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(categoryQueryReq);
        response.setContent(list);
        return response;
    }
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq categorySaveReq) {
        CommonResp response = new CommonResp<>();
        categoryService.save(categorySaveReq);
        return response;
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }

}
