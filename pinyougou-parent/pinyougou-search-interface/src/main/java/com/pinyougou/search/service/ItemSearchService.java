package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {


    /**
     * 根据前端传递的数据进行查询结果并且返回Map集合
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);
}
