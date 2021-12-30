package com.myspace.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.myspace.wiki.domain.Category;
import com.myspace.wiki.domain.CategoryExample;
import com.myspace.wiki.mapper.CategoryMapper;
import com.myspace.wiki.request.CategoryQueryReq;
import com.myspace.wiki.request.CategorySaveReq;
import com.myspace.wiki.response.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SnowFlake snowFlake;


    /**
     * 持久层返回List<Category>需要转成List<CategoryResp>再返回controller
     * @param categoryQueryReq
     * @return
     */
    public PageResp<CategoryQueryResp> list(CategoryQueryReq categoryQueryReq) {

        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();//criteria相当于SQL语句中的查询条件
        if(!ObjectUtils.isEmpty(categoryQueryReq.getName())){
            criteria.andNameLike("%"+ categoryQueryReq.getName()+"%");
        }
        PageHelper.startPage(categoryQueryReq.getPage(), categoryQueryReq.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数:{}",pageInfo.getTotal());//总行数
        LOG.info("总页数:{}",pageInfo.getPages());//总页数

//        List<CategoryQueryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
//            CategoryQueryResp categoryQueryResp = new CategoryQueryResp();
////            //categoryQueryResp.setId(category.getId());
////            BeanUtils.copyProperties(category,categoryQueryResp);
////            //StringBoot自带工具类BeanUtils，BeanUtils.copyProperties(“拷贝数据源”,“拷贝数据目的地”)
        //对象复制
//            CategoryQueryResp copy = CopyUtil.copy(category, CategoryQueryResp.class);
//            respList.add(copy);
//        }
        //使用CopyUtil工具类封装相同Copy功能，封装大量相似代码，提高代码复用性
        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存分类
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            // 更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 删除分类
     * @param id
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
