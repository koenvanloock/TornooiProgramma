'use strict';

angular.module('tornooiControllers').controller('SubscriptionController', ['$scope', 'PlayerService', 'TournamentService', 'SeriesService', '$location', '$routeParams',
    function ($scope, playerService, tournamentService, seriesService, $location, $routeParams) {
        $scope.ranks = [];
        $scope.inserting = true;
        $scope.editing = false;
        $scope.seriesSubscriptions = [];
        $scope.seriesSubscriptions.push({seriesName: "", seriesId: 0});
        $scope.subscription = {
            "seriesSubscriptions": []
        };

        if(tournamentService.getCurrentTournament().id == $routeParams.id) {
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
                console.log($scope.allPlayers);
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
            console.log($scope.seriesSubscriptions);
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
            playerService.getSeriesSubscriptionsOfPlayer($scope.tournament.id, $scope.playerSelection.selectedItem.playerId).success(function(data){
                $scope.subscription.seriesSubscriptions = data;
                console.log('subscriptions' + data)
            })
        };

        $scope.enterPlayer = function(){
            var seriesToSubscribe = [];
            $scope.subscription.seriesSubscriptions.map(function(subscriptionId){
                if(!isNaN(subscriptionId) && subscriptionId > 0){
                    seriesToSubscribe.push({"seriesId": parseInt(subscriptionId)})
                }
            });
            playerService.subscribePlayer($scope.tournament.id, {"player": {"firstname": $scope.playerSelection.selectedItem.firstname, "lastname": $scope.playerSelection.selectedItem.lastname, "rank": $scope.playerSelection.selectedItem.rank.value}, "seriesSubscriptions": seriesToSubscribe}).success(function(data){
                $scope.tournament.series.map(fetchSeriesplayers);
                createSubscriptions();
            })
        };



        $scope.calculateTournamentPlayers = function(){
            if($scope.tournament!= undefined && $scope.tournament.series != undefined){
                return $scope.tournament.series.reduce(function(previousValue, currentValue){
                    return previousValue + currentValue.seriesPlayers.length;
                },0)} else {return 0;}
        };

        $scope.gotoMenu = function(){
            $location.path("/tournamentMenu/"+$routeParams.id);
        };

    }

]);