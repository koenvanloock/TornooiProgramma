angular.module("tornooiControllers").controller("TournamentOverviewController", ["TournamentService", "SeriesService", "$scope", '$routeParams', '$location',
    function TournamentController(tournamentService, seriesService, $scope, $routeParams, $location) {
        $scope.openAddSeries = false;
        $scope.openEditSeries = false;
        $scope.openAddRound = false;
        $scope.openEditRound = false;
        $scope.roundTypes = [
            {"value": 'R', "name": "Pouleronde"},
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
        $scope.seriesToChange = {
            "seriesName": "",
            "seriesRules": {
                "numberOfSetsToWin": 2,
                "setTargetScore": 21,
                "playingWithHandicaps": true,
                "showReferees": false,
                "extraHandicapForRecs": 0
            },
            "seriesRounds": []
        };

        $scope.roundToChange = {
            "roundType": null,
            "numberOfRobins": null,
            "numberOfBracketRounds": null
        };


        tournamentService.getTournament($routeParams.id).success(function (data) {
            $scope.tournament = data;
        });

        $scope.addSeries = function () {
            checkInput();
            seriesService.addSeries($routeParams.id, $scope.seriesToChange.seriesName, $scope.seriesToChange.seriesRules).success(function (id) {
                $scope.tournament.series.push({
                    "seriesId": id,
                    "seriesName": $scope.seriesToChange.seriesName,
                    "seriesRules": $scope.seriesToChange.seriesRules,
                    "seriesRounds": []
                });
                $scope.openAddSeries = false;
                $scope.openEditSeries = false;
            })
        };

        $scope.deleteSeries = function (id) {
            seriesService.deleteSeries(id);
            for (var i = 0; i < $scope.tournament.series.length; i++) {
                if ($scope.tournament.series[i].seriesId === id) {
                    $scope.tournament.series.splice(i, 1);
                    $scope.openAddSeries = false;
                    $scope.openEditSeries = false;
                    return;
                }
            }
        };

        $scope.startEditSeries = function (seriesIndex) {
            $scope.seriesToChange = $scope.tournament.series[seriesIndex];
            $scope.openAddSeries = false;
            $scope.openEditSeries = !$scope.openEditSeries;
        };

        $scope.editSeries = function () {
            checkInput();
            seriesService.updateSeries($scope.seriesToChange);
            $scope.seriesToChange = {
                "seriesName": "",
                "seriesRounds": [],
                "seriesRules": {
                    "numberOfSetsToWin": 2,
                    "setTargetScore": 21,
                    "playingWithHandicaps": true,
                    "showReferees": false,
                    "extraHandicapForRecs": 0
                }
            };
            $scope.openEditSeries = false;
        };

        $scope.showAddSeries = function () {
            $scope.openAddSeries = !$scope.openAddSeries;
            $scope.openEditSeries = false;
            $scope.seriesToChange = {};
        };

        $scope.startAddSeriesRound = function () {
            $scope.openAddRound = !$scope.openAddRound;
            $scope.openEditRound = false;
            $scope.roundToChange = {
                "roundType": null,
                "numberOfRobins": null,
                "numberOfBracketRounds": null
            };
        };

        function checkInput() {
            if (!isNaN($scope.seriesToChange.seriesRules.numberOfSetsToWin) && !isNaN($scope.seriesToChange.seriesRules.setTargetScore) && !isNaN($scope.seriesToChange.seriesRules.extraHandicapForRecs)) {
                $scope.seriesToChange.seriesRules.numberOfSetsToWin = parseInt($scope.seriesToChange.seriesRules.numberOfSetsToWin);
                $scope.seriesToChange.seriesRules.setTargetScore = parseInt($scope.seriesToChange.seriesRules.setTargetScore);
                $scope.seriesToChange.seriesRules.extraHandicapForRecs = parseInt($scope.seriesToChange.seriesRules.extraHandicapForRecs);
                $scope.seriesToChange.seriesRules.playingWithHandicaps = $scope.seriesToChange.seriesRules.playingWithHandicaps ? $scope.seriesToChange.seriesRules.playingWithHandicaps : false;
                $scope.seriesToChange.seriesRules.showReferees = $scope.seriesToChange.seriesRules.showReferees ? $scope.seriesToChange.seriesRules.showReferees : false;

            } else {
                console.log("error, fouten in invoer")
            }
        }

        $scope.showRoundType = function () {
            if ($scope.roundToChange.roundType === 'R') {
                return "PouleRonde";
            } else if ($scope.roundToChange.roundType === 'B') {
                return "Tabel";
            } else {
                return "Kies een rondetype";
            }
        };

        $scope.showSeriesRoundType = function(roundType){
            if (roundType === 'R') {
                return "PouleRonde";
            } else if (roundType === 'B') {
                return "Tabelronde";
            } else {
                return "Onbepaald";
            }
        };

        $scope.showBracketRounds = function () {
            $scope.roundToChange.numberOfBracketRounds = parseInt($scope.roundToChange.numberOfBracketRounds);
            switch ($scope.roundToChange.numberOfBracketRounds) {
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

        $scope.showNumberOfRobinsSelector = function () {
            return $scope.roundToChange.roundType === 'R';
        };

        $scope.showBracketRoundsSelector = function () {
            return $scope.roundToChange.roundType === 'B';
        };

        $scope.addRound = function () {
            if ($scope.roundToChange.numberOfBracketRounds || $scope.roundToChange.numberOfRobins) {
                $scope.roundToChange.numberOfBracketRounds = parseInt($scope.roundToChange.numberOfBracketRounds);
                $scope.roundToChange.numberOfRobins = parseInt($scope.roundToChange.numberOfRobins);
                seriesService.addRound($scope.seriesToChange.seriesId, $scope.roundToChange).success(function (data) {
                    $scope.seriesToChange.seriesRounds.push(data);
                    $scope.roundToChange = {}
                })
            }
        };

        $scope.deleteRound = function (index) {

            var seriesId = $scope.seriesToChange.seriesId;
            var roundNr = $scope.seriesToChange.seriesRounds[index].roundNr;
            seriesService.deleteRound(seriesId, roundNr).success(function(data){
                $scope.seriesToChange.seriesRounds = data;
            });
            $scope.openAddRound = false;
            $scope.openEditRound = false;

        };

        $scope.startEditSeriesRound = function(index){
            $scope.roundToChange = $scope.seriesToChange.seriesRounds[index];
            console.log($scope.roundToChange);
            $scope.openEditRound = !$scope.openEditRound;
            $scope.openAddRound = false;
        };

        $scope.updateRound = function() {
            if ($scope.roundToChange.numberOfBracketRounds || $scope.roundToChange.numberOfRobins) {
                $scope.roundToChange.numberOfBracketRounds = parseInt($scope.roundToChange.numberOfBracketRounds);
                $scope.roundToChange.numberOfRobins = parseInt($scope.roundToChange.numberOfRobins);
                seriesService.updateRound($scope.seriesToChange.seriesId, $scope.roundToChange.roundNr, $scope.roundToChange)
                $scope.openEditRound = false;
                $scope.openAddRound = false;
            }
        };

        $scope.moveToPlayerSubscription = function(){
            tournamentService.setCurrentTournament($scope.tournament);
            $location.path("/"+ $routeParams.id +"/playerSubscription")
        };

        $scope.gotoMenu = function(){
            $location.path("/tournamentMenu/"+$routeParams.id);
        }
    }]);