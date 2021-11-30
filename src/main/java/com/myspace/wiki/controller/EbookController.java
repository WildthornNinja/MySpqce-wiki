package com.myspace.wiki.controller;


import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.response.EbookQueryResp;
import com.myspace.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq ebookQueryReq) {
        CommonResp<List<EbookQueryResp>> response = new CommonResp<>();
        List<EbookQueryResp> list = ebookService.list(ebookQueryReq);
        response.setContent(list);
        return response;
    }

}
