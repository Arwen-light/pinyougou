package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/spring/applicationContext*.xml")
public class ItemToSolrText {

    @Autowired
    private SolrUtil solrUtil;

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testDeleteAll() {
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    // 测试从数据库中读取有关的Item 数据的列表，并且后台打印一下
    @Test
    public void getItemsFromSql() {
        solrUtil.importItemDataTest();
    }

    @Test
    public void getItemsFromSqlImportIntoSolr() {
        List<TbItem> items = solrUtil.importItemDataReal();
        solrTemplate.saveBeans(items);
        solrTemplate.commit();

        System.out.println("已经成功将基本的数据上传到Solr中了");

    }

    @Test
    public void getItemsFromSqlImportIntoSolrAndDynamic() {

        List<TbItem> items = solrUtil.importItemDataReal();

        for (TbItem item : items) {
            Map specMap = JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map
            item.setSpecMap(specMap);//给带注解的字段赋值
            System.out.println(item.getTitle());
        }

        solrTemplate.saveBeans(items);
        solrTemplate.commit();
        System.out.println("已经成功将基本的数据和动态的数据和更新的时间数据上传到Solr中了");

    }


}
