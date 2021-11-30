package com.myspace.wiki.service;


import com.myspace.wiki.domain.Ebook;
import com.myspace.wiki.domain.EbookExample;
import com.myspace.wiki.mapper.EbookMapper;
import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.response.EbookQueryResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    /**
     * 持久层返回List<Ebook>需要转成List<EbookResp>再返回controller
     * @param ebookQueryReq
     * @return
     */
    public List<EbookQueryResp> list(EbookQueryReq ebookQueryReq) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();//criteria相当于SQL语句中的查询条件
        criteria.andNameLike("%"+ebookQueryReq.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookQueryResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            EbookQueryResp ebookQueryResp = new EbookQueryResp();
            //ebookQueryResp.setId(ebook.getId());
            BeanUtils.copyProperties(ebook,ebookQueryResp);
            //StringBoot自带工具类BeanUtils，BeanUtils.copyProperties(“拷贝数据源”,“拷贝数据目的地”)
            respList.add(ebookQueryResp);
        }
        return respList;
    }
}
