'use strict';

angular.module('tornooiControllers').controller('DrawController', ['$scope', 'PlayerService', 'TournamentService', 'SeriesService', '$location', '$routeParams','baseUrl',
    function ($scope, playerService, tournamentService, seriesService, $location, $routeParams, baseUrl) {

        if (tournamentService.getCurrentTournament().id == $routeParams.id) {
            $scope.tournament = tournamentService.getCurrentTournament();
            $scope.showSeries = [];
            $scope.tournament.series.map(function(series){
                $scope.showSeries.push(false);
                series.seriesRounds = [];
            })
        } else {
            tournamentService.getTournament($routeParams.id).success(function (data) {
                $scope.tournament = data;
                $scope.showSeries = [];
                $scope.tournament.series.map(function(series){
                    $scope.showSeries.push(false);
                    series.seriesRounds = [];
                })
            })
        }

        $scope.drawSeries = function(seriesIndex){
            seriesService.drawSeries($scope.tournament.series[seriesIndex].seriesId, $scope.tournament.series[seriesIndex].currentRoundNr).success(function(drawnSeries){
                $scope.tournament.series[seriesIndex].seriesRounds.push(drawnSeries);
                console.log($scope.tournament.series[seriesIndex]);
            });
        };

        $scope.showDrawnSeries = function(seriesIndex){
            return $scope.showSeries[seriesIndex];
        };

        $scope.changeShowDrawnSeries = function(seriesIndex){
            $scope.showSeries[seriesIndex] = !$scope.showSeries[seriesIndex];
        };

        $scope.printHandicap = function (isB, isForB, handicap) {
            return (isForB === isB) ? "+" + handicap : "";
        };

        $scope.showBracketPlayer = function(bracket, playerId){
            for(var i=0;i<bracket.bracketPlayers.length;i++){
                if(playerId == bracket.bracketPlayers[i].playerId) {
                    return bracket.bracketPlayers[i].firstname + " " + bracket.bracketPlayers[i].lastname;
                }
            }

        };

        $scope.gotoMainOverview = function(){
            $location.path("/tournamentMenu/"+$scope.tournament.tournament.tournamentId)
        }

    }]);