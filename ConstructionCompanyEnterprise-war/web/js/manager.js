/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('Manager', []);

app.controller('MainController', ['$scope','$http', function($scope,$http) {
    $http.get("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager")
    .then(function (response) {
        //function handles success
        $scope.managerData = response.data;
    },function (response) {
        //function handles error
        $scope.managerData = response.data;
    });
    
    // Get the modal dialog
    var clientDialog = document.getElementById("clientDialog");
    var masterDialog = document.getElementById("masterDialog");
    var TempIndex = 0;
    var dialogTypeAdd = true;//true - Add Dialog,false - Edit Dialog
    
    
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
    
    $scope.editStorege = function(storegeIndex){
        //функция изменить элемент storege
    };
    
    $scope.deleteStorege = function(storegeIndex){
        //функция удалить элемент storege
    };
    
    $scope.editResource = function(storegeIndex,resourceIndex){
        //функция изменить элемент resource
    };
    
    $scope.deleteResource = function(storegeIndex,resourceIndex){
        //функция удалить элемент resource
    };
    
    $scope.editResource = function(resourceIndex){
        //функция изменить элемент resource
    };
    
    $scope.deleteResource = function(resourceIndex){
        //функция удалить элемент resource
    };
    
    
    $scope.editWork = function(index){
        //функция изменить элемент work
    };

    $scope.deleteWork = function(index){
        //функция удалить элемент work
    };

    $scope.addClient = function(){
        dialogTypeAdd = true;
        document.getElementById("clientTitle").innerHTML = "Добавить клиента"; 
        document.getElementById('clientname').value = "";
        document.getElementById('clientphone').value = "";
        document.getElementById('clientaddress').value = "";
        document.getElementById('clientype').value = "юридический";
        clientDialog.style.display = "block";
    };
    
    $scope.addMaster = function(){
        dialogTypeAdd = true;
        document.getElementById("masterTitle").innerHTML = "Добавить мастера"; 
        document.getElementById('mastername').value = "";
        document.getElementById('masterphone').value = "";
        masterDialog.style.display = "block";
    };
    
    $scope.editClient = function(clientIndex){
        //функция изменить элемент client
        TempIndex = clientIndex;
        dialogTypeAdd = false;
        document.getElementById("clientTitle").innerHTML = "Редактировать";       
        document.getElementById('clientname').value = $scope.managerData.clients[TempIndex].name;
        document.getElementById('clientphone').value = $scope.managerData.clients[TempIndex].phone;
        document.getElementById('clientaddress').value = $scope.managerData.clients[TempIndex].address;
        document.getElementById('clientype').value = $scope.managerData.clients[TempIndex].type;
        clientDialog.style.display = "block";
    };
    
    $scope.editMaster = function(masterIndex){
        //функция изменить элемент client
        TempIndex = masterIndex;
        dialogTypeAdd = false;
        document.getElementById("masterTitle").innerHTML = "Редактировать";       
        document.getElementById('mastername').value = $scope.managerData.masters[TempIndex].name;
        document.getElementById('masterphone').value = $scope.managerData.masters[TempIndex].phone;
        clientDialog.style.display = "block";
    };
    
    
    
    $scope.deleteClient = function(clientIndex){
        //функция удалить элемент client
        $http.delete("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager/array/clients/" + 
                $scope.managerData.clients[clientIndex].id.toString());
        $scope.managerData.clients.splice(clientIndex, 1);
    };
    
    $scope.deleteMaster = function(masterIndex){
        //функция удалить элемент client
        $http.delete("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager/array/masters/" + 
                $scope.managerData.masters[masterIndex].id.toString());
        $scope.managerData.masters.splice(masterIndex, 1);
    };
    
    //When the user clicks Ok button
    $scope.clientAddEdit = function(){
        //функция изменить элемент client     
        var clientData = {
            name: document.getElementById('clientname').value,
            phone: document.getElementById('clientphone').value,
            address: document.getElementById('clientaddress').value,
            type: document.getElementById('clientype').value
        };
        
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager/array/clients/" +
                (dialogTypeAdd ? "" : $scope.managerData.clients[TempIndex].id.toString() + "/update"),
        JSON.stringify(clientData),
        {"Content-Type": "application/json"})
        .then(
            function(response){
                // success callback
                if(response.data.info !== undefined){
                    alert("Успешное выполнение");
                    if(dialogTypeAdd){
                        $scope.managerData.clients.push({
                            name: clientData.name,
                            phone: clientData.phone,
                            address: clientData.address,
                            type: clientData.type
                        });
                        clientDialog.style.display = "none";
                        window.location.reload();
                    }else{
                        $scope.managerData.clients[TempIndex].name = clientData.name;
                        $scope.managerData.clients[TempIndex].phone = clientData.phone;
                        $scope.managerData.clients[TempIndex].address = clientData.address;
                        $scope.managerData.clients[TempIndex].type = clientData.type;
                        clientDialog.style.display = "none";
                    }
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
    
    //When the user clicks Ok button
    $scope.masterAddEdit = function(){
        //функция изменить элемент client     
        var masterData = {
            name: document.getElementById('mastername').value,
            phone: document.getElementById('masterphone').value
        };
        
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager/array/masters/" +
                (dialogTypeAdd ? "" : $scope.managerData.masters[TempIndex].id.toString() + "/update"),
        JSON.stringify(masterData),
        {"Content-Type": "application/json"})
        .then(
            function(response){
                // success callback
                if(response.data.info !== undefined){
                    alert("Успешное выполнение");
                    if(dialogTypeAdd){
                        $scope.managerData.clients.push({
                            name: masterData.name,
                            phone: masterData.phone,
                            address: masterData.address,
                            type: masterData.type
                        });
                        masterDialog.style.display = "none";
                        window.location.reload();
                    }else{
                        $scope.managerData.masters[TempIndex].name = masterData.name;
                        $scope.managerData.masters[TempIndex].phone = masterData.phone;
                        masterDialog.style.display = "none";
                    }
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
    
    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        switch(event.target){
            case clientDialog:
                clientDialog.style.display = "none";
                break;
            case masterDialog:
                masterDialog.style.display = "none";
                break;
                
        }
    };
    
    // When the user clicks on (x), close the modal
    $scope.closer = function() {
        clientDialog.style.display = "none";
        masterDialog.style.display = "none";
    };
    
    
    $scope.onClickSaveProfile = function(){
        //функция сохранить профиль
        var profileData = {
            name: document.getElementById('managername').value,
            phone: document.getElementById('managerphone').value,
            address: document.getElementById('manageraddress').value
        };
                
        $http.put("http://localhost:8080/ConstructionCompanyEnterprise-war/api/manager/profile",
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
    
}]);
