angular.module('tornooiServices').factory("PlayerService", ["$http", "baseUrl", function ($http, baseUrl) {
    return {
        getTournamentPlayers: function () {
            return $http.get(baseUrl.url + '/players');
        },

        getRanks: function () {
            return $http.get(baseUrl.url + '/ranks')
        },
        postPlayer: function (player) {
            player.rank.value = parseInt(player.rank.value);
            return $http.post(baseUrl.url + '/player', player)
        },

        subscribePlayer: function (seriesPlayerList, tournamentId) {
            return $http.post(baseUrl.url + '/seriesplayers/'+ tournamentId, seriesPlayerList)
        },

        updatePlayer: function (player) {
            return $http.put(baseUrl.url + "/player", player)
        },

        deletePlayer: function (id) {
            return $http.delete(baseUrl.url + '/player/' + id)
        },

        getSeriesSubscriptionsOfPlayer: function(playerId, tournamentId){
            return $http.get(baseUrl.url + '/seriesofplayer/'+playerId+'/'+tournamentId)
        }
    }

}]);