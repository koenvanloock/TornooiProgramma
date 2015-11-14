angular.module("tornooiControllers").controller("MainController", ["TournamentService", "SeriesService", "$scope", '$routeParams', '$location',

    function MainController(tournamentService, seriesService, $scope, $routeParams, $location) {
        if(tournamentService.getCurrentTournament().id == $routeParams.id) {
            $scope.tournament = tournamentService.getCurrentTournament();
            selectFirstIfOneSeries()

        } else{ tournamentService.getTournament($routeParams.id).success(function(data){
            $scope.tournament = data;
            selectFirstIfOneSeries()
        })}

        // general
        function selectFirstIfOneSeries(){
            if($scope.tournament.series.length == 1){
                $scope.selectedSeries = $scope.tournament.series[0];
                if($scope.selectedSeries.seriesRounds.length == 1){
                    selectRound(1);
                    $scope.isBracketShown = $scope.selectedRound.roundType == "B";
                    calculateNumberOfSets();
                }
            }
            console.log($scope.tournament);
        }

        $scope.selectRound = function(roundNr){
            calculateNumberOfSets();
            $scope.selectedRound = $scope.selectedSeries.seriesRounds[roundNr-1];
            $scope.enableInputs = roundNr == $scope.selectedSeries.currentRound;
            console.log("selected round");
        };

        $scope.selectSeries = function(index){
            $scope.selectedSeries = $scope.tournament.series[index];
            selectRound(1); // tijdelijk, tot currentround gelezen wordt
            if($scope.selectedSeries.seriesRounds.length == 1){
                selectRound(1);
                calculateNumberOfSets();
            }
        };

        function calculateNumberOfSets(){
          if($scope.selectedSeries)
            return $scope.selectedSeries.seriesRules ? $scope.seriesSets = new Array($scope.selectedSeries.seriesRules.numberOfSetsToWin * 2 -1) : 0;
            return 0;
        }


        $scope.hasNoPreviousRound = function(){
            if($scope.selectedSeries!=undefined) {
                return $scope.selectedSeries.currentRound < 2;
            }else{
                return true;
            }
        };

        $scope.hasNoNextRound = function(){
            if($scope.selectedSeries!=undefined && $scope.selectedSeries.seriesRounds!=undefined) {return $scope.selectedSeries.currentRound >= $scope.selectedSeries.seriesRounds.length;
            }else{
                return true;
            }
        };

        $scope.showNextRound = function(){
           selectRound($scope.selectedRound.roundNr +1);
        };

        $scope.showPreviousRound = function(){
            selectRound($scope.selectedRound.roundNr - 1);
        };

        // RobinMethods
        $scope.selectRobin = function(index){
            $scope.selectedRobin = $scope.selectedRound.robinGroups[index];
        };

        $scope.printHandicap = function(match){
            return match.isHandicapForB ? "/+"+match.handicap : "+"+ match.handicap + "/";

        };

        $scope.calculateMatchResult= function(match){
            var result = {numberOfSetsForA: 0, numberOfSetsForB: 0};
            match.sets.map(function(matchSet){
                if(matchSet.pointA >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointA > matchSet.pointB+1){
                    result.numberOfSetsForA++;
                }else if(matchSet.pointB >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointB > matchSet.pointA+1){
                    result.numberOfSetsForB++;
                }
            });
            return result.numberOfSetsForA +" - " + result.numberOfSetsForB;
        };

        $scope.saveMatch = function(match){
            var result = {numberOfSetsForA: 0, numberOfSetsForB: 0};
            match.sets.map(function(matchSet){
                if(matchSet.pointA >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointA > matchSet.pointB+1){
                    result.numberOfSetsForA++;
                }else if(matchSet.pointB >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointB > matchSet.pointA+1){
                    result.numberOfSetsForB++;
                }
            });
            if(result.numberOfSetsForA == $scope.selectedSeries.seriesRules.numberOfSetsToWin || result.numberOfSetsForB == $scope.selectedSeries.seriesRules.numberOfSetsToWin){
                seriesService.updateMatch(match);
            }
        };


        $scope.saveRobin = function(){
            seriesService.updateRobin($scope.selectedRobin)
        };

        $scope.calculatePlayerStats = function(playerANr, playerBNr){
            updatePlayerStats(playerANr);
            updatePlayerStats(playerBNr);
        };



        function updatePlayerStats(playerNr){
            var playerToUpdate = {};
            for(var i=0; i< $scope.selectedRobin.robinPlayers.length; i++) {
                if($scope.selectedRobin.robinPlayers[i].playerId == playerNr) {
                    playerToUpdate = $scope.selectedRobin.robinPlayers[i];
                    break;
                }
            }
            playerToUpdate.wonMatches = 0;
            playerToUpdate.lostMatches = 0;
            playerToUpdate.wonSets = 0;
            playerToUpdate.lostSets = 0;
            playerToUpdate.wonPoints = 0;
            playerToUpdate.lostPoints = 0;

            var result = {};
            $scope.selectedRobin.robinMatches.map(function(robinMatch){
                result = {numberOfSetsForA: 0, numberOfSetsForB: 0};

                if(robinMatch.playerA == playerNr){

                    robinMatch.sets.map(function(matchSet){
                        playerToUpdate.wonPoints += matchSet.pointA;
                        playerToUpdate.lostPoints += matchSet.pointB;
                        if(matchSet.pointA >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointA > matchSet.pointB+1){
                            result.numberOfSetsForA++;
                        }else if(matchSet.pointB >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointB > matchSet.pointA+1){
                            result.numberOfSetsForB++;
                        }
                    });
                       playerToUpdate.wonSets += result.numberOfSetsForA;
                       playerToUpdate.lostSets += result.numberOfSetsForB;
                    if(result.numberOfSetsForA == $scope.selectedSeries.seriesRules.numberOfSetsToWin){
                        playerToUpdate.wonMatches += 1;
                    }else if(result.numberOfSetsForB == $scope.selectedSeries.seriesRules.numberOfSetsToWin){
                        playerToUpdate.lostMatches +=1;
                    }
                }else if(robinMatch.playerB == playerNr){
                    robinMatch.sets.map(function(matchSet){
                        playerToUpdate.wonPoints += matchSet.pointB;
                        playerToUpdate.lostPoints += matchSet.pointA;
                        if(matchSet.pointA >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointA > matchSet.pointB+1){
                            result.numberOfSetsForA++;
                        }else if(matchSet.pointB >= $scope.selectedSeries.seriesRules.setTargetScore && matchSet.pointB > matchSet.pointA+1){
                            result.numberOfSetsForB++;
                        }
                    });
                    playerToUpdate.wonSets += result.numberOfSetsForB;
                    playerToUpdate.lostSets += result.numberOfSetsForA;
                    if(result.numberOfSetsForB == $scope.selectedSeries.seriesRules.numberOfSetsToWin){
                        playerToUpdate.wonMatches += 1;
                    }else if(result.numberOfSetsForA == $scope.selectedSeries.seriesRules.numberOfSetsToWin){
                        playerToUpdate.lostMatches +=1;
                    }
                }
            })
        }

        $scope.printBracketPlayer = function(playerId){
            for(i=0; i<$scope.selectedRound.bracket.bracketPlayers.length;i++) {
                if ($scope.selectedRound.bracket.bracketPlayers[i].playerId === playerId) {
                    return $scope.selectedRound.bracket.bracketPlayers[i].firstname + " " + $scope.selectedRound.bracket.bracketPlayers[i].lastname;
                }
            }
        };

    }]);
