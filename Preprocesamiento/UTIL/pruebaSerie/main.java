import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/*
args[ 0 ] : datos en formato csv
args[ 1 ] : palabra para generar frecuencia
args[ 2 ] : Archivo con los datos etiquetados
*/
public class main
{
	public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {
  	if ( args.length != 2  )
    {
    	System.out.println( "Error en la cantidad de argumentos " );
   	}
    else
    {
			HashMap< Long,Integer > series = new HashMap< Long ,Integer >( );
			ArrayList < Long > series_aux = new ArrayList < Long >();
      BufferedReader br = null;
      try
      {
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
				String linea = "";
				System.out.println( "Insertando Series de Tiempo");
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( " " );
					if( data.length != 2 ) continue;
					String ts_minutos = data[ 0 ]; /* instante del tuit */
					Integer frecuencia = Integer.parseInt( data[ 1] );
					/*Procesamiento de series de tiempo*/
					insertarSerie( series, ts_minutos, frecuencia );
					series_aux.add( Long.parseLong( ts_minutos ) );
      	}
        br.close( );
				
				Long upperBoundSerie = maximoSerie( series_aux );
      	Long lowerBoundSerie = minimoSerie( series_aux );
				System.out.println( "Corrigiendo Series de Tiempo" );
				corregirSerie( series, upperBoundSerie, lowerBoundSerie );
				Long maxFrecuencia = get_tsMinutos_Max( series, upperBoundSerie, lowerBoundSerie );	
				System.out.println( get_tsMinutos_Max( series, upperBoundSerie, lowerBoundSerie ) );
				//ImprimeSerie( series, upperBoundSerie, lowerBoundSerie );
				get_tsMinutos_Coeficiente_Variacion( series, upperBoundSerie, lowerBoundSerie, Long.parseLong( args[ 1 ] ), maxFrecuencia );

     	}
      catch ( IOException e)
      {
      	// Manejo de excepciones      
      	e.printStackTrace();
      }
     
   	}

	}
	
	/* Obtiene el instante en el cual la frecuencia es máxima*/
	/* Semanticamente corresponde al limite entre la clase CRISIS y PRELUDIO*/
	public static Long get_tsMinutos_Max( HashMap< Long,Integer > series, Long upperBoundSerie,
                                                                      Long lowerBoundSerie )
	{
		Integer maxFrecuencia = 0;
		Long tsMinutosMax = Long.parseLong( "0" );
		for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ ) 
			if ( series.get( instante_minuto ) >= maxFrecuencia )
			{
				maxFrecuencia = series.get( instante_minuto );
				tsMinutosMax = instante_minuto;
				System.out.println( maxFrecuencia + " " + tsMinutosMax );
			}
		return tsMinutosMax;
	}

	/* Obtiene el instante en el cual la frecuencia es varía respecto a una ventana de tiempo */
  /* Semanticamente corresponde al limite entre la clase PRE-EPISODE y CRISIS*/
  public static Long get_tsMinutos_Coeficiente_Variacion( HashMap< Long,Integer > series, Long upperBoundSerie,
                                                                      Long lowerBoundSerie, Long sizeTimeSerie,
																																			Long maxFrecuencia )
  {
		System.out.println( "lower : " + lowerBoundSerie + " upper : " + upperBoundSerie );
    Double maxCoeficienteVariacion = 0.0; /* Variable para almacenar el CV MAX*/
    Long tsMinutosMax = Long.parseLong( "0" ); /* Variable para almacenar el instante del CV MAX*/
    for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
		{
			Long diffTimeWindow = instante_minuto - sizeTimeSerie;
			if( diffTimeWindow >= lowerBoundSerie )
			{
				/*Calcular el promedio */
				Long i = instante_minuto; /* Variable que recorre las ventanas*/
				Double sumaFrecuencias = 0.0; /* Suma las frecuencias de la ventana de tiempo */
				Double averageTimeWindow = 0.0;
				while ( i > diffTimeWindow )
				{
					sumaFrecuencias += series.get( i ).doubleValue( ) ;
					i--;
				}
				averageTimeWindow = sumaFrecuencias / sizeTimeSerie.doubleValue( );
				/*Calcular la Desviacion Estandar */
				i = instante_minuto;
				Double standarDesviationTimeWindow = 0.0;
				Double sumaDesviaciones = 0.0;
				while ( i > diffTimeWindow )
				{
					Double absolute = Math.abs( series.get( i ).doubleValue( ) - averageTimeWindow );
					Double powAbsolute = Math.pow( absolute, 2 );
					sumaDesviaciones += powAbsolute;
					i--;
				}
				standarDesviationTimeWindow = sumaDesviaciones / sizeTimeSerie.doubleValue( );
				standarDesviationTimeWindow = Math.sqrt( standarDesviationTimeWindow );
				Double coeficienteVariacionTimeWindow = standarDesviationTimeWindow / averageTimeWindow ;
				Double extremos = averageTimeWindow + ( 2 * standarDesviationTimeWindow );
				if( series.get( instante_minuto ) >= extremos && instante_minuto < maxFrecuencia ) 
				//if ( coeficienteVariacionTimeWindow >= maxCoeficienteVariacion && instante_minuto < maxFrecuencia)
				{
					maxCoeficienteVariacion = series.get( instante_minuto ).doubleValue( );
					tsMinutosMax = instante_minuto;
				}					
			}
		}
		System.out.println( tsMinutosMax + "\t" + maxCoeficienteVariacion );
    return tsMinutosMax;
  }
	


	public static void ImprimeSerie( HashMap< Long,Integer > series, Long upperBoundSerie,
                                                                      Long lowerBoundSerie )
	{
		for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
		{
			System.out.println( instante_minuto + "\t" + series.get( instante_minuto ) );
		}
	}

	public static HashMap< Long,Integer > corregirSerie( HashMap< Long,Integer > series , Long upperBoundSerie,
																																			Long lowerBoundSerie)
	{
      for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
      	if ( ! series.containsKey( instante_minuto ) )
        	series.put( instante_minuto, 0 );
		return series;
	}

	public static HashMap< Long,Integer > insertarSerie( HashMap< Long,Integer > series, String ts_minutos, Integer frecuencia )
	{
  	if ( ! series.containsKey( Long.parseLong( ts_minutos ) ) )
    {
    	Long instante = Long.parseLong( ts_minutos );
      series.put( instante, frecuencia );
    }
		else
   	{
    	series.put( Long.parseLong( ts_minutos ), series.get( Long.parseLong( ts_minutos ) ) + 1 );
    }
		return series;
	}


	public static Long maximoSerie( ArrayList < Long > series )
  {
    Long x = Long.parseLong( "0" );
    for( int i = 0 ; i < series.size( ) ; i++ )
    {
      if ( series.get( i ) > x )
				x = series.get( i );
    }
    return x;
  }

  public static Long minimoSerie( ArrayList < Long > series )
  {
    Long x = Long.parseLong( "0" );
    for( int i = 0 ; i < series.size( ) ; i++ )
    {
      if ( i == 0 ) x = series.get( i );
      if ( series.get( i ) < x )
        x = series.get( i );
    }
    return x;
  }

  	public static ArrayList<String> cargarBW( String archivoInput ) throws FileNotFoundException, IOException
    {
      ArrayList<String> BW = new ArrayList<String>();
      File finput = null;
      FileReader fr = null;
      BufferedReader br = null;

      try
      {
        finput = new File( archivoInput );
        fr = new FileReader ( finput );
        br = br = new BufferedReader(fr);

        String linea = "";

        while( ( linea = br.readLine( ) ) != null)
        {
          BW.add( linea );
        }
        
        br.close( );
        fr.close( );

      }
      catch ( IOException e)
      {
        //manejo de excepciones
      }

      return BW;
    }
}

