'use strict' ;

var hostServer = "http://localhost:8080/"

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
			popUpNewPage('page2.html', varUser, dataUser);
    },
    error : function(xhr, status) {
			alert( 'Username y/o password incorrectos ');
		}
	});
}

function popUpNewPage(pagina, variables, nombres) 
{
  pagina +="?";
  var nomVec = nombres.split(",");
  var metadata = variables.split(",");
  for ( var i=0; i<nomVec.length; i++ )
	{
  	console.log(nomVec[i]);
  	pagina += metadata[i] + "=" + nomVec[i] +"&";
  }
  	pagina = pagina.substring(0,pagina.length-1);
		//alert( pagina );
    location.href="page2.html";
}
