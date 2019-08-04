 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.brandIds=  JSON.parse($scope.entity.brandIds);//转换品牌列表
				$scope.entity.specIds=  JSON.parse($scope.entity.specIds);//转换品牌列表
				$scope.entity.customAttributeItems=  JSON.parse($scope.entity.customAttributeItems);//转换品牌列表
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	};
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	};
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};


	// 处理前端数据的查询和展示以供选择使用
	$scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};//品牌列表
	$scope.specList={data:[{id:1,text:'屏幕尺寸'},{id:2,text:'运行内存'},{id:3,text:'电池容量'}]};// 规格

	$scope.findBrandList=function () {
		brandService.selectBrandOptionList().success(
			function (response) {
				$scope.brandList={data:response}
		})
	};

	$scope.findSpecList=function () {
		specificationService.selectSpecficationOptionList().success(
			function (response) {
				$scope.specList={data:response}
			})
	};

	// 添加行
	$scope.addTableRow=function (){
		$scope.entity.customAttributeItems.push({});
	};
	// 删除行
	$scope.deleTableRow=function (index){
		$scope.entity.customAttributeItems.splice(index,1);
	}

});	
