package com.myspace.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myspace.wiki.domain.Content;
import com.myspace.wiki.domain.Doc;
import com.myspace.wiki.domain.DocExample;
import com.myspace.wiki.mapper.ContentMapper;
import com.myspace.wiki.mapper.DocMapper;
import com.myspace.wiki.mapper.DocMapperCust;
import com.myspace.wiki.request.DocQueryReq;
import com.myspace.wiki.request.DocSaveReq;
import com.myspace.wiki.response.DocQueryResp;
import com.myspace.wiki.response.PageResp;
import com.myspace.wiki.util.CopyUtil;
import com.myspace.wiki.util.RedisUtil;
import com.myspace.wiki.util.RequestContext;
import com.myspace.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private DocMapperCust docMapperCust;
    @Resource
    public RedisUtil redisUtil;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }
    /**
     * 持久层返回List<Doc>需要转成List<DocResp>再返回controller
     * @param docQueryReq
     * @return
     */
    public PageResp<DocQueryResp> list(DocQueryReq docQueryReq) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();//criteria相当于SQL语句中的查询条件
        if(!ObjectUtils.isEmpty(docQueryReq.getName())){
            criteria.andNameLike("%"+ docQueryReq.getName()+"%");
        }
        PageHelper.startPage(docQueryReq.getPage(), docQueryReq.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数:{}",pageInfo.getTotal());//总行数
        LOG.info("总页数:{}",pageInfo.getPages());//总页数

//        List<DocQueryResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
//            DocQueryResp docQueryResp = new DocQueryResp();
////            //docQueryResp.setId(doc.getId());
////            BeanUtils.copyProperties(doc,docQueryResp);
////            //StringBoot自带工具类BeanUtils，BeanUtils.copyProperties(“拷贝数据源”,“拷贝数据目的地”)
        //对象复制
//            DocQueryResp copy = CopyUtil.copy(doc, DocQueryResp.class);
//            respList.add(copy);
//        }
        //使用CopyUtil工具类封装相同Copy功能，封装大量相似代码，提高代码复用性
        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
                }
        }
    }

    /**
     * 删除
     * @param id
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 查找内容
     * @param id
     * @return
     */
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
            } else {
            return content.getContent();
            }
    }
    /**
     * 点赞
     */
    public void vote(Long id) {
        docMapperCust.increaseVoteCount(id);
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
//        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
//            docMapperCust.increaseVoteCount(id);
//        } else {
//            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
//        }
    }
}
