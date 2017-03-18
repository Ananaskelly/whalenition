/**
 * Created by Ananasy on 15.03.2017.
 */
angular.module('app.directives', ['app.services'])
    .directive('painter', function($timeout, content){
        return {
            restrict: "A",
            link: function(scope, element) {
                var canvas = new fabric.Canvas('canvas');
                canvas.isDrawingMode = true;
                canvas.backgroundColor = 'white';
                canvas.freeDrawingBrush.width = 10;
                angular.element(document).ready( function(){
                    var height = $('#canvas_wrap')[0].offsetHeight * 0.98;
                    var width = $('#canvas_wrap')[0].offsetWidth * 0.98;
                    canvas.setHeight(height);
                    canvas.setWidth(width);
                    canvas.renderAll();
                });
                $( window ).resize(function(){
                    console.log("hello");
                    var height = $('#canvas_wrap')[0].offsetHeight * 0.98;
                    var width = $('#canvas_wrap')[0].offsetWidth * 0.98;
                    canvas.setHeight(height);
                    canvas.setWidth(width);
                    canvas.renderAll();
                });

                scope.clear = function(){
                   canvas.clear();
                   canvas.backgroundColor = 'white';
                };

                scope.recognize = function(){
                    var file = canvas.toDataURL();
                    var blob = dataURItoBlob(file);
                    content.send(blob).then(function(response){
                        scope.answer.mlp = response.data;
                    });
                };

                function dataURItoBlob(dataURI) {
                    // convert base64/URLEncoded data component to raw binary data held in a string
                    var byteString;
                    if (dataURI.split(',')[0].indexOf('base64') >= 0)
                        byteString = atob(dataURI.split(',')[1]);
                    else
                        byteString = unescape(dataURI.split(',')[1]);

                    // separate out the mime component
                    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

                    // write the bytes of the string to a typed array
                    var ia = new Uint8Array(byteString.length);
                    for (var i = 0; i < byteString.length; i++) {
                        ia[i] = byteString.charCodeAt(i);
                    }

                    return new Blob([ia], {type:mimeString});
                }
            }
        }
    });