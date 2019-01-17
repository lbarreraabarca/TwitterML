'use strict';
var jwtGenerator = require('../../../../lib/jwt-generator');
var auth = require('../../../../lib/auth');
var UserModel = require('../../../../models/jugador');

module.exports = function (router) {
    
    router.use(function (req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        next();
    });

    router.post('/', auth.isAuthenticated(),function (req, res) {
        
        var data = req.body;
        var accessToken = data.token;
        var vtoken = accessToken;

        return res.status(200).json({result: 'Todo OK!', token: vtoken}).end();

    });
};
