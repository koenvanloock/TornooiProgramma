
angular.module("tornooiControllers").controller("TournamentController",["TournamentService","$scope",'$location',
    function TournamentController(tournamentService, $scope,$location){
        $scope.selectedTournament = {
            series: []
        };
        $scope.name="";
        $scope.showClub = false;
        $scope.hasMultipleSeries = false;
        $scope.maximumNumberOfSeriesEntries= 2;
        $scope.date=new Date();
        $scope.tournaments = [];
        $scope.numberList = [1,2,3,4,5];


    tournamentService.getTournaments().then(
        function(result){$scope.tournaments = result.data}
    );
    var tournamentdate = {"year": $scope.date.getFullYear(), "month": ($scope.date.getMonth()+1), "day": $scope.date.getDate()};

   $scope.createTournament = function(){tournamentService.addTournament($scope.name,tournamentdate, $scope.hasMultipleSeries, $scope.maximumNumberOfSeriesEntries, $scope.showClub)
       .success( function(id){
           tournamentService.getTournament(id).then( function(result){
               $scope.selectedTournament = result.data;
               tournamentService.setCurrentTournament($scope.selectedTournament);
               $location.path('/tournament/' + id + "/series");
           });

       })};

    $scope.selectTournament = function(id){
        tournamentService.getTournament(id).then(
            function(result) {
                $scope.selectedTournament = result.data;
                tournamentService.setCurrentTournament($scope.selectedTournament);
            })
        };

        $scope.goto = function(path) {
          $location.path(path);
        }
}]);