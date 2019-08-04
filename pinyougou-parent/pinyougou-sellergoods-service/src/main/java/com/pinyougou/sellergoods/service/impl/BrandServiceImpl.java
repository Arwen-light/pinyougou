package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.BrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;


    // 查询全部
    @Override
    public List<Brand> findBrandAll() {

        List<Brand> brandList = brandMapper.selectByExample(null);
        return brandList;
    }


    /**
     * 分页查询服务层代码
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findByPage(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> brandList = (Page<Brand>) brandMapper.selectByExample(null);
        return new PageResult(brandList.getTotal(),brandList.getResult());
    }

    @Override
    public void save(Brand brand) {
        brandMapper.insert(brand);
    }
    /**
     * 修改查询
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改保存
     * @param brand
     */
    @Override
    public void updateByid(Brand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }


    /**
     * 删除选中（循环删除）
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }
    /**
     * 分页条件查询接口实现
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findByPageAndCondition(Brand brand, int pageNum, int pageSize) {

        // 判断Brand 条件是否为空
        BrandExample example = new BrandExample();

        if( brand !=null){
            BrandExample.Criteria criteria = example.createCriteria();
            // brand.getName 是否为空并且长度大于0
            if(brand.getName()!=null && brand.getName().length()> 0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            // brand.getFirstChar 是否为空并且长度大于0
            if(brand.getFirstChar() !=null &&brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        Page<Brand> brandList = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult(brandList.getTotal(),brandList.getResult());

    }


    /**
     *
     * @return
     */
    @Override
    public List<Map> selectBrandOptionList() {
        List<Map> map = brandMapper.selectBrandOptionList();
        return map;
    }
}
