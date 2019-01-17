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

	var textHasta = document.getElementById( "fFin" ).value;
	if( textHasta.length === 0 )
	{
		var fechaActual = new Date();
		var dia = fechaActual.getDate();
		var mes = fechaActual.getMonth()+1;
		var anio = fechaActual.getFullYear();
		if ( dia < 10 ) { dia = "0" + dia; }
		if ( mes < 10 ) { mes = "0" + mes; } 
		document.getElementById( "fFin" ).value = dia + "/" + mes + "/" + anio;
	}
	//loadTableWords( );
}


function loadTableWords( )
{	

	/*Procesar data*/
	var fechaHasta = document.getElementById("fFin").value;

	var fechaHastaSplit = fechaHasta.split("/");

	var dateFechaDesde, dateFechaHasta;
	
	if( fechaHastaSplit.length === 3 )
	{
		var errores = "";
		
		var diaHasta  = fechaHastaSplit[ 0 ];
		if ( diaHasta < 1 && diaHasta > 31 )
		{
			errores = errores + "Dia fecha hasta fuera de rango [1-31]. \n";
		}
		var mesHasta  = fechaHastaSplit[ 1 ] - 1;
		if ( mesHasta < 1 &&  mesHasta > 12 )
    		{
      			errores = errores + "Mes fecha hasta fuera de rango [1-12]. \n";
    		}
		var anioHasta = fechaHastaSplit[ 2 ];
		if ( anioHasta.length != 4 )
    		{
      			errores = errores + "Formato del anio hasta ser: 2006.\n";
    		}

		if ( errores.length > 0 )
		{
			alert( errores );
			return;
		}
		
		dateFechaHasta = new Date(anioHasta,mesHasta,diaHasta);
		dateFechaHasta.setHours(dateFechaHasta.getHours());
		dateFechaHasta.setMinutes(dateFechaHasta.getMinutes()+0);
			
	}
	else
	{
		alert( 'Favor Ingresar el Formato correcto de las fechas : dd/mm/aaaa' );
		return;
	}
	
	var ts_minuto_FechaHasta = Math.trunc(dateFechaHasta.getTime()/(1000 * 60 * 60 * 24));
	var hostServer = urlServer;
	var serviceRest = hostServer + "api/tuits_por_dia/buscarData";
	var flagState = 0;
	var dataUser = "";
	var varUser = "";
	var data = [];
	var dataPost = {};
	var selectHTML = document.getElementById("comboBoxConjunto");
	var valorConjuntoDato = selectHTML.value;
	dataPost.conjuntoDato = valorConjuntoDato;
	dataPost.ts_day = ts_minuto_FechaHasta;
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
			'<td>'+ data[i].ts_day+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'</tr>';
		}
		else if ( fila === 1 )
		{
			rowsTables+= '<tr class="info" id="rowTable' + i + '" >' +
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_day+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'</tr>';
		}
		else if ( fila === 2 )
		{
			rowsTables+= '<tr class="warning" id="rowTable' + i + '" >' + 
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_day+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'</tr>';
		}
		else if ( fila === 3 )
		{
			rowsTables+= '<tr class="danger" id="rowTable' + i + '" >' + 
			'<td>'+ data[i].palabra+'</td>' + 
			'<td>'+ data[i].ts_day+'</td>' +
			'<td>'+ data[i].frecuencia+'</td>' +
			'</tr>';
			fila = -1;
		}
		fila = fila + 1;
	}
	
	$("#palabrasClavesTables").append(rowsTables);	

}
