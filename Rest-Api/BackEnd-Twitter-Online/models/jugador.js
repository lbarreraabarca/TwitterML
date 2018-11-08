'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var jugadorSchema = new Schema({
    nombre: {type: String, trim: true, required: true},
    apellido : {type: String, trim: true, required: true},
    username: {type: String, trim: true, required: true},
    password: {type: String, trim: true, required: true},
    edad: {type: Number, trim: true, required: true}
});

module.exports = mongoose.model('jugador', jugadorSchema);
