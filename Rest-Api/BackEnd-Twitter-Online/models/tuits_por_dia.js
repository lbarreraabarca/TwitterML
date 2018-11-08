'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var tuits_por_diaSchema = new Schema({
    palabra: {type: String, trim: true, required: true},
    ts_day : {type: Number, trim: true, required: true},
    frecuencia: {type: Number, trim: true, required: true},
		conjuntoDato : {type: String, trim: true, required: true}
});

module.exports = mongoose.model('tuits_por_dia', tuits_por_diaSchema);
