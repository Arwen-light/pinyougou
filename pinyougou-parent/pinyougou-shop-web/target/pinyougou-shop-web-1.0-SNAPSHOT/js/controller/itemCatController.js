//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            // 给新添加的一个对象赋值ParentId
            $scope.entity.parentId = $scope.parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.findItemCatByParentId($scope.parentId);
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {

        if (confirm("确认删除吗")) {
            //获取选中的复选框
            itemCatService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.findItemCatByParentId($scope.parentId);
                        $scope.selectIds = [];
                    } else {
                        alert(response.message)
                    }
                }
            );
        }
    };

    $scope.searchEntity = {};//定义搜索对象


    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };


    // 定义一个变量，记录刚刚访问的那个父节点，备用新建添加功能
    $scope.parentId = null;


    //findItemCatByParentId
    $scope.findItemCatByParentId = function (id) {

        // 记录刚刚访问的parentId
        $scope.parentId = id;

        itemCatService.findItemCatByParentId(id).success(
            function (response) {
                $scope.list = response;
            }
        );
    };


    // 处理面包屑导航问题  bread crumbs
    // 定位的面包屑 --
    $scope.grade = 1;
    // 给一个函数 触发面包屑的记录位置

    $scope.changeGrade = function (value) {
        $scope.grade = value;
    };

    $scope.excuateMethodAndBreadCrumbs = function (ParentEntity) {

        if ($scope.grade == 1) {
            $scope.breadCrumbs_1 = null;
            $scope.breadCrumbs_2 = null;

        }


        if ($scope.grade == 2) {
            $scope.breadCrumbs_1 = ParentEntity;
            $scope.breadCrumbs_2 = null;
        }

        if ($scope.grade == 3) {
            $scope.breadCrumbs_2 = ParentEntity;
        }

        $scope.findItemCatByParentId(ParentEntity.id);

    }


    $scope.typeIdList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};//品牌列表


    $scope.selectTemplateOptionList = function ( ) {

        typeTemplateService.selectTemplateOptionList( ).success(
            function (response) {
                $scope.typeIdList = {data:response};
            }
        );
    };

});	
