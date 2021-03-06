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

        subscribePlayer: function (tournamentId, playerAndSubscription) {
            return $http.post(baseUrl.url + '/tournament/' + tournamentId + '/player/subscription', playerAndSubscription)
        },

        updatePlayer: function (player) {
            return $http.put(baseUrl.url + "/player", player)
        },

        deletePlayer: function (id) {
            return $http.delete(baseUrl.url + '/player/' + id)
        },

        getSeriesSubscriptionsOfPlayer: function (tournamentId, playerId) {
            return $http.get(baseUrl.url + '/tournament/'+ tournamentId + '/player/' + playerId)
        }
    }

}]);