'use strict';

var express = require('express');
var kraken = require('kraken-js');
var mongoose = require('mongoose');
//var cors = require('cors');

var options;
var app;

mongoose.connect('mongodb://127.0.0.1:27017/'+'DB_TUITS_ONLINE');

app = module.exports = express();
//app.use(cors(corsOptions));
/*app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});*/
app.use(express.static(__dirname + '/view'));
app.use(kraken(options));
app.on('start', function () {
    console.log('Application ready to serve requests.');
    console.log('Environment: %s', app.kraken.get('env:env'));
});

