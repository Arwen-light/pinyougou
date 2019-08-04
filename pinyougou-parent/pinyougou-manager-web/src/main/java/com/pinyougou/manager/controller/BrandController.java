package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.container.page.Page;
import com.pinyougou.pojo.Brand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll.do")
    public List<Brand> findBrandAll() {
        List<Brand> brandList = brandService.findBrandAll();
        return brandList;
    }


    @RequestMapping("/findByPage.do")
    public PageResult findBrandPage(int page, int rows) {
        System.out.println(page + rows);
        PageResult pageList = brandService.findByPage(page, rows);
        return pageList;
    }

    @RequestMapping("/add.do")
    public Result save(@RequestBody Brand brand) {

        try {
            brandService.save(brand);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    // 修改查询 findOne
    @RequestMapping("/findOne.do")
    public Brand findOne(Long id) {
        Brand brand = brandService.findOne(id);
        return brand;
    }

    // 修改保存
    @RequestMapping("/update.do")
    public Result updateById(@RequestBody Brand brand) {
        try {
            brandService.updateByid(brand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    // 删除选中
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    // 条件查询分页查询合并体
    @RequestMapping("/search.do")
    public PageResult findBrandPageAndCondition(@RequestBody Brand brand, int page, int rows) {
        System.out.println(page + rows);
        PageResult pageList = brandService.findByPageAndCondition(brand, page, rows);
        return pageList;
    }


    // 查询品牌为支撑模板数据
    @RequestMapping("/selectBrandOptionList.do")
    public List<Map> selectBrandOptionList() {
        List<Map> maps = brandService.selectBrandOptionList();
        return maps;
    }


}
