'use strict';
var jwt = require('./jwt-generator');

var Auth = function () {

    var self = this;

    self.isAuthenticated = function (req, res, next) {

        return function (req, res, next) {

            jwt.verifyToken(req, function (err, decoded) {
                if (err) {
                    return res.status(403).json({error: err.message}).end();
                }
                if (decoded) {
                    next();
                }
            });

        };

    };

};

module.exports = new Auth();
