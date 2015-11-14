angular.module("TornooiSite").config(
    function ($routeProvider, $httpProvider, jwtInterceptorProvider) {
        $routeProvider
            .when('/', {
                templateUrl: "assets/partials/startPage.html",
                controller: 'TournamentController',
                access: {
                    requiresLogin: true
                }
            })
            .when('/login', {
                templateUrl: "assets/partials/login.html",
                controller: 'loginController',
                access: {
                    requiresLogin: false
                }
            })
            .when('/playerDbOverview', {
            templateUrl: "assets/partials/playerDbOverview.html",
            controller: 'PlayerController',
            access: {
                requiresLogin: true
            }
        }).when('/mainOverview/:id', {
            templateUrl: "assets/partials/mainOverview.html",
            controller: 'MainController',
            access: {
                requiresLogin: true
            }
        }).when('/tournament/:id/series', {
            templateUrl: "assets/partials/createSeries.html",
            controller: 'SeriesController',
            access: {
                requiresLogin: true
            }
        }).when('/tournament/:id/rounds',{
            templateUrl: "assets/partials/seriesRoundSetup.html",
            controller: "seriesRoundController",
            access: {
                requiresLogin: true
            }
        }).when('/tournamentOverview/:id', {
            templateUrl: "assets/partials/tournamentSetup.html",
            controller: 'TournamentOverviewController',
            access: {
                requiresLogin: true
            }
        }).when('/:id/playerSubscription', {
            templateUrl: "assets/partials/playerSubscription.html",
            controller: 'SubscriptionController',
            access: {
                requiresLogin: true
            }
        }).when('/tournamentMenu/:id', {
            templateUrl: "assets/partials/tournamentMenu.html",
            controller: 'TournamentMenuController',
            access: {
                requiresLogin: true
            }
        }).when('/drawSeries/:id', {
            templateUrl: "assets/partials/drawPage.html",
            controller: 'DrawController',
            access: {
                requiresLogin: true
            }
        }).when('/index', {
            templateUrl: "assets/partials/startPage.html",
            controller: 'TournamentController',
            access: {
                requiresLogin: true
            }
        }).when('/newTournament', {
            templateUrl: "assets/partials/createTournament.html",
            controller: 'TournamentController',
            access: {
                requiresLogin: true
            }
        }).when('/loadTournament', {
            templateUrl: "assets/partials/tournamentList.html",
            controller: 'TournamentController',
            access: {
                requiresLogin: true
            }
        }).otherwise({
            redirectTo: '/'
        });

        jwtInterceptorProvider.tokenGetter = ['$cookieStore', function($cookieStore) {
            return $cookieStore.get('auth_token');
        }];

        $httpProvider.interceptors.push('jwtInterceptor')
    })
    .run(['$rootScope', '$location', 'authService',
        function($rootScope, $location, authService) {
            $rootScope.$on('$routeChangeStart', function(event, next) {
                if (next.access != undefined && next.access.requiresLogin) {
                    if (!authService.isAuthenticated()) {
                        event.preventDefault();
                        $location.path('/login');
                    }
                }
            })
        }]);
