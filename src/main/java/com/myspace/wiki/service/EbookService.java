package com.myspace.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myspace.wiki.domain.Ebook;
import com.myspace.wiki.domain.EbookExample;
import com.myspace.wiki.mapper.EbookMapper;
import com.myspace.wiki.request.EbookQueryReq;
import com.myspace.wiki.request.EbookSaveReq;
import com.myspace.wiki.response.EbookQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.util.CopyUtil;
import com.myspace.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;
    @Resource
    private SnowFlake snowFlake;


    /**
     * 持久层返回List<Ebook>需要转成List<EbookResp>再返回controller
     * @param ebookQueryReq
     * @return
     */
    public PageResp<EbookQueryResp> list(EbookQueryReq ebookQueryReq) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();//criteria相当于SQL语句中的查询条件
        if(!ObjectUtils.isEmpty(ebookQueryReq.getName())){
            criteria.andNameLike("%"+ ebookQueryReq.getName()+"%");
        }
        PageHelper.startPage(ebookQueryReq.getPage(), ebookQueryReq.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数:{}",pageInfo.getTotal());//总行数
        LOG.info("总页数:{}",pageInfo.getPages());//总页数

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

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存电子书
     */
    public void save(EbookSaveReq ebookSaveReq){
        Ebook ebook = CopyUtil.copy(ebookSaveReq,Ebook.class);
        if(ObjectUtils.isEmpty(ebookSaveReq.getId())){
            //新增
            ebook.setId(snowFlake.nextId());
            ebook.setDocCount(0);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);
        }else{
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    /**
     * 删除电子书
     * @param id
     */
    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }

}
