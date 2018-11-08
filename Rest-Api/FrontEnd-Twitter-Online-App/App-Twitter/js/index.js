'use strict' ;

var hostServer;

function validarCampos( )
{
	var username = document.getElementById("usernameLogin").value;
	var password = document.getElementById("passwordLogin").value;
	if ( username.length === 0  || password.length === 0 )
	{
		alert( "Favor ingresar username y/o password" );
	}
	else
	{
		serviceQueryUser( username, password );
	}
}


function serviceQueryUser( username, password )
{	
	hostServer = urlServer;
	var dataPost = {};
	dataPost.username = username;
	dataPost.password = password;
	var serviceRest = hostServer + "api/loginJugador/";
	var flagState = 0;
	var dataUser = "";
	var varUser = "";
	$.ajax({
		url: serviceRest,
		data: dataPost,
		type: 'POST',
		dataType: 'json',
		async : false,
		success : function(json) {
      dataUser = json.token +','+json.nombre + ',' + json.apellido +','+ json.edad+','+ username;
      varUser = "token,nombre,apellido,edad,username";
			popUpNewPage('monTwitterOnline/monTwitterHome/pages/index.html', varUser, dataUser);
    },
    error : function(xhr, status) {
			alert( 'Username y/o password incorrectos ');
		}
	});
}

function popUpNewPage(pagina, variables, nombres) 
{
	//window.top.location.href = pagina;
	window.close(  );
	window.open("monTwitterOnline/monTwitterHome/pages/index.html"); 
}
