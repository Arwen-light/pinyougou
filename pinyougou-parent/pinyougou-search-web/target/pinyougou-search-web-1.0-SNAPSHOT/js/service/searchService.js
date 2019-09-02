app.service("searchService",function ($http) {
    this.search=function (reachMap) {
        return $http.post("/itemsearch/search.do",reachMap);
    }
});