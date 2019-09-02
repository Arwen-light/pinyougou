app.controller("contentController", function ($scope, contentService) {


    // 定义一个数组接收广告分类Id 和 广告的分类列表
    $scope.contendCategorylist = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contendCategorylist[categoryId] = response;
            }
        );
    }


});