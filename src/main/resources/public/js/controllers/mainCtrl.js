/**
 * Created by Ananasy on 15.03.2017.
 */
angular.module('app.controllers', ['app.services'])
    .controller('mainCtrl', function($scope){
        $scope.answer = {
            mlp: "",
            cnn: "",
            cnn_exp: "",
            cnn_stn: ""
        }
    });