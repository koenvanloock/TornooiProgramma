"use strict";

angular.module("tornooiControllers").controller("seriesRoundController", ["$scope", "$location", "SeriesService", "SeriesRoundService", "$routeParams", "$rootScope", function($scope, $location, seriesService, seriesRoundService, $routeParams, $rootScope){
    var closed = [];

    $scope.toggleOpenClosed = function(index){
        closed[index] = !closed[index];
        $scope.selectionChanged(index);
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

    $rootScope.$watch(seriesRoundService.getRoundsOfSeries(), function(){
        $scope.seriesList[0].rounds = seriesRoundService.getRoundsOfSeries();
    });



    $scope.createSeriesRound = function(index){
        var initSeriesRound = {seriesId: $scope.seriesList[index].seriesId, roundType: "B", numberOfBracketRounds: 0, numberOfRobinGroups: 0};
        closed[index] = false;
        if($scope.seriesList[index].rounds == undefined) $scope.seriesList[index].rounds = [];
        seriesRoundService.createSeriesRound(initSeriesRound).then(function(response){
            $scope.seriesList[index].rounds.push(response.data);
        })
    };

    $scope.gotoPlayerSubscription = function(){
        $location.path("/" + $routeParams.id + "/playerSubscription")
    };

    $scope.gotoSeriesSetup = function(){
        $location.path("tournament/" + $routeParams.id + "/series")
    };

    $scope.selectionChanged = function(index){
        seriesRoundService.loadRoundsOfSeries($scope.seriesList[index].seriesId).then(function(roundList){
            console.log("roundlist");
            console.log(roundList);
            seriesRoundService.setRoundsOfSeries(roundList.data);
            $scope.seriesList[index].rounds = seriesRoundService.getRoundsOfSeries();
         });

    }

}]);

angular.module("tornooiControllers").controller("roundSetupController",["$scope", "SeriesRoundService",function($scope, seriesRoundService){

    $scope.roundTypes = [
        {"type": "R", "name": "Pouleronde"},
        {"type": "B", "name": "Tabel"}
    ];
    $scope.numberOfRobinsList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
    $scope.bracketRounds = [
        {"name": "zestiende finales", "value": 5},
        {"name": "achtste finales", "value": 4},
        {"name": "kwartfinales", "value": 3},
        {"name": "halve finales", "value": 2},
        {"name": "finale", "value": 1}
    ];

    $scope.showSeriesRoundType = function(){
        if ($scope.round.roundType == "R") {
         return $scope.roundTypes[0];
         } else if ($scope.round.roundType == "B") {
         return $scope.roundTypes[1];
         } else {
         return {};
         }
    };

    $scope.round.roundType = $scope.showSeriesRoundType();





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

    $scope.updateRound = function(round){
        var roundToUpdate = {
            roundType: round.roundType.type,
            seriesRoundId: round.seriesRoundId,
            numberOfBracketRounds: 0,
            numberOfRobinGroups: 0,
            seriesId: round.seriesId,
            roundNr: round.roundNr
        };
        seriesRoundService.updateSeriesRound(roundToUpdate).then(
            function(result){
            $scope.round = {
                roundType: (result.data.roundType.type!=undefined || result.data.roundType =="B") ? $scope.roundTypes[1]: $scope.roundTypes[0],
                seriesRoundId: result.data.seriesRoundId,
                numberOfBracketRounds: result.data.numberOfBracketRounds,
                numberOfRobinGroups: result.data.numberOfRobins,
                seriesId: result.data.seriesId,
                roundNr: result.data.roundNr
            };
        })
    };


    $scope.showMoveUp = function(){
        return $scope.round.roundNr > 1;
    };

    $scope.showMoveDown = function(){
        return $scope.round.roundNr < seriesRoundService.getRoundCount();
    };

    $scope.moveSeriesRoundUp = function(){
        seriesRoundService.moveSeriesUp($scope.round);
    };

    $scope.moveSeriesRoundDown = function(){
        seriesRoundService.moveSeriesDown($scope.round);
    }

}]);