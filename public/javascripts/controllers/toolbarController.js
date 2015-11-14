angular.module("tornooiControllers").controller("toolbarController", ['$scope', '$location', 'authService',function($scope, $location, $authService){
    $scope.returnToMain = function(){
        $location.path("/index");
    };

    var originatorEv;
    $scope.openMenu = function($mdOpenMenu, ev) {
        originatorEv = ev;
        $mdOpenMenu(ev);
    };


    $scope.logout = function(){
        $authService.logout();
    }
}]);
