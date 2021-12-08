package com.myspace.wiki.controller;


import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.request.EbookSaveReq;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.response.EbookQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq ebookQueryReq) {
        CommonResp<PageResp<EbookQueryResp>> response = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(ebookQueryReq);
        response.setContent(list);
        return response;
    }
    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq ebookSaveReq) {
        CommonResp response = new CommonResp<>();
        ebookService.save(ebookSaveReq);
        return response;
    }


}
