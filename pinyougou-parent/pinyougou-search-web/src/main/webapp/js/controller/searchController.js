app.controller("searchController", function ($scope,  $location, searchService) {
    $scope.resultMap = {"totalPages":5};
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 40,
        'sortField': '',
        'sort': ''
    };//搜索对象

    // 搜索
    $scope.search = function () {
        // 修改search 由于使用过了ng-model 绑定了pageNO 参数，需要字符串转化成数字，才可以进行处理，否则后端会报错
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;  //搜索返回的结果
                buildPageLabel();//调用
            }
        )
    };

    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类或者是品牌
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    };

    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == 'price') {//如果是分类或品牌
            $scope.searchMap[key] = "";
        } else {//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//执行搜索
    };


    //构建分页标签(totalPages为总页数)
    buildPageLabel = function () {
        $scope.pageLabel = [];
        var maxPageNo = $scope.resultMap.totalPages;
        var firstPage = 1;
        var lastPage = maxPageNo;

        // 显示点的逻辑判断操作
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点

        if ($scope.resultMap.totalPages > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                $scope.firstDot = false;  //前面无点
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                firstPage = maxPageNo - 4;
                $scope.lastDot = false;  //后边无点
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }

        // 构建一个集合--》 为了使用ng-reapt 方法
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };


    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };


    //判断当前页为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    };

    //判断当前页是否未最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    };

    // SearchMap 中的 sort and sortField 进行赋值
    $scope.sortSearch = function (sortField,sort ) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    };


    // 判断关键字是不是品牌，影响品牌面板的显示或者隐藏
    $scope.keywordsIsBrand = function () {

        var  brands =   $scope.resultMap.brandList;
        for (var i = 0; i < brands.length; i++) {

            var brand = brands[1];
            var brandName = brand.text;
            if( $scope.searchMap.keywords.indexOf( brandName)>=0){
                return true;
            }
        }
        return  false;
    };

    // $location  方法进行index.html 传递过来的搜索关键字
    $scope.obtainLoadkeywords=function(){
        $scope.searchMap.keywords =  $location.search()['keywords'];
        $scope.search();
    }

});