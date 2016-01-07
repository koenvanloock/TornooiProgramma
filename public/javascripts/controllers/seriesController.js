angular.module("tornooiControllers").controller("SeriesController", ['$scope', '$location', 'SeriesService', 'TournamentService','$routeParams', '$mdDialog',
    function($scope, $location, seriesService, tournamentService, $routeParams, $mdDialog){
        $scope.numberOfSetsToWinList= [1,2,3,4,5];
        $scope.extraHandicapForRecsList= [0,1,2,3,4,5];
        $scope.targetScores= [11,21];

    tournamentService.getTournament($routeParams.id).then(function(tournament){
            $scope.tournament = tournament.data;
    });

    $scope.createSeries = function(ev) {
        $mdDialog.show($mdDialog.alert({
            controller: "createSeriesController",
            templateUrl: "assets/dialogs/createSeriesDialog.html",
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        })).then(function(newSeries){
            newSeries.tournamentId = $scope.tournament.tournament.tournamentId;
            seriesService.addSeries(newSeries);
            $scope.tournament.series.push(newSeries);
        });
    };

    $scope.updateSeries = function(index){
        var seriesToUpdate = $scope.tournament.series[index];
        seriesService.updateSeries(seriesToUpdate);
    };

    $scope.gotoPlayerSubscription = function(event){
        if($scope.tournament.series.length > 0) {
            $location.path("/"+$routeParams.id+"/playerSubscription");
        }else{
            function showNoSeries(event) {
                $mdDialog.show(
                    $mdDialog.alert()
                        .clickOutsideToClose(true)
                        .title('Geen reeksen in dit tornooi')
                        .content('U hebt geen reeksen gemaakt in een tornooi met meerdere reeksen!')
                        .ariaLabel('Alert No series')
                        .ok('OK')
                        .targetEvent(event)
                );
            }

            showNoSeries(event);
        }
    }
}]);

angular.module("tornooiControllers").controller("createSeriesController", ["$scope","$mdDialog", function($scope, $mdDialog){

    $scope.series = {
        "seriesName": "",
        "seriesColor": "#ffffff",
        "setTargetScore": 21,
        "numberOfSetsToWin": 2,
        "playingWithHandicaps": false,
        "extraHandicapForRecs": 0,
        "showReferees": false
    };

    $scope.numberOfSetsToWinList= [1,2,3,4,5];
    $scope.extraHandicapForRecsList= [0,1,2,3,4,5];
    $scope.targetScores= [11,21];


    $scope.flipPlayingWithHandicaps = function () {
        $scope.series.playingWithHandicaps = !$scope.series.playingWithHandicaps
    };
    $scope.flipShowReferees = function () {
        $scope.series.showReferees = !$scope.series.showReferees
    };

    $scope.hide = function() {
        $mdDialog.hide();
    };
    $scope.cancel = function() {
        $mdDialog.cancel();
    };
    $scope.answer = function() {
        $scope.series.numberOfSetsToWin = parseInt($scope.series.numberOfSetsToWin);
        $scope.series.setTargetScore = parseInt($scope.series.setTargetScore);

        $mdDialog.hide($scope.series);
    };

}]);