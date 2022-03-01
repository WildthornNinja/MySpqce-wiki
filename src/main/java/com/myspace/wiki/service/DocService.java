package com.myspace.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myspace.wiki.domain.Doc;
import com.myspace.wiki.domain.DocExample;
import com.myspace.wiki.mapper.DocMapper;
import com.myspace.wiki.request.DocQueryReq;
import com.myspace.wiki.request.DocSaveReq;
import com.myspace.wiki.response.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;
    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
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
     * 保存分类
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            // 更新
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /**
     * 删除分类
     * @param id
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}
