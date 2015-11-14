"use strict";

angular.module("tornooiControllers").controller("seriesRoundController", ["$scope", "$location", "SeriesService", "SeriesRoundService", "$routeParams", function($scope, $location, seriesService, seriesRoundService, $routeParams){
    var closed = [];

    $scope.toggleOpenClosed = function(index){
        closed[index] = !closed[index];
    };

    $scope.isClosed = function(index){ return closed[index];};

    seriesService.getAllSeriesOfTournament($routeParams.id).then(
        function(response){
            $scope.seriesList= [];
            response.data.map(function(series){
                $scope.seriesList.push(series);
                closed.push(true);
            })
    });

    $scope.selectedSeries = {};

    $scope.getRoundsOfSeries = function(){
        console.log($scope.selectedSeriesId);
        for(var i=0; i < $scope.seriesList.length;i++) {
            if( parseInt($scope.selectedSeriesId) == $scope.seriesList[i].seriesId) {
                $scope.selectedSeries = $scope.seriesList[i];
            }
        }
        console.log($scope.selectedSeries);

    };

    $scope.createSeriesRound = function(index){
        var initSeriesRound = {roundType: 'B', numberOfBracketRounds: 0, numberOfRobinGroups: 0};
        closed[index] = false;
        if($scope.seriesList[index].rounds == undefined) $scope.seriesList[index].rounds = [];
        seriesRoundService.createSeriesRound(initSeriesRound).then(function(response){
            console.log(response.data);
            $scope.seriesList[index].rounds.push(response.data);
        })
    };

    $scope.gotoPlayerSubscription = function(){
        console.log("link to player");
        $location.path("/" + $routeParams.id + "/playerSubscription")
    };

    $scope.gotoSeriesSetup = function(){
        console.log("link to series");
        $location.path("tournament/" + $routeParams.id + "/series")
    }

}]);

angular.module("tornooiControllers").controller("roundSetupController",["$scope", "SeriesRoundService",function($scope, seriesService){

    $scope.roundTypes = [
        {"value": "R", "name": "Pouleronde"},
        {"value": "B", "name": "Tabel"}
    ];
    $scope.numberOfRobinsList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
    $scope.bracketRounds = [
        {"name": "zestiende finales", "value": 5},
        {"name": "achtste finales", "value": 4},
        {"name": "kwartfinales", "value": 3},
        {"name": "halve finales", "value": 2},
        {"name": "finale", "value": 1}
    ];

    console.log($scope.round);

    $scope.showSeriesRoundType = function(){
        console.log($scope.round);
        if ($scope.round.roundType === 'R') {
            return "PouleRonde";
        } else if (roundType === 'B') {
            return "Tabelronde";
        } else {
            return "Onbepaald";
        }
    };

    $scope.showBracketRounds = function () {
        console.log($scope.round.numberOfBracketRounds);
        var numberOfBrackets = $scope.round.numberOfBracketRounds;
        console.log(numberOfBrackets);
        switch (numberOfBrackets) {
            case 1:
                return "finale (2 spelers)";
            case 2:
                return "halve finales (4 spelers)";
            case 3:
                return "kwartfinales (8 spelers)";
            case 4:
                return "achtste finale (16 spelers)";
            case 5:
                return "zestiende finales (32 spelers)";
            default:
                return "Kies de startronde";
        }
    };

    $scope.updateRound = function(){
        console.log($scope.round);
        var round = {
          roundType: $scope.round.roundType,
          numberOfBracketRounds: 0,
            numberOfRobinGroups: 0
        };
        seriesService.updateSeriesRound(round).then(
            function(result){
            $scope.round = result.data;
        })
    };


}]);