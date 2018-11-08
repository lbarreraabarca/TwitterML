'use strict';

var express = require('express');
var kraken = require('kraken-js');
var mongoose = require('mongoose');
//var cors = require('cors');

var options;
var app;

// Connection to DB
var promise = mongoose.connect('mongodb://localhost/DB_TUITS_ONLINE', {
    useMongoClient : true,
    poolSize : 10,
    reconnectInterval: 500,
    autoIndex : false
  });

/*var corsOptions = {
  origin: '*'
}*/

app = module.exports = express();
//app.use(cors(corsOptions));
/*app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});*/
app.use(kraken(options));
app.on('start', function () {
    console.log('Application ready to serve requests.');
    console.log('Environment: %s', app.kraken.get('env:env'));
});

