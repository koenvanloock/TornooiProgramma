angular.module("tornooiControllers").controller("TournamentMenuController", ["TournamentService", "SeriesService", "$scope", '$routeParams', '$location',
    function TournamentController(tournamentService, seriesService, $scope, $routeParams, $location) {
        if(tournamentService.getCurrentTournament().id == $routeParams.id) {
            $scope.tournament = tournamentService.getCurrentTournament();
            $scope.tournament.series.map(fetchSeriesplayers);

        } else{ tournamentService.getTournament($routeParams.id).success(function(data){
            $scope.tournament = data;
            $scope.tournament.series.map(fetchSeriesplayers);
        })}


        function fetchSeriesplayers(series) {
            seriesService.fetchSeriesPlayers(series.seriesId).success(
                function(seriesPlayers) {
                    series.seriesPlayers = seriesPlayers;
                }
            )}

        $scope.gotoStart = function(){
            $location.path("/mainOverview/"+ $routeParams.id);
        };

        $scope.gotoDraw = function(){
            $location.path("/drawSeries/"+$routeParams.id);
        };

        $scope.gotoTournamentSetup = function(){
            $location.path();
        };

        $scope.gotoTournamentSeriesSetup = function(){
            $location.path("/tournamentOverview/"+$routeParams.id);
        };

        $scope.gotoPlayerSubscription = function(){
            $location.path("/" + $routeParams.id + "/playerSubscription");
        }
    }]);