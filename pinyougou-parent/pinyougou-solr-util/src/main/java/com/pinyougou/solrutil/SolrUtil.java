package com.pinyougou.solrutil;


import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    public void  importItemDataTest(){

        List<TbItem> items = getItemsFromDatebase();

        System.out.println("===商品列表===");
        for (TbItem item : items) {
            System.out.println(item.getTitle());
        }
        System.out.println("===结束===");

    }


    public List<TbItem>  importItemDataReal(){

        // 按照条件查询所有
        List<TbItem> items = getItemsFromDatebase();

        System.out.println("===商品列表===");
        for (TbItem item : items) {
            System.out.println(item.getTitle());
        }
        System.out.println("===结束===");

        return items;

    }


    // 抽取查询数据库得到item数据信息的内容
    public List<TbItem> getItemsFromDatebase(){
        // 按照条件查询所有
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        return itemMapper.selectByExample(example);
    }


}



