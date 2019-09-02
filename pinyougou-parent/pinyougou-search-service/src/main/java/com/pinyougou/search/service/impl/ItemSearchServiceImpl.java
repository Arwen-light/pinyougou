package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        // 创建返回的map
        Map<String, Object> map = new HashMap<String, Object>();


        // 带高亮的主题的数据返回
        map.putAll(searchList(searchMap));

        return map;
    }


    private Map<String, Object> searchList(Map searchMap) {

        Map<String, Object> map = new HashMap<String, Object>();


        HighlightQuery query = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);

        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮的数组集合
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {

            if (tbItemHighlightEntry.getHighlights().size() > 0) {

                List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
                HighlightEntry.Highlight highlight = highlights.get(0);
                String title = highlight.getSnipplets().get(0);

                TbItem item = tbItemHighlightEntry.getEntity();
                item.setTitle(title);
            }
        }


        map.put("rows", page.getContent());

        for (TbItem tbItem : page.getContent()) {
            System.out.println(tbItem.getTitle());
        }

        return map;
    }
}
