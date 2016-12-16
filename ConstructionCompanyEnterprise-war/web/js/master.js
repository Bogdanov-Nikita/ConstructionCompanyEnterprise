/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('Master', []);

app.controller('MainController', ['$scope','$http', function($scope,$http) {
    $http.get("http://localhost:8080/ConstructionCompanyEnterprise-war/api/master")
    .then(function (response) {
        //function handles success
        $scope.masterData = response.data;
    },function (response) {
        //function handles error
        $scope.masterData = response.data;
    });
    
    
    //save profile
    $scope.onClickSaveProfile = function(){
        //функция сохранить профиль
        
        var profileData = {
            name: document.getElementById('mastername').value,
            phone: document.getElementById('masterphone').value
        };
                
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/master/profile",
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
    
    $scope.onClickTableItem = function(index){
        $("#"+index+" tr[id]").each(
                function (){
                    $("#" + $(this).attr("id")).slideToggle("slow");
            });
    };
    
    $scope.onClickChangeWorkStatus = function(index){        
        if(confirm("вы точно хотите изменить статус работы\n"+
                $scope.masterData.works[index].description + 
                " ?")){            
            //смена статуса работы
            var workStatusData = {
                action: "WorkFinish"
            };

            $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/master/array/works/"+
                    $scope.masterData.works[index].workId + "/update",
            JSON.stringify(workStatusData),
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
    
    $scope.compareStatus = function(status){
        return status.localeCompare("не завершена");
    };   
}]);

