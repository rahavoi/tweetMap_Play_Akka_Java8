var app = angular.module('tweetMapApp',  ["leaflet-directive"]);

app.factory('Twitter', function($http, $timeout) {

    var ws = new WebSocket("ws://localhost:9000/ws");

    var twitterService = {
        tweets: [],
        query: function (query) {
            ws.send(JSON.stringify({query: query}));
        }
    };

    ws.onmessage = function(event) {
        $timeout(function() {
            twitterService.tweets = JSON.parse(event.data).statuses;
        });
    };

    return twitterService;
});

app.controller('Search', function($scope, $http, $timeout, Twitter) {
    $scope.tweets = [];
    $scope.markers = [];

    $scope.$watch(
        function() {
            return Twitter.tweets;
        },
        function(tweets) {
            $scope.tweets = tweets;

            $scope.markers = tweets.map(function(tweet) {
                return {
                    lng: tweet.coordinates[0],
                    lat: tweet.coordinates[1],
                    message: tweet.text,
                    focus: true
                }
            });
        }
    );


    $scope.search = function() {
        Twitter.query($scope.query);
    };

});

app.controller('Tweets', function($scope, $http, $timeout, Twitter) {

    $scope.tweets = [];

    $scope.$watch(
        function() {
            return Twitter.tweets;
        },
        function(tweets) {
            $scope.tweets = tweets;
        }
    );

});

