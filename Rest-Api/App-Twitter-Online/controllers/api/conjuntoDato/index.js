'use strict';

var ConjuntoDatoModel = require('../../../models/conjuntoDato');

module.exports = function (router) {
    
     router.use(function (req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        next();
    });

    router.post('/buscar', function (req, res) {
        var data = req.body;
        ConjuntoDatoModel.findOne({nombreConjunto: data.nombreConjunto})
        .populate('user')
        .exec(function (err, person) {
            if (err) {
                return res.status(500).json({error: err}).end();
            }
            if (!person) {
                return res.status(404).end();
            }
            res.status(200).json(person).end();
        });

    });

    router.get('/', function (req, res) {

        ConjuntoDatoModel.find()
        .populate('user')
        .exec(function (err, person) {
            if (err) {
                return res.status(500).json({error: err}).end();
            }
            if (!person) {
                return res.status(404).end();
            }
            res.status(200).json(person).end();
        });

    });

    router.post('/', function (req, res) {
        var data = req.body;
        var newJugador = new ConjuntoDatoModel(data);
        newJugador.save(function (err, personCreated) {
            if (err) {
                return res.status(500).json({error: err}).end();
            }
            res.status(201).json(personCreated).end();
        });

    });

    router.put('/:id', function (req, res) {
        var data = req.body;
        var jugadorId = req.params.id;
        ConjuntoDatoModel.findOne({_id: jugadorId}, function (err, jugadorToUpdate) {
            if (err) {
                return res.status(500).json({error: err}).end();
            }

            mapPersonDataToUpdate(jugadorToUpdate, data);
            jugadorToUpdate.save(function (err, jugadorToUpdate) {
                if (err) {
                    return res.status(500).json({error: err}).end();
                }
                res.status(201).json(jugadorToUpdate).end();
            });
        });

    });

    router.delete('/:id', function (req, res) {

        var jugadorId = req.params.id;
				
        ConjuntoDatoModel.remove({_id: jugadorId}, function (err) {
            if (err) {
                res.status(500).json({error: err}).end();
            }
						var statusOK = '{"status" : "delete ok"}';
            res.status(204).json(statusOK).end();
        });

    });

    function mapPersonDataToUpdate(jugadorToUpdate, data) {

        jugadorToUpdate.palabra = data.palabra || jugadorToUpdate.palabra;
        jugadorToUpdate.ts_minutos = data.ts_minutos || jugadorToUpdate.ts_minutos;
        jugadorToUpdate.frecuencia = data.frecuencia || jugadorToUpdate.frecuencia;
        jugadorToUpdate.promedio = data.promedio || jugadorToUpdate.promedio;
				jugadorToUpdate.desviacionEstandar = data.desviacionEstandar || jugadorToUpdate.desviacionEstandar;
				jugadorToUpdate.coeficienteVariacion = data.coeficienteVariacion || jugadorToUpdate.coeficienteVariacion;
        return jugadorToUpdate;

    };

};
