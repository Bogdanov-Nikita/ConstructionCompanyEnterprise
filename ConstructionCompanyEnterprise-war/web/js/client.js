/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('Client', []);

app.controller('MainController', ['$scope','$http', function($scope,$http) {
    
    $http.get("http://localhost:8080/ConstructionCompanyEnterprise-war/api/client")
    .then(function (response) {
        //function handles success
        $scope.clientData = response.data;
    },function (response) {
        //function handles error
        $scope.clientData = response.data;
    });
    
    // Get the modal dialog
    var payDilog = document.getElementById("payDialog");
    var TempOrderIndex = 0;
    var TempEstimateIndex = 0;
    
    $scope.payEstimateDialog = function(orderIndex,estimateIndex){ 
        TempOrderIndex = orderIndex;
        TempEstimateIndex = estimateIndex;
        document.getElementById("orderTitle").innerHTML =
                "Оплатить заказ №" + $scope.clientData.orders[TempOrderIndex].number;
        payDilog.style.display = "block";
    };
    
    //When the user clicks pay
    $scope.payEstimate = function(){
        //функция оплаты сметы клиенотм        
        var orderData = {
            action: "payEstimate",
            estimateId:$scope.clientData.orders[TempOrderIndex].estimates[TempEstimateIndex].estimateId,
            pay: document.getElementById('clientpay').value
        };
        
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/client/orders/"+
                $scope.clientData.orders[TempOrderIndex].orderId.toString()+"/update",
        JSON.stringify(orderData),
        {"Content-Type": "application/json"})
        .then(
            function(response){
                // success callback
                if(response.data.info !== undefined){
                    alert("Успешное выполнение");
                    payDilog.style.display = "none";
                }else{
                    alert("Ошибка: " + response.data.error);
                }
            }, 
            function(response){
              // failure callback
              alert("Ошибка: Сервер не отвечает");
            }
        );
    };
    
    $scope.takeWork = function(orderIndex){
        //принятие работ, заказа
        if (confirm('Вы уверены что хотите принять работы данного заказа?')) {
            
            var orderData = {
                action: "takeWork"
            };
        
            $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/client/orders/"+
                    $scope.clientData.orders[orderIndex].orderId.toString()+"/update",
            JSON.stringify(orderData),
            {"Content-Type": "application/json"})
            .then(
                function(response){
                    // success callback
                    if(response.data.info !== undefined){
                        alert("Успешное выполнение");
                    }else{
                        alert("Ошибка: " + response.data.error);
                    }
                }, 
                function(response){
                  // failure callback
                  alert("Ошибка: Сервер не отвечает");
                }
            );
        }
    };
    
    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target === payDilog) {
            payDilog.style.display = "none";
        }
    };
    
    // When the user clicks on (x), close the modal
    $scope.closer = function() {
        payDilog.style.display = "none";
    };
    
    //save profile
    $scope.onClickSaveProfile = function(){
        //функция сохранить профиль
        
        var profileData = {
            name: document.getElementById('clientname').value,
            phone: document.getElementById('clientphone').value,
            address: document.getElementById('clientaddress').value,
            type: $scope.clientData.type
        };
                
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/client/profile",
        JSON.stringify(profileData), 
        {"Content-Type": "application/json"})
        .then(
            function(response){
                // success callback
                if(response.data.info !== undefined){
                    alert("Успешное выполнение");
                }else{
                    alert("Ошибка: " + response.data.error);
                }
              
            }, 
            function(response){
              // failure callback
              alert("Ошибка: Сервер не отвечает");
              
            }
        );
    };
    
    $scope.mouseOverTableItem = function(index) {
        document.getElementById(index).setAttribute("class","mouseover ng-scope");
    };
    
    $scope.mouseLeaveTableItem = function(index) { 
        document.getElementById(index).setAttribute("class","ng-scope");
    };
    
    $scope.compareStatusEstimate = function(status){
        return status.localeCompare("не оплачена | завершена");
    };
    
    $scope.compareStatusOrder = function(status){
        return status.localeCompare("отправлен на подтверждение, клиенту");
    };
}]);
