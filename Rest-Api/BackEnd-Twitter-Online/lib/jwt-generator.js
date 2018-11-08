'use strict';
var jwt = require('jsonwebtoken');

var JwtGenerator = function () {

    var self = this;
    //puede ser cualcquier cosa el valor de preferencia hexadecimal
    var SECRET = '8d337c7e088c11e_MY_SECRET_WORD_5a6c01697f925ec7b';

    self.generateToken = function (data) {

        var opts = {expiresIn: parseInt(60000)};
        var token = jwt.sign(data, SECRET, opts);
        return token;

    };

    self.verifyToken = function (req, callback) {

        var token = req.body.token || req.params.token || req.headers['x-access-token'];
        var opts = {expiresIn: parseInt(60000)};

        if (!token) {
            // if there is no token
            // return an error
            return callback(new Error('NO_TOKEN'));
        }

        jwt.verify(token, SECRET, opts, function (err, decoded) {
            callback(err, decoded);
        });

    };
};

module.exports = new JwtGenerator();'use strict';
