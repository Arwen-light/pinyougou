package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        //关键字空格处理

        //安全性校验
        if(searchMap.get("keywords")!=null){

            String keywords = (String) searchMap.get("keywords");
            searchMap.put("keywords", keywords.replace(" ", ""));

        }else{
            return  null;
        }


        // 创建返回的map
        Map<String, Object> map = new HashMap<String, Object>();


        // 1.带高亮的主题的数据返回
        map.putAll(searchList(searchMap));

        // 2.查询solr，获取搜索产品的分类category
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);

        //3.查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        } else {//如果没有分类名称，按照第一个查询
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
            }
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    /**
     * 带高亮的主题的数据返回
     *
     * @param searchMap
     * @return
     */
    private Map<String, Object> searchList(Map searchMap) {

        Map<String, Object> map = new HashMap<String, Object>();


        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);

        //1.1按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5按价格筛选.....
        if (!"".equals(searchMap.get("price"))) {
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")) {//如果区间起点不等于0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")) {//如果区间终点不等于*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        // 1.6 分页查询功能的实现
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }
        // 设置起始页
        query.setOffset((pageNo - 1) * pageSize);
        // 设置每页搜索的页数
        query.setRows(pageSize);


        // 根据条件进行排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if(sortValue!=null && !sortValue.equals(" ")){

            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_" + sortField);
                query.addSort(sort);
            }

            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_" + sortField);
                query.addSort(sort);
            }


        }


        //2 获取高亮结果集
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

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


        map.put("rows", page.getContent()); // 每页显示的条数
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数
        for (TbItem tbItem : page.getContent()) {
            System.out.println(tbItem.getTitle());
        }

        return map;
    }


    /**
     * 查询solr，获取搜索产品的分类category
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {

        // 定义好返回的map 集合
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> list = new ArrayList();

        // 初步筛选  类似where  按照关键字查询
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // spring data solr   分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);


        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        // 通过分组的入口，得到分组的数据，分装到map中，返回

        GroupResult<TbItem> itemCategory = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = itemCategory.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }

        return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        System.out.println("进入方法searchBrandAndSpecList了");
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        System.out.println("结果集问题" + typeId);
        if (typeId != null) {
            System.out.println("Nihao,开始查询了");
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            System.out.println(brandList);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            System.out.println(specList);
            map.put("specList", specList);
        }
        return map;
    }


}
