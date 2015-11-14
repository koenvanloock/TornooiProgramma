"use strict";

angular.module("tornooiServices").factory("SeriesRoundService", ["$http", "baseUrl", function($http, baseUrl) {
    return {
        createSeriesRound: function(seriesRoundJson){
            return $http.post(baseUrl.url + "/seriesround", seriesRoundJson)
        },

        updateSeriesRound: function(seriesRoundJson){
            return $http.put(baseUrl.url + "/seriesround", seriesRoundJson )
        }
    }
}
]);