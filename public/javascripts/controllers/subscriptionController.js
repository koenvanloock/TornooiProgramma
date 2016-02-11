'use strict';

angular.module('tornooiControllers').controller('SubscriptionController', ['$scope', 'PlayerService', 'TournamentService', 'SeriesService', '$location', '$routeParams',
    function ($scope, playerService, tournamentService, seriesService, $location, $routeParams) {
        $scope.ranks = [];
        $scope.inserting = true;
        $scope.editing = false;
        $scope.seriesSubscriptions = [];
        $scope.seriesSubscriptions.push({seriesName: "", seriesId: ""});
        $scope.subscription = {
            "seriesSubscriptions": []
        };

        if(tournamentService.getCurrentTournament().tournamentId == $routeParams.id) {
            $scope.tournament = tournamentService.getCurrentTournament();
            init();
        } else{ tournamentService.getTournament($routeParams.id).success(function(data){ $scope.tournament = data;
            init();
        })}

        var pushPlayer = function (player) {
            $scope.allPlayers.push(
                {
                    'playerId': player.playerId,
                    'firstname': player.firstname,
                    'lastname': player.lastname,
                    'rank': player.rank
                });
        };

        playerService.getTournamentPlayers().then(
            function (result) {
                result.data.map(pushPlayer);
            });


        $scope.allPlayers = [];
        playerService.getRanks().then(
            function (result) {
                result.data.map(function (x) {
                    $scope.ranks.push({'name': x.name, 'value': x.value})
                });
            }
        );

        function init(){
            $scope.numberOfSeriesEntries = new Array($scope.tournament.maximumNumberOfSeriesEntries);
            $scope.tournament.series.map(function(series){$scope.seriesSubscriptions.push(series)});
            $scope.tournament.series.map(fetchSeriesplayers);
            createSubscriptions();
        }

        function fetchSeriesplayers(series) {
            seriesService.fetchSeriesPlayers(series.seriesId).success(
                function(seriesPlayers) {
                    series.seriesPlayers = seriesPlayers;
                }
            )}


        function createSubscriptions(){
            for(var i=0; i< $scope.tournament.maximumNumberOfSeriesEntries; i++){
                $scope.subscription.seriesSubscriptions.push({});
            }
        }

        $scope.playerSelection = {
            "isDisabled": false,
            "selectedItem": null
        };


        $scope.querySearch = function(query) {
            return query ? $scope.allPlayers.filter(createFilterFor(query)) : $scope.allPlayers;
        };


        function createFilterFor(query) {
            var lowercaseQueryList = angular.lowercase(query).split(' ');
            return function filterFn(person) {
                var contains = false;

                lowercaseQueryList.map(function(queryPart){
                    contains = contains || (angular.lowercase(person.firstname).indexOf(queryPart) === 0) || (angular.lowercase(person.lastname).indexOf(queryPart) === 0);
                });
                return contains;
            };
        }

        $scope.getSeriesSubscriptions = function(){
            if($scope.playerSelection.selectedItem) {
                playerService.getSeriesSubscriptionsOfPlayer($scope.playerSelection.selectedItem.playerId, $scope.tournament.tournament.tournamentId).success(function (response) {

                    $scope.subscription.seriesSubscriptions = response.map(getSubscriptionOfSeries);
                });
            }

        };

        var getSubscriptionOfSeries = function(series){
          for(var i=0; i<$scope.seriesSubscriptions.length;i++){
              if($scope.seriesSubscriptions[i].seriesId == series.seriesId){
                  return $scope.seriesSubscriptions[i].seriesId;
              }
          }
        };

        $scope.enterPlayer = function(){
            var seriesToSubscribe = [];
            $scope.subscription.seriesSubscriptions.map(function(subscriptionId){
                console.log("subscriptionID "+ subscriptionId);
                 seriesToSubscribe.push(subscriptionId)
                }
            );
            createSubscription()
        };

        var createSubscription = function(){
            var subscriptionList = $scope.subscription.seriesSubscriptions.map(function(subscription){
                return {
                    playerId:  $scope.playerSelection.selectedItem.playerId,
                    rank: $scope.playerSelection.selectedItem.rank.value,
                    seriesId: subscription
                }
            });
            console.log($scope.tournament);
            playerService.subscribePlayer(subscriptionList, $scope.tournament.tournament.tournamentId).success(function(data){
                $scope.tournament.series.map(fetchSeriesplayers);
                createSubscriptions();
            })
        };


        $scope.calculateTournamentPlayers = function(){
            if($scope.tournament!= undefined && $scope.tournament.series != undefined){
                return $scope.tournament.series.reduce(function(previousValue, currentValue){
                    return currentValue.seriesPlayers? previousValue + currentValue.seriesPlayers.length : previousValue;
                },0)} else {return 0;}
        };

        $scope.gotoSeriesSetup = function(){
            $location.path("tournament/" + $routeParams.id + "/series")
        };

        $scope.gotoRoundsSetup = function(){
            $location.path("/tournament/" + $routeParams.id + "/rounds");
        };

    }

]);