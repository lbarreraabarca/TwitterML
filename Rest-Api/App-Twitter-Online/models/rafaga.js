'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var rafagaSchema = new Schema({
    palabra: {type: String, trim: true, required: true},
    ts_minutos : {type: String, trim: true, required: true},
    frecuencia: {type: Number, trim: true, required: true},
    promedio: {type: Number, trim: true, required: true},
    desviacionEstandar: {type: Number, trim: true, required: true},
		coeficienteVariacion : {type: Number, trim: true, required: true},
		conjuntoDato : {type: String, trim: true, required: true}
});

module.exports = mongoose.model('rafaga', rafagaSchema);
