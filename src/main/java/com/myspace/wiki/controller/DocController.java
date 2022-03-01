package com.myspace.wiki.controller;


import com.myspace.wiki.request.DocQueryReq;
import com.myspace.wiki.request.DocSaveReq;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.response.DocQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq docQueryReq) {
        CommonResp<PageResp<DocQueryResp>> response = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(docQueryReq);
        response.setContent(list);
        return response;
    }
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq docSaveReq) {
        CommonResp response = new CommonResp<>();
        docService.save(docSaveReq);
        return response;
    }
    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp resp = new CommonResp<>();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

}
