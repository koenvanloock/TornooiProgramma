angular.module("tornooiServices").factory("TournamentService", ["$http", "baseUrl",
    function ($http, baseUrl) {
        var currentTournament = {
            "tournament": {id: 0,
                tournamentName: null,
                tournamentDate: {"day": null, "month": null, "year":null },
                hasMultipleSeries: false,
                showClub: false,
                maximumNumberOfSeriesEntries: 0},
            "series": []
        };

        return {
            getTournaments: function () {
                return $http.get(baseUrl.url + '/tournaments');
            },

            addTournament: function (name, date, hasMultiple, maxEntries, showClub) {
                return $http.post(baseUrl.url + '/tournament',
                    {
                        'tournamentName': name,
                        'tournamentDate': date,
                        'hasMultipleSeries': hasMultiple,
                        'maximumNumberOfSeriesEntries': parseInt(maxEntries),
                        'showClub': showClub
                    })
            },
            updateTournament: function (id, name, date) {
                return $http.put(baseUrl.url + '/tournament'+ id, {'tournamentId': id, 'name': tournamentName, 'tournamentDate': date})
            },

            getTournament: function (id) {
                return $http.get(baseUrl.url + '/tournament/' + id);
            },

            setCurrentTournament: function (tournament) {
                currentTournament = tournament;
            },
            getCurrentTournament: function () {
                return currentTournament;
            }
        }
    }]);