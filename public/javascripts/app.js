'use strict';

var tornooiSite = angular.module("TornooiSite", ['tornooiControllers', 'tornooiServices', 'ngRoute', 'ngMaterial','ngCookies','angular-jwt']);

var tornooiControllers = angular.module("tornooiControllers", []);
var tornooiServices = angular.module("tornooiServices", []).constant('baseUrl', {
    url: 'http://localhost:9000'
});

tornooiSite.directive("editTournament", function () {
    return {
        restrict: 'E',
        templateUrl: "assets/partials/createTournament.html",
        controller: "TournamentController"
    }
});

tornooiSite.directive("tournamentList", function () {
    return {
        restrict: 'E',
        templateUrl: "partials/tournamentList.html"
    }
});

tornooiSite.directive("seriesRules", function () {
    return {
        restrict: 'E',
        templateUrl: "assets/directives/seriesRules.html"
    }
});

tornooiSite.directive("tournamentOverview", function () {
    return {
        restrict: 'E',
        templateUrl: "assets/directives/tournamentOverview.html"
    }
});

tornooiSite.directive("seriesRoundSetup", function(){
    return{
        restrict: 'E',
        scope: {round: '='},
        templateUrl: "assets/directives/SeriesRoundSetup.html",
        controller: "roundSetupController"
    }
});

tornooiSite.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});


tornooiSite.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('green');
});

tornooiSite.config(function ($mdThemingProvider) {
    // Configure a dark theme with primary foreground yellow
    $mdThemingProvider.theme('docs-dark', 'default')
        .primaryPalette('yellow')
        .dark();
});