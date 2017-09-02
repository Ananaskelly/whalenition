/**
 * Created by Ananasy on 16.03.2017.
 */
angular.module('app.services', [])
    .service('content', function($q, $http){
        return {
            send: function (image) {
                console.log("hello");
                var postPromise = $q.defer();
                var file = new FormData();
                file.append('file', image);
                $http({
                    method: 'POST',
                    url: '/recognize',
                    data: file,
                    headers: {
                        'Content-Type': undefined
                    },
                    transformRequest: angular.identity
                }).then((function (response) {
                        return postPromise.resolve(response)
                    }), (function (error) {
                        return postPromise.reject(error);
                    })
                );

                return postPromise.promise;
            }
        }
    });