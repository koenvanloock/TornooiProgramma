'use strict';

angular.module('tornooiControllers').controller('DrawController', ['$scope', 'PlayerService', 'TournamentService', 'SeriesService', '$location', '$routeParams',
    function ($scope, playerService, tournamentService, seriesService, $location, $routeParams) {

        if (tournamentService.getCurrentTournament().id == $routeParams.id) {
            $scope.tournament = tournamentService.getCurrentTournament();
            $scope.showSeries = [];
            $scope.tournament.series.map(function(){
                $scope.showSeries.push(false);
            })
        } else {
            tournamentService.getTournament($routeParams.id).success(function (data) {
                $scope.tournament = data;
                $scope.showSeries = [];
                $scope.tournament.series.map(function(){
                    $scope.showSeries.push(false);
                })
            })
        }

        $scope.drawSeries = function(seriesIndex){
            seriesService.drawSeries($scope.tournament.series[seriesIndex].seriesId, 1).success(function(drawnSeries){
                $scope.tournament.series[seriesIndex].seriesRounds[0] = drawnSeries;
                console.log(drawnSeries);
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

        }
    }]);