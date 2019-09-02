package com.pinyougou.sellergoods.service.impl;

import java.util.List;


import com.pinyougou.userException.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service(timeout = 50000)
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbItemCat itemCat) {
        itemCatMapper.updateByPrimaryKey(itemCat);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbItemCat findOne(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) throws MyException {

            for (Long id : ids) {

                List<TbItemCat> itemcats = findItemCatByParentId(id);

                if (itemcats == null || itemcats.size() <= 0) {
                    itemCatMapper.deleteByPrimaryKey(id);
                } else {
                    // 造一个异常，告诉前端不可循环删除，安全性不高
                    throw new MyException("删除失败,只授权删除子叶节点！！！");
                }

            }

    }


    @Override
    public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbItemCatExample example = new TbItemCatExample();
        Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }

        }

        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 这个是自己添加的方法，并且要进行缓存到redis 中
     * @param id
     * @return
     */


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbItemCat> findItemCatByParentId(Long id) {


        TbItemCatExample example = new TbItemCatExample();
        Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);



        //每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
        List<TbItemCat> list = findAll();
        for(TbItemCat itemCat:list){
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            System.out.println("更新缓存:商品分类表一个个的在存入");
        }
     //   System.out.println("更新缓存:商品分类表");
        return tbItemCats;
    }

}
