package com.myspace.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myspace.wiki.domain.Content;
import com.myspace.wiki.domain.Doc;
import com.myspace.wiki.domain.DocExample;
import com.myspace.wiki.exception.BusinessException;
import com.myspace.wiki.exception.BusinessExceptionCode;
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
import com.myspace.wiki.websocket.WebSocketServer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Resource
    public WebSocketServer webSocketServer;
    @Resource
    public WsService wsService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // ????????????
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }
    /**
     * ???????????????List<Doc>????????????List<DocResp>?????????controller
     * @param docQueryReq
     * @return
     */
    public PageResp<DocQueryResp> list(DocQueryReq docQueryReq) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();//criteria?????????SQL????????????????????????
        if(!ObjectUtils.isEmpty(docQueryReq.getName())){
            criteria.andNameLike("%"+ docQueryReq.getName()+"%");
        }
        PageHelper.startPage(docQueryReq.getPage(), docQueryReq.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("?????????:{}",pageInfo.getTotal());//?????????
        LOG.info("?????????:{}",pageInfo.getPages());//?????????

//        List<DocQueryResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
//            DocQueryResp docQueryResp = new DocQueryResp();
////            //docQueryResp.setId(doc.getId());
////            BeanUtils.copyProperties(doc,docQueryResp);
////            //StringBoot???????????????BeanUtils???BeanUtils.copyProperties(?????????????????????,???????????????????????????)
        //????????????
//            DocQueryResp copy = CopyUtil.copy(doc, DocQueryResp.class);
//            respList.add(copy);
//        }
        //??????CopyUtil?????????????????????Copy?????????????????????????????????????????????????????????
        //????????????
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * ??????
     */
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // ??????
            doc.setId(snowFlake.nextId());
            //????????????0??????insert()??????????????????????????????default?????????
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // ??????
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);//BOLOB???????????????????????? ?????????
            if (count == 0) {
                contentMapper.insert(content);
                }
        }
    }

    /**
     * ??????
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
     * ????????????
     * @param id
     * @return
     */
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // ???????????????+1
        docMapperCust.increaseViewCount(id);//?????????SQL??????
        if (ObjectUtils.isEmpty(content)) {
            return "";
            } else {
            return content.getContent();
            }
    }
    /**
     * ??????
     */
    public void vote(Long id) {
        docMapperCust.increaseVoteCount(id);
        // docMapperCust.increaseVoteCount(id);
        // ??????IP+doc.id??????key???24?????????????????????
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // ????????????
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("???" + docDb.getName() + "???????????????", logId);
        //rocketMQTemplate.convertAndSend("VOTE_TOPIC", "???" + docDb.getName() + "???????????????");
    }
    /*
    ???????????????????????????????????????????????????
     */
    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
