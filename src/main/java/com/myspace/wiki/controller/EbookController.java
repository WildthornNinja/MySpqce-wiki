package com.myspace.wiki.controller;


import com.myspace.wiki.domain.Ebook;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list() {
        CommonResp<List<Ebook>> response = new CommonResp<>();
        List<Ebook> list = ebookService.list();
        response.setContent(list);
        return response;
    }

}
