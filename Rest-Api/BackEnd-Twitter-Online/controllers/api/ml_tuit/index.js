'use strict';

var PersonModel = require('../../../models/ml_tuit');

module.exports = function (router) {
    
     router.use(function (req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        next();
    });

    router.get('/:id', function (req, res) {
        var jugadorId = req.params.id;
        PersonModel.findOne({_id: jugadorId})
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

        PersonModel.find()
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
		
		router.post('/buscarData', function (req, res) {

				var data = req.body;
        PersonModel.find({conjuntoDato: data.conjuntoDato, ts_minutos:{$gte:data.desde, $lte:data.hasta}})
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

		router.post('/sumarDia', function (req, res) {

        var data = req.body;
        PersonModel.aggregate([ { $match : { ts_day : Number(data.ts_day) } } , { $group : { _id : "$etiqueta", total : { $sum : 1 } } } ])
        //.populate('user')
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
        var newJugador = new PersonModel(data);
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
        PersonModel.findOne({_id: jugadorId}, function (err, jugadorToUpdate) {
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
				
        PersonModel.remove({_id: jugadorId}, function (err) {
            if (err) {
                res.status(500).json({error: err}).end();
            }
						var statusOK = '{"status" : "delete ok"}';
            res.status(204).json(statusOK).end();
        });

    });

    function mapPersonDataToUpdate(jugadorToUpdate, data) {

        jugadorToUpdate.idTuit = data.idTuit || jugadorToUpdate.idTuit;
        jugadorToUpdate.ts_minutos = data.ts_minutos || jugadorToUpdate.ts_minutos;
        jugadorToUpdate.hashtags = data.hashtags || jugadorToUpdate.hashtags;
        jugadorToUpdate.userMentions = data.userMentions || jugadorToUpdate.userMentions;
				jugadorToUpdate.timeZone = data.timeZone || jugadorToUpdate.timeZone;
				jugadorToUpdate.followersCount = data.followersCount || jugadorToUpdate.followersCount;
				jugadorToUpdate.retweetCount = data.retweetCount || jugadorToUpdate.retweetCount;
				jugadorToUpdate.text = data.text || jugadorToUpdate.text;
				jugadorToUpdate.horaUTC = data.horaUTC || jugadorToUpdate.horaUTC;
				jugadorToUpdate.etiqueta = data.etiqueta || jugadorToUpdate.etiqueta;
        return jugadorToUpdate;

    };

};
