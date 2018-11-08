var limit = 250 * 1,
duration = 15000;
movil = 60000, // 1000 corresponde a un segundo
now = new Date(Date.now() - duration)
var fechaInicio = new Date();
fechaInicio.setHours(fechaInicio.getHours()+3);
var lowerBoundMinute = Math.trunc(fechaInicio.getTime()/(60000));


var width = 450,
    height = 220

var groups = {
	current: {
        value: 0,
        color: 'Red',
        data: d3.range(limit).map(function() {
			return 0
		})
    },
    /*target: {
		value: 0,
        color: 'green',
        data: d3.range(limit).map(function() {
			return 0
        })
    },
    output: {
		value: 0,
        color: 'grey',
        data: d3.range(limit).map(function() {
			return 0
		})
    }*/
}
 
 var x = d3.time.scale()
	.domain([now - (limit - 2), now - duration])
	.range([0, width])

var y = d3.scale.linear()
	.domain([0, 500])
    .range([height, 0])

var line = d3.svg.line()
	.interpolate('basis')
    .x(function(d, i) {
		return x(now - (limit - 1 - i) * duration)
    })
    .y(function(d) {
		return y(d)
    })

var svg = d3.select('.graph').append('svg')
	.attr('class', 'chart')
    .attr('width', width)
    .attr('height', height + 50)

var axis = svg.append('g')
	.attr('class', 'x axis')
    .attr('transform', 'translate(0,' + height + ')')
    .call(x.axis = d3.svg.axis().scale(x).orient('bottom'))

var paths = svg.append('g')

for (var name in groups) 
{
	var group = groups[name]
	group.path = paths.append('path')
    .data([group.data])
    .attr('class', name + ' group')
    .style('stroke', group.color)
}

var primeraVez = true;
var frecuencia = 0;
function tick( ) 
{
	now = new Date();
	var fechaTuit = new Date();
	fechaTuit.setHours(fechaTuit.getHours()+3);
	var instanteMinuto = Math.trunc(fechaTuit.getTime()/(60000)); //Para transformar de date a long. Luego pasar de milisecond a minute
	
	console.log( 'instante ' , now);
	var diferencia = instanteMinuto - lowerBoundMinute;
	
	// Add new values
	for (var name in groups) 
	{
		var group = groups[name]	
		//group.data.push(group.value) // Real values arrive at irregular intervals
		console.log( diferencia );
		if( diferencia > 0 || primeraVez === true )
		{	
			frecuencia = 0 + Math.random() * 400 
			group.data.push( frecuencia );
			primeraVez = false;
			lowerBoundMinute = instanteMinuto;
		}
		group.path.attr('d', line)

	}

	// Shift domain
	x.domain([now - (limit - 2) * duration, now - duration]);
	
	// Slide x-axis left
	axis.transition()
		.duration(duration)
		.ease('linear')
		.call(x.axis)
			
	// Slide paths left
		paths.attr('transform', null)
			.transition()
			.duration(duration)
			.ease('linear')
			.attr('transform', 'translate(' + x(now - (limit - 1) * duration) + ')')
			.each('end', tick)

	// Remove oldest data point from each group
	for (var name in groups) 
	{
		var group = groups[name]
		if( diferencia > 0 || primeraVez === true )
		{
			group.data.shift();
		}
	}
}



tick();

