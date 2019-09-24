$('#divTablapalabrasClavesTables').onload(cargarSelect( ) );

var dataGraficoArray ;
var palabraTitulo;

function cargarChart( )
{
	/*Procesar data*/
	var fechaDesde = document.getElementById("fInicio").value;
	var fechaHasta = document.getElementById("fFin").value;

	var fechaDesdeSplit = fechaDesde.split("/");
	var fechaHastaSplit = fechaHasta.split("/");

	var dateFechaDesde, dateFechaHasta;

	if( fechaDesdeSplit.length === 3 && fechaHastaSplit.length === 3 )
	{
		var errores = "";
		var diaDesde  = fechaDesdeSplit[ 0 ];
		if ( diaDesde < 1 && diaDesde > 31 )
		{
			errores = errores + "Dia fecha desde fuera de rango [1-31]. \n";
		}
		var mesDesde  = fechaDesdeSplit[ 1 ] - 1;
		if ( mesDesde < 1 &&  mesDesde > 12 )
		{
			errores = errores + "Mes fecha desde fuera de rango [1-12]. \n";
		}
		var anioDesde = fechaDesdeSplit[ 2 ];
		if ( anioDesde.length != 4 )
		{
			errores = errores + "Formato del anio desde ser: 2006.\n";
		}

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

		dateFechaDesde = new Date(anioDesde,mesDesde,diaDesde);
		//dateFechaDesde.setHours(dateFechaDesde.getHours()+23);
		//dateFechaDesde.setMinutes(dateFechaDesde.getMinutes()+59);

		dateFechaHasta = new Date(anioHasta,mesHasta,diaHasta);
		dateFechaHasta.setHours(dateFechaHasta.getHours()+23);
		dateFechaHasta.setMinutes(dateFechaHasta.getMinutes()+59);

	}
	else
	{
		alert( 'Favor Ingresar el Formato correcto de las fechas : dd/mm/aaaa' );
		return;
	}

	var ts_minuto_FechaDesde = Math.trunc(dateFechaDesde.getTime()/(60000));
	var ts_minuto_FechaHasta = Math.trunc(dateFechaHasta.getTime()/(60000));

	if ( ts_minuto_FechaDesde >= ts_minuto_FechaHasta )
	{
		alert( 'Fecha desde mayor o igual que fecha Hasta' );
		return;
	}

	var hostServer = urlServer;
	var serviceRest = hostServer + "api/serieTiempo/buscarData";
	var flagState = 0;
	var dataUser = "";
	var varUser = "";
	var data = [];
	var dataPost = {};
	var selectHTML = document.getElementById("comboBoxConjunto");
	var valorConjuntoDato = selectHTML.value;
	dataPost.conjuntoDato = valorConjuntoDato;
	dataPost.desde = ts_minuto_FechaDesde;
	dataPost.hasta = ts_minuto_FechaHasta;
	dataPost.palabra = document.getElementById( "palabraBuscar" ).value;
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

	var dataGraficoString = "[ "; //['Tiempo', 'Frecuencia']
	dataGraficoArray = null;
	palabraTitulo = "";
	if( data.length === 0 )
	{
		alert( 'No existe serie de tiempo para los parametros definidos. ');
		//deleteChart();
		return;
	}
	for (var i = 0; i < data.length ; i++)
	{
		dataGraficoString = dataGraficoString + "[ " + data[i].ts_minutos + ",";
		dataGraficoString = dataGraficoString + " " + data[i].frecuencia + " ], ";

	}
	dataGraficoString = dataGraficoString.substr( 0 , dataGraficoString.length - 2 );
	dataGraficoString = dataGraficoString +  "]";
	dataGraficoArray  = JSON.parse( dataGraficoString );
	palabraTitulo = document.getElementById( "palabraBuscar" ).value;
	loadGrafico( );
}
// fecha ts_minutos : 25250579 para atras
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
	var textDesde = document.getElementById( "fInicio" ).value;
	if( textDesde.length === 0 )
	{
		document.getElementById( "fInicio" ).value = "01/01/2006";
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

	var keywordText = document.getElementById( "palabraBuscar" ).value;
	if( keywordText.length === 0 )
	{
		document.getElementById( "palabraBuscar" ).value = document.getElementById("comboBoxConjunto").value;
	}
}

function loadGrafico( )
{
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);
}

function deleteChart(){
    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
    chart.draw({}, {});
}


function drawChart( ) {

	var titulo = ['Tiempo', 'Frecuencia'];
	dataGraficoArray.unshift( titulo );
	var data = google.visualization.arrayToDataTable( dataGraficoArray );

        var options = {
          title: palabraTitulo,
          curveType: 'function',
          legend: { position: 'bottom' },
	  colors: [ 'red', 'blue' ]
        };

        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

        chart.draw(data, options);
}
