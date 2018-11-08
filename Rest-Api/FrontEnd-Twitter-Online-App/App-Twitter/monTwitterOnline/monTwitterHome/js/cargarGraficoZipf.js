$('#divTablapalabrasClavesTables').onload(cargarSelect( ) );

var dataGraficoArray ;
var palabraTitulo;

function cargarChart( )
{
	/*Procesar data*/
	var fechaHasta = document.getElementById("fFin").value;

	var fechaHastaSplit = fechaHasta.split("/");

	var dateFechaHasta;
	
	if(  fechaHastaSplit.length === 3 )
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
		dateFechaHasta.setMinutes(dateFechaHasta.getMinutes());
			
	}
	else
	{
		alert( 'Favor Ingresar el Formato correcto de las fechas : dd/mm/aaaa' );
		return;
	}

	var ts_minuto_FechaHasta = Math.trunc(dateFechaHasta.getTime()/(1000 * 60 * 60 * 24 ));

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
	
	var dataGraficoString = "[ "; //['Tiempo', 'Frecuencia']
	dataGraficoArray = null;
	palabraTitulo = "";
	if( data.length === 0 )
	{
		alert( 'No existe serie de tiempo para los parametros definidos. ');
		//deleteChart();
		return;
	}
	dataGraficoArray = new Array();
	var tmp = new Array( );
	tmp.push( "Palabra" );
	tmp.push( "Frecuencia" );	
	dataGraficoArray.push( tmp );
	for (var i = 0; i < data.length ; i++)
	{	
		var test = new Array();
		test.push( "'" + data[i].palabra + "'" );
		test.push( data[i].frecuencia );		
		dataGraficoArray.push( test );	
	}
	palabraTitulo = document.getElementById( "comboBoxConjunto" ).value;
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

}

function loadGrafico( )
{
		google.charts.load('current', {packages: ['corechart', 'bar']});
		google.charts.setOnLoadCallback(drawBasic);
}


function drawBasic() 
{
	var data = google.visualization.arrayToDataTable( dataGraficoArray );

      var options = {
        title: palabraTitulo,
        chartArea: {width: '70%'},
        hAxis: {
          title: palabraTitulo,
          minValue: 0
        },
        vAxis: {
          title: 'Palabras'
        }
      };

      var chart = new google.visualization.BarChart(document.getElementById('chart_div'));

      chart.draw(data, options);
}

