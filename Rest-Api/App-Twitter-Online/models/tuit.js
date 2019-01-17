'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var tuitSchema = new Schema({
    idTuit: {type: String, trim: true, required: true},
    ts_minutos : {type: Number, trim: true, required: true},
    hashtags: {type: String, trim: true, required: true},
    userMentions: {type: String, trim: true, required: true},
    timeZone: {type: String, trim: true, required: true},
		followersCount : {type: Number, trim: true, required: true},
		retweetCount : {type: Number, trim: true, required: true},
		text : {type: String, trim: true, required: true},
		horaUTC : {type: String, trim: true, required: true},
		etiqueta : {type: String, trim: true},
		conjuntoDato : {type: String, trim: true},
});

module.exports = mongoose.model('tuit', tuitSchema);
