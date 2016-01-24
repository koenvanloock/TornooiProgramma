angular.module("tornooiServices").factory("SeriesService", ["$http", "baseUrl",
    function ($http, baseUrl) {
        return {
            getSeries: function (tournamentId, seriesId) {
                return $http.get(baseUrl.url + "/tournament/" + tournamentId + "/series/" + seriesId);
            },

            getAllSeriesOfTournament: function(tournamentId){
                return $http.get(baseUrl.url +"/tournament/"+ tournamentId + "/series")
            },

            addSeries: function (series) {
                return $http.post(baseUrl.url + "/series",series);
            },

            deleteSeries: function (id) {
                return $http.delete(baseUrl.url + "/series/"+ id);
            },

            updateSeries: function(series){
                return $http.put(baseUrl.url + "/series/" + series.seriesId, series);
            },

            addRound: function(seriesId, seriesRound){
                return $http.post(baseUrl.url + "/series/" + seriesId + "/seriesRound", seriesRound);
            },

            deleteRound: function(seriesId, roundNr){
                return $http.delete(baseUrl.url + "/series/" + seriesId + "/seriesRound/" + roundNr);
            },

            updateRound: function(seriesId, roundNr, seriesRound){
                return $http.post(baseUrl.url + "/series/" + seriesId + "/seriesRound/" + roundNr, seriesRound)
            },

            fetchSeriesPlayers: function(seriesId){
                return $http.get(baseUrl.url + "/seriesplayers/"+seriesId)
            },

            drawSeries: function(seriesRoundId){
                return $http.post(baseUrl.url + "/drawseries/" +seriesRoundId);
            },

            updateMatch: function(match){
                return $http.post(baseUrl.url + "/match/" + match.matchId, match);
            },

            updateRobin: function(robin){
                return $http.post(baseUrl.url + "/robin/"+ robin.robinId, robin);
            }
        }
    }])
;