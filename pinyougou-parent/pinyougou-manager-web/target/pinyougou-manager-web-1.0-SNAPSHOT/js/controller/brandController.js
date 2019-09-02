app.controller("brandController", function ($scope,$controller, brandService) {

    $controller ("baseController",{$scope:$scope});

    $scope.findAll = function () {

        brandService.findAll().success(
            function (response) {
                $scope.arr = response;
            }
        );
    };

    // http://localhost:9102/brand/findByPage.do?page=2&rows=8
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                $scope.arr = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    // 添加方法
    $scope.save = function () {

        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message)
                }
            }
        )
    };


    // 修改查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    // 修改保存代码 使用之前的add 方法进行公用(已完成)



    // delete 方法ajax 请求删除，并且进行回显刷新
    $scope.del = function () {
        brandService.del($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message)
                }
            }
        )
    };


    // 分页条件查询  $http.post("../brand/search.do?page=" + page + "&rows=" + rows, $scope.searchEntity)
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.arr = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

});