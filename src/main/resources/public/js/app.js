/**
 * Created by Ananasy on 15.03.2017.
 */
var app = angular.module('app', ['ui.router', 'app.controllers', 'app.directives', 'app.services']);

app.config(function ($stateProvider, $locationProvider){
    $locationProvider.hashPrefix('');
    $locationProvider.html5Mode(true);
    $stateProvider.
        state('main', {
            url: '/whalenition',
            views: {
                "view_1": {templateUrl: "./templates/main.html"}
            },
            controller: 'mainCtrl'
        })
    });