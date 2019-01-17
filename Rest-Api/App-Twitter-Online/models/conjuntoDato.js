'use strict';

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var conjuntoDatoSchema = new Schema({
    nombreConjunto: {type: String, trim: true, required: true}
});

module.exports = mongoose.model('conjuntoDato', conjuntoDatoSchema);
