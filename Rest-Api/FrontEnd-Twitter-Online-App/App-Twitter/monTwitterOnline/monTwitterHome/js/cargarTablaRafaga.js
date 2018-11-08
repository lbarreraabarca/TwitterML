$('#divTablapalabrasClavesTables').onload(cargarSelect( ) );


function cargarSelect( )
{
	var hostServer = urlServer;
	var serviceRest = hostServer + "api/conjuntoDato/";
	var dataSelect = [];
	$.ajax({
		url: serviceRest,
		type: 'GET',
		async : false,
		success : function(json) {
      			dataSelect = json;
    		},
    		error : function(xhr, status) {
			alert( 'Error al cargar Tabla.');
		}
	});
	var selectConjunto = document.getElementById( "comboBoxConjunto" );
	
	if( selectConjunto.length === 0 )
	{
		for (var i = 0; i < dataSelect.length ; i++) 
		{
			var option = document.createElement("option");
			option.value = dataSelect[ i ].nombreConjunto;
			option.innerHTML = dataSelect[ i ].nombreConjunto;
			selectConjunto.appendChild( option );
		}
	}

	loadTableWords( );
}

function loadTableWords( )
{	
	var hostServer = urlServer;
	var serviceRest = hostServer + "api/rafaga/buscarData";
	var flagState = 0;
	var dataUser = "";
	var varUser = "";
	var data = [];
	var dataPost = {};
	var selectHTML = document.getElementById("comboBoxConjunto");
	var valorConjuntoDato = selectHTML.value;
	dataPost.conjuntoDato = valorConjuntoDato;
	$.ajax({
		url: serviceRest,
		type: 'POST',
		async : false,
		data: dataPost,
		type: 'POST',
		dataType: 'json',
		success : function(json) {
      			data = json;
    		},
    		error : function(xhr, status) {
			alert( 'Error al cargar Tabla.');
		}
	});

	var trCount =	document.getElementById("palabrasClavesTables").getElementsByTagName("tr").length;
	if( trCount > 1 )
	{
		var tbodyTag = document.getElementsByTagName('tbody')[0];
		tbodyTag.parentNode.removeChild(tbodyTag);
	}

	var rowsTables = '';
	var fila = 0;

	var tableRef = document.getElementById('palabrasClavesTables').getElementsByTagName('tbody')[0];

	for (var i = 0; i < data.length ; i++) 
	{	
		if ( fila === 0 )
		{
			rowsTables+= '<tr class="success" id="rowTable' + i + '" >' + 
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_minutos+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'<td>'+ data[i].promedio+'</td>' +
			'<td>'+ data[i].desviacionEstandar+'</td>' +
			'<td>'+ data[i].coeficienteVariacion+'</td>' +
			'</tr>';
		}
		else if ( fila === 1 )
		{
			rowsTables+= '<tr class="info" id="rowTable' + i + '" >' +
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_minutos+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'<td>'+ data[i].promedio+'</td>' +
			'<td>'+ data[i].desviacionEstandar+'</td>' +
			'<td>'+ data[i].coeficienteVariacion+'</td>' +
			'</tr>';
		}
		else if ( fila === 2 )
		{
			rowsTables+= '<tr class="warning" id="rowTable' + i + '" >' + 
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_minutos+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'<td>'+ data[i].promedio+'</td>' +
			'<td>'+ data[i].desviacionEstandar+'</td>' +
			'<td>'+ data[i].coeficienteVariacion+'</td>' +
			'</tr>';
		}
		else if ( fila === 3 )
		{
			rowsTables+= '<tr class="danger" id="rowTable' + i + '" >' + 
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_minutos+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'<td>'+ data[i].promedio+'</td>' +
			'<td>'+ data[i].desviacionEstandar+'</td>' +
			'<td>'+ data[i].coeficienteVariacion+'</td>' +
			'</tr>';
			fila = -1;
		}
		fila = fila + 1;
	}
	
	$("#palabrasClavesTables").append(rowsTables);	

}
