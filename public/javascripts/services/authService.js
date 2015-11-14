'use strict';

angular.module('tornooiServices').factory('authService', ['$http', 'baseUrl', 'jwtHelper', '$cookieStore', '$location',
    function($http, baseUrl, jwtHelper, $cookieStore, $location) {
        var authService = {};
        var currentUser = {
            username: '',
            role: ''
        };

        authService.login = function(credentials) {
            return $http({
                url: '/login',
                method: 'POST',
                skipAuthorization: true,
                data: credentials
            }).then(function(response) {
                var token = response.data;

                $cookieStore.put('auth_token', token);
                authService.setCurrentUser(token);
            })
        };

        authService.logout = function() {
            $cookieStore.remove('auth_token');
            currentUser = undefined;


            $location.path('/login');
        };

        authService.isAuthenticated = function() {
            var token = $cookieStore.get('auth_token');
            var isValidToken = !!token;

            if (isValidToken && !jwtHelper.isTokenExpired(token)) {
                authService.setCurrentUser(token);
                return isValidToken;
            } else {
                authService.logout();
            }
        };

        authService.setCurrentUser = function(token) {
            var tokenPayload = jwtHelper.decodeToken(token);

            currentUser = {
                'username': tokenPayload.username,
                'role': tokenPayload.role
            };
        };

        authService.getCurrentUser = function() {
            return currentUser;
        };

        return authService;
    }
]);
