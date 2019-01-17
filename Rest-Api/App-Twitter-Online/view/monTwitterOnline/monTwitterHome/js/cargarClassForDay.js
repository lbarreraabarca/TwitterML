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
	var serviceRest = hostServer + "api/ml_tuit/sumarDia";
	var flagState = 0;
	var dataUser = "";
	var varUser = "";
	var data = [];
	var dataPost = {};
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
	
	var dataGraficoString = "[ ";
	dataGraficoArray = null;
	palabraTitulo = "";
	if( data.length === 0 )
	{
		alert( 'No existe datos para los parametros definidos. ');
		//deleteChart();
		return;
	}
	dataGraficoArray = new Array();
	var tmp = new Array( );
	tmp.push( "Conjunto Dato" );
	tmp.push( "Cantidad de Tuits" );	
	dataGraficoArray.push( tmp );
	for (var i = 0; i < data.length ; i++)
	{	
		var test = new Array();
		test.push( "'" + data[i]._id + "'" );
		test.push( data[i].total );		
		dataGraficoArray.push( test );
	}
	palabraTitulo = "Tuits Clasificados para el " + document.getElementById("fFin").value;
	loadGrafico( );
}
// fecha ts_minutos : 25250579 para atras
function cargarSelect( )
{
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
	google.charts.load("current", {packages:["corechart"]});
      	google.charts.setOnLoadCallback(drawChart);
}

function drawChart() 
{
        var data = google.visualization.arrayToDataTable(dataGraficoArray);

        var options = {
          title: palabraTitulo,
          pieHole: 0.3,
        };

        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
        chart.draw(data, options);
}
