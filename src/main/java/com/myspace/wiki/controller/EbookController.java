package com.myspace.wiki.controller;


import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.request.EbookSaveReq;
import com.myspace.wiki.response.CommonResp;
import com.myspace.wiki.response.EbookQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.service.EbookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/ebook")
public class EbookController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq ebookQueryReq) {
        CommonResp<PageResp<EbookQueryResp>> response = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(ebookQueryReq);
        response.setContent(list);
        return response;
    }
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq ebookSaveReq) {
        CommonResp response = new CommonResp<>();
        ebookService.save(ebookSaveReq);
        return response;
    }
    @RequestMapping("/upload/avatar")
    public CommonResp upload(@RequestParam MultipartFile avatar) throws IOException {
        LOG.info("上传文件开始：{}", avatar);
        LOG.info("文件名：{}", avatar.getOriginalFilename());
        LOG.info("文件大小：{}", avatar.getSize());

        // 保存文件到本地
        String fileName = avatar.getOriginalFilename();
        String fullPath = "D:/JavaWorkSpace/wiki/web/public/image/" + fileName;

        File dest = new File(fullPath);
        avatar.transferTo(dest);
        LOG.info(dest.getAbsolutePath());

        return new CommonResp();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }

}
