## [MonTweet System FrontEnd] ##

Este componente de software corresponde a la capa visual para levantar este servicio se deben ejecutar los siguientes comandos. 

```
$ node index.js 
```

Además se debe modificar la ruta del servidor backEnd donde apuntará la capa visual. Para ello se debe modifcar el archivo [cargaUrlServer.js](https://github.com/lbarreraabarca/TwitterML/blob/master/Rest-Api/FrontEnd-Twitter-Online-App/App-Twitter/monTwitterOnline/monTwitterHome/js/cargaUrlServer.js), esta ruta debe corresponde a la Url publicada por el servidor  [BackEnd](https://github.com/lbarreraabarca/TwitterML/tree/master/Rest-Api/BackEnd-Twitter-Online)

```Javascript
//var urlServer="http://40a1cac3.ngrok.io/";
var urlServer="http://localhost:8080/";

```
