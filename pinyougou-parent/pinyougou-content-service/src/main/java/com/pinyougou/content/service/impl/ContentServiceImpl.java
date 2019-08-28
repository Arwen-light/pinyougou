package com.pinyougou.content.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        //清除新增广告分类中的数据
        Long categoryId = content.getCategoryId();
        redisTemplate.boundHashOps("content").delete(categoryId);

        contentMapper.insert(content);

    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {

        //查询修改前的分类Id
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        redisTemplate.boundHashOps("content").delete(categoryId);
        contentMapper.updateByPrimaryKey(content);
        //如果分类ID发生了修改,清除修改后的分类ID的缓存
        if (categoryId.longValue() != content.getCategoryId().longValue()) {
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {

            // 清除删除的广告id 所属于的分类广告id ---》
            // 需要注意的是必须先去查询在删除否则你是看不到广告的分类Id ，顺序很重要
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            Long categoryId = tbContent.getCategoryId();
            redisTemplate.boundHashOps("content").delete(categoryId);

            contentMapper.deleteByPrimaryKey(id);


        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {

        List<TbContent> content = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);

        if (content == null) {

            //根据广告分类ID查询广告列表
            TbContentExample contentExample = new TbContentExample();
            Criteria criteria2 = contentExample.createCriteria();
            criteria2.andCategoryIdEqualTo(categoryId);
            criteria2.andStatusEqualTo("1");//开启状态
            contentExample.setOrderByClause("sort_order");//排序
            content = contentMapper.selectByExample(contentExample);

            // 将其存储到spring-data-redis 中，进行缓存，减轻服务器的压力
            redisTemplate.boundHashOps("content").put(categoryId, content);
            System.out.println("第一次从redis中读取数据");

        } else {
            System.out.println("redis 数据库中已经有了相关的数据");
        }
        return content;
    }

}
