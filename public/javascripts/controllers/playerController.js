'use strict';

angular.module('tornooiControllers').controller('PlayerController', ['$scope', 'PlayerService', 'TournamentService', 'SeriesService', '$location', '$routeParams',
    function ($scope, playerService, tournamentService, seriesService, $location, $routeParams) {
        $scope.ranks = [];
        $scope.inserting = true;
        $scope.editing = false;
        $scope.playerToEdit = {"rank": {"name": null, "value": null}};
        $scope.allPlayers = [];
        playerService.getRanks().then(
            function (result) {
                result.data.map(function (x) {
                    $scope.ranks.push({'name': x.name, 'value': x.value})
                });
            }
        );

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

        $scope.addPlayer = function () {
            playerService.postPlayer($scope.playerToEdit).success(
                function (insertedPlayer) {
                    pushPlayer(insertedPlayer);
                    $scope.playerToEdit = {}
                }
            );
        };

        $scope.startEditPlayer = function(playerindex){
            if($scope.editIndex != playerindex) {
                $scope.playerToEdit = {"rank":{}};
                $scope.playerToEdit.playerId = $scope.allPlayers[playerindex].playerId;
                $scope.playerToEdit.firstname = $scope.allPlayers[playerindex].firstname;
                $scope.playerToEdit.lastname = $scope.allPlayers[playerindex].lastname;
                $scope.playerToEdit.rank.value = $scope.allPlayers[playerindex].rank.value;


                $scope.editIndex = playerindex;
                $scope.editing = true;
                $scope.inserting = false;
            } else {
                $scope.playerToEdit = {};
                $scope.editIndex = -1;
                $scope.editing = false;
                $scope.inserting = true;
            }
        };

        $scope.editPlayer = function(){
            $scope.playerToEdit.rank = $scope.ranks[$scope.playerToEdit.rank.value];
            playerService.updatePlayer($scope.playerToEdit).success(function(){
                $scope.allPlayers[$scope.editIndex] = $scope.playerToEdit;

                $scope.editing = false;
                $scope.inserting = true;
                $scope.playerToEdit = {};
                $scope.editIndex = -1;
            })
        };

        $scope.deletePlayer = function(playerIndex){
            var playerToDelete = $scope.allPlayers[playerIndex];
          playerService.deletePlayer(playerToDelete.playerId).success(function(){
              $scope.allPlayers.splice(playerIndex, 1);
          })
        };



    }

]);