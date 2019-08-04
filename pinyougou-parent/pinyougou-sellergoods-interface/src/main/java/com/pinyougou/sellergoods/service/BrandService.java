package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.Brand;
import entity.PageResult;

import java.util.List;
import java.util.Map;


public interface BrandService {
    /**
     * 这是我们的名牌接口
     * @return
     */

    List<Brand> findBrandAll ();


    /**
     *
     * Brand 品牌 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findByPage(int pageNum,int pageSize);

    /**
     *
     * Brand 保存操作
     * @param brand
     */
    void save(Brand brand);

    /**
     *
     * 修改查询接口
     * @param id
     * @return
     */
     Brand findOne(Long id);

    /**
     * 修改保存接口
     * @param brand
     */
    void updateByid(Brand brand);


    /**
     * 删除选中 （数组）
     * @param ids
     */
    void delete(Long[] ids);


    /**
     *  根据条件进行分页查询接口
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findByPageAndCondition(Brand brand,int pageNum,int pageSize);

    /**
     * 查询List(Map ) 集合 返回给前端展示，选择
     * @return
     */
    List<Map> selectBrandOptionList();
}
