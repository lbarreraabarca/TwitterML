//$('#cantidad_tuits_tag').onload(cargarCantidadTuits( ) ); /* Funciona correctamente...*/
//$('#cantidad_rafagas_tag').onload(cargarCantidadRafagas( ) ); /* No funciona*/

$('#panelPrincipalDatos').onload(loadAllData( ) );

function loadAllData( )
{	
	/* Mostrar la cantidad de tuits*/
	var cantidadTuits = Math.floor((Math.random() * 2000) + 1);
	/* Se debe llamar al servicio que trae los datos...*/
	$('#cantidad_tuits_tag').html( cantidadTuits );
	/*Mostrar la cantidad de rafagas*/
	var cantidadRafagas = Math.floor((Math.random() * 200) + 1);
	/* Se debe llamar al servicio que trae los datos...*/
	$('#cantidad_rafagas_tag').html( cantidadRafagas );	
	var cantidadCrisis = Math.floor((Math.random() * 40) + 1);
	/* Se debe llamar al servicio que trae los datos...*/
	$('#crisis_detectadas').html( cantidadCrisis );

}

function cargarCantidadTuits()
{	
	var cantidadTuits = Math.floor((Math.random() * 50000) + 1);
	/* Se debe llamar al servicio que trae los datos...*/
	$('#cantidad_tuits_tag').html( cantidadTuits );
}

function cargarCantidadRafagas()
{	
	var cantidadRafagas = Math.floor(Math.random() * 20  + 1 );
	/* Se debe llamar al servicio que trae los datos...*/
	$('#cantidad_rafagas_tag').html( cantidadRafagas );
}