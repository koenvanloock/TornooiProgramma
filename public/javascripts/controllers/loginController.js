'use strict';

angular.module('tornooiControllers').controller('loginController', ['$scope', '$location', 'authService', '$timeout', '$interval',
    function($scope, $location, authService, $timeout, $interval) {
        $scope.loginBtnText = "Log in";
        $scope.loginFailed = false;
        $scope.withCredentials = {
            username: '',
            password: ''
        };

        $scope.login = function(credentials) {
            console.log("sending login");
            $scope.loginFailed = false;

            $timeout(function() {
                !$scope.loginFailed ? setLoggingIn() : '';
            }, 1000);

            authService.login(credentials).then(
                function() {
                    $location.path('/');
                },
                function() {
                    $scope.loginFailed = true;
                })
        };

        function setLoggingIn() {
            $scope.loginBtnText = "Logging in";
            var counter = 0;

            var changeSearchBtnTextInterval = $interval(function() {
                if (!$scope.foundFile && !$scope.errorMessage) {
                    if (counter != 3) {
                        $scope.loginBtnText += ".";
                        counter++;
                    } else {
                        $scope.loginBtnText = "Logging in";
                        counter = 0;
                    }
                } else {
                    $scope.loginBtnText = "Log in";
                    $interval.cancel(changeSearchBtnTextInterval);
                }
            }, 500);
        }
    }
]);