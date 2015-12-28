"use strict";

angular.module("tornooiServices").factory("SeriesRoundService", ["$http", "baseUrl", function($http, baseUrl) {
    var roundsOfSelectedSeries = {};



    return {
        createSeriesRound: function(seriesRoundJson){
            return $http.post(baseUrl.url + "/seriesround", seriesRoundJson)
        },

        updateSeriesRound: function(seriesRoundJson){
            return $http.put(baseUrl.url + "/seriesround", seriesRoundJson )
        },

        loadRoundsOfSeries: function(seriesId){
            return $http.get(baseUrl.url + "/seriesrounds/"+ seriesId);
        },

        setRoundsOfSeries: function(roundsOfSeries){
            roundsOfSelectedSeries = roundsOfSeries;
        },

        getRoundsOfSeries: function(){
            return roundsOfSelectedSeries;
        },
        getRoundCount: function(){
            return roundsOfSelectedSeries.length;
        },
        moveSeriesUp: function(roundToMove){
            if(roundToMove.roundNr > 1){
                var roundNr= roundToMove.roundNr;
                var seriesToMoveUp = roundsOfSelectedSeries[roundNr-1];
                seriesToMoveUp.roundNr -= 1;
                var seriesToMoveDown = roundsOfSelectedSeries[roundNr-2];
                seriesToMoveDown.roundNr += 1;
                roundsOfSelectedSeries[roundNr-2] = this.convertRoundType(seriesToMoveUp);
                roundsOfSelectedSeries[roundNr-1] = this.convertRoundType(seriesToMoveDown);
                updateSeriesRound(seriesToMoveUp);
                updateSeriesRound(seriesToMoveDown);
            }
        },
        moveSeriesDown: function(roundToMove){
            var roundNr= roundToMove.roundNr;
            var seriesToMoveDown = roundsOfSelectedSeries[roundNr-1];
            seriesToMoveDown.roundNr += 1;
            var seriesToMoveUp = roundsOfSelectedSeries[roundNr];
            seriesToMoveUp.roundNr -= 1;
            this.updateSeriesRound(seriesToMoveUp);
            this.updateSeriesRound(seriesToMoveDown);
            roundsOfSelectedSeries[roundNr-1] = this.convertRoundType(seriesToMoveUp);
            roundsOfSelectedSeries[roundNr] = this.convertRoundType(seriesToMoveDown);
            console.log(roundsOfSelectedSeries)
        },

        convertRoundType: function(round){
            if(round.roundType){
                switch (round.roundType) {
                    case "B":
                    default:
                        round.roundType = {type: "B", name: "Tabel"};
                        break;
                    case "R":
                        round.roundType = {type: "R", name: "Poulerounde"};
                }
            }
            return round;
        }
    }
}
]);