//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService, itemCatService ,$location, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承uploadService

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function () {

       var id  =   $location.search()['id'];
       alert(id);
       // 健壮性的判断从处理
       if(id == null){
           return;
       }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);

                // 显示图片的列表

               $scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);

                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=  JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                // 规格信息也需要进行Json 格式的转化
                $scope.entity.goodsDesc.specificationItems  = JSON.parse($scope.entity.goodsDesc.specificationItems);

                //SKU列表规格列转换
                var lenList = $scope.entity.itemList.length;
                for (var i = 0; i < lenList; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec)
                }


            }
        );
    };


    // 创建一个方法用来判断attribuateName and  attribuateValue 在entity.goodsDesc.specificationItems 中是否存在用来显示

    $scope.checkAttributeValue = function(specName,optionName){

        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items,'attributeName',specName);
        if(object == null){
            return false;
        }else{
            if( object.attributeValue.indexOf(optionName) >=0){
                return  true;
            }else{
                return  false;
            }
             //attributeValue
        }

    };

    // 保存融合和有关的添加和修改的内容，并且进行了update 更新判断id  是否存在的功能的列表
    $scope.save = function () {

        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction = editor.html();

        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID  $scope.entity.goods.id!=null
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功");  //location.href="goods.html";//跳转到商品列表页
                    $scope.entity = {};//重新加载
                    editor.html('');//清空富文本编辑器
                    location.href="goods.html";//跳转到商品列表页
                } else {
                    alert(response.message);
                }
            }
        );
    };



    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };


    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };


    /**
     * 上传图片
     */
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传发生错误");
        });
    };


    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }


    //读取一级分类
    $scope.selectItemsCat1List = function () {

        itemCatService.findItemCatByParentId(0).success(
            function (response) {
                $scope.itemsCat1List = response;
            }
        );
    }


    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findItemCatByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        );
    });

    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findItemCatByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        );
    });


    //三级分类选择后  读取模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
            }
        );
    });


    // 读取模板中的findOne数据，typeTemplateService
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {

        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表  customAttributeItems

                if($location.search()['id']==null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems)
                }
            }
        );

        //查询规格列表
        typeTemplateService.findAboutSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });


    // 绑定选择的参数准备上传  [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]

    $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };

    $scope.updateSpecAttribute=function($event,name,value){

        var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);

        if(object!=null){

            if($event.target.checked){
                object.attributeValue.push(value);
            }else{

                object.attributeValue.splice( object.attributeValue.indexOf(value),1);

                if(object.attributeValue.length==0){$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
                }

            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});
        }
    }



    // 根据spec 规格选项动态的生成sku 库存两单位列表

    //创建SKU列表
    $scope.createItemList=function(){
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
        var items=  $scope.entity.goodsDesc.specificationItems;
        for(var i=0;i< items.length;i++){
            $scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }
    //添加列值
    addColumn=function(list,columnName,conlumnValues){
        var newList=[];//新的集合
        for(var i=0;i<list.length;i++){
            var oldRow= list[i];
            for(var j=0;j<conlumnValues.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
                newRow.spec[columnName]=conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    $scope.status=['未审核','已审核','审核未通过','关闭'];  //商品状态


    // 在页面初始化后向后端itemcat 类型的数据的请求


    $scope.myItemCatList = [ ];//商品分类列表

    $scope.changeDateToStringAllItemCat = function () {
        itemCatService.findAll().success(
            function (reponse) {
                // 将结果集添加到数组中
                for (var i = 0; i < reponse.length; i++) {
                    $scope.myItemCatList[reponse[i].id] = reponse[i].name
                }
            }
        )
    };

    $scope.directURL = function (id) {

        location.href="goods_edit.html#?id="+id;
    }

});
