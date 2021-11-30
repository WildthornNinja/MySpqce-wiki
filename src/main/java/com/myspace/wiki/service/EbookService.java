package com.myspace.wiki.service;


import com.myspace.wiki.domain.Ebook;
import com.myspace.wiki.domain.EbookExample;
import com.myspace.wiki.mapper.EbookMapper;
import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.response.EbookQueryResp;
import com.myspace.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

//        List<EbookQueryResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
//            EbookQueryResp ebookQueryResp = new EbookQueryResp();
////            //ebookQueryResp.setId(ebook.getId());
////            BeanUtils.copyProperties(ebook,ebookQueryResp);
////            //StringBoot自带工具类BeanUtils，BeanUtils.copyProperties(“拷贝数据源”,“拷贝数据目的地”)
        //对象复制
//            EbookQueryResp copy = CopyUtil.copy(ebook, EbookQueryResp.class);
//            respList.add(copy);
//        }
        //使用CopyUtil工具类封装相同Copy功能，封装大量相似代码，提高代码复用性
        //列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        return list;
    }
}
