// 控制层抽取代码
app.service("brandService", function ($http) {

    this.findAll = function () {
        return $http.post("../brand/findAll.do")
    };

    this.findPage = function (page, rows) {
        return $http.post("../brand/findByPage.do?page=" + page + "&rows=" + rows + "")
    };


    this.add = function (entity) {
        return $http.post("../brand/add.do", entity)
    };

    this.update = function (entity) {
        return $http.post("../brand/update.do", entity)
    };

    this.findOne = function (id) {
        return $http.post("../brand/findOne.do?id=" + id)
    };

    this.del = function (selectIds) {
        return $http.post("../brand/delete.do?ids=" + selectIds)
    };

    this.search = function (page, rows, searchEntity) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, searchEntity)
    };

    this.selectBrandOptionList = function () {
        return $http.post("../brand/selectBrandOptionList.do?")
    }

});