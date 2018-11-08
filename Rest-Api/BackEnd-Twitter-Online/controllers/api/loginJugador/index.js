'use strict';
var jwtGenerator = require('../../../lib/jwt-generator');
var UserModel = require('../../../models/jugador');

module.exports = function (router) {
		
		router.use(function(req, res, next) {
  		res.header("Access-Control-Allow-Origin", "*");
  		res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  		next();
		});
	
    router.post('/', function (req, res) {

        var data = req.body;
        var username = data.username;
        var password = data.password;

        //Validate mandatory data
        if (!username || !password) {
            return res.status(400).end();
        }

        UserModel.findOne({
            username: username,
            password: password
        }).exec(function (err, user) {
            if (err) {
                return res.status(500).json({err: err.message}).end();
            }

            if (!user) {
                return res.status(401).json({err: 'Usuario y/o contrasena invalidos'}).end();
            }

            var dataForPayload = {
                username: username,
                nombre: user.nombre,
                apellido: user.apellido
            };
            
            var nombre = user.nombre;
            var apellido = user.apellido;
            var edad = user.edad;

            var token = jwtGenerator.generateToken(dataForPayload);
            
            return res.status(201).json({token: token, nombre: nombre, apellido: apellido, edad: edad, username: username}).end();

        });

    });
		
		router.get('/', function (req, res) {

        UserModel.find()
        .populate('user')
        .exec(function (err, person) {
            if (err) {
                return res.status(500).json({error: err}).end();
            }
            if (!person) {
                return res.status(404).end();
            }
            res.status(200).end();
        });

    });		

};
