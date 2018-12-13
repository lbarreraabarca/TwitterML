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
args[ 3 ] : Inicio CRISIS
args[ 4 ] : Largo Ventana Tiempo
*/
public class main
{
	public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {
  	if ( args.length != 7 )
    {
    	System.out.println( "[ EtiquetadoAutomatico ][ Error en la cantidad de argumentos ]" );
   	}
    else
    {
			HashMap< String,Map< Long,Integer > > series = new HashMap< String, Map< Long ,Integer> >( ); /*Almacena la series de tiempo de la palabra terremoto*/
			HashMap< String, Map< Long, String > > dataBase = new HashMap< String, Map< Long, String> >( ); /*Almacena el tuit y su metadata.*/
			ArrayList < Long > instantes_ts_minuto = new ArrayList < Long >( );
      ArrayList<String>  palabras_Busqueda  = cargarBW( args[ 1 ] );
			Long initCrisis = Long.parseLong( args[ 3 ] );
			HashMap < Long, Integer > frecuenciaPalabra = new HashMap< Long, Integer >( );
			Long timeWindow = Long.parseLong( args[ 4 ] );
      BufferedReader br = null;
      PrintWriter pw = null;
      try
      {
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
        pw = new PrintWriter( new FileWriter( args[ 2 ]) );
				String linea = "";
				System.out.println( "[ EtiquetadoAutomatico ][ Load Data ]" );
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( "\t" );
					//System.out.println( " length  : " + data.length );
					if( data.length != 10 ) continue;
					String id_tuit = data[ 0 ]; /*id del tuit*/
					String ts_minutos = data[ 1 ]; /* instante del tuit*/
					String hashtags = data[ 2 ]; /* conjunto de hashtags del tuit*/
					String user_mentions = data[ 3 ]; /* conjunto de usuarios mencionados */
					String location = data[ 4 ]; /* Ubicacion proporcionada por el usuario EJ: Vivo en la luna*/
					String time_zone = data[ 5 ]; /* time_zone del tuit */
					String followers_count = data[ 6 ]; /* cantidad de seguidores*/
					String retweet_count = data[ 7 ]; /* cantidad de retuits*/
					String text = data[ 8 ]; /* cuerpo del tuit */
					String horaUTC = data[ 9 ]; /* hora del tuit segun el meridiano de greenwich*/

					/*Procesamiento de series de tiempo*/
					instantes_ts_minuto.add( Long.parseLong( ts_minutos ) );
					insertarSerie( text, series, palabras_Busqueda, ts_minutos );
					insertaDataBase( dataBase, id_tuit, Long.parseLong( ts_minutos ), id_tuit + "\t" + ts_minutos + "\t" + hashtags + "\t" + user_mentions + "\t" + location + "\t" + time_zone + "\t" + followers_count + "\t" + retweet_count + "\t" + text + "\t" + horaUTC );

					InsertarInstanteFrecuencia( palabras_Busqueda, frecuenciaPalabra, Long.parseLong( ts_minutos ), text );
      	}
        br.close( );

				Long upperBoundSerie = maximoSerie( instantes_ts_minuto );
      	Long lowerBoundSerie = minimoSerie( instantes_ts_minuto );
				//System.out.println( lowerBoundSerie + "\t" + upperBoundSerie );
				System.out.println( "[ EtiquetadoAutomatico ][ Corrigiendo Series de Tiempo ]" );
				corregirSerie( series, upperBoundSerie, lowerBoundSerie );
				Long FinCrisis = frecuenciaMaxima( series, initCrisis, lowerBoundSerie, upperBoundSerie );

				/*OBTENER TENDENCIA ABSOLUTA*/
				Long sizeTW = Long.parseLong( "20" ); /* Tamano de las ventanas de tiempo. */
				HashMap < Long, ArrayList < Double  > > analisisTendencia = analisisHora( series, lowerBoundSerie, upperBoundSerie, sizeTW, args[ 5 ], args[ 6 ] );
				ArrayList < Double > estadisticaTendencia = get_MediaStd( analisisTendencia );
				double avgTendencia   = estadisticaTendencia.get( 0 );
				double stdTendencia   = estadisticaTendencia.get( 1 );
				double umbralInferior = avgTendencia - ( stdTendencia );
				double umbralSuperior = avgTendencia + ( stdTendencia );
				//System.out.println(  avgTendencia + " " + stdTendencia  + " " + umbralInferior + " " + umbralSuperior );
				//System.out.println( "Asignando etiqueta..." );
				//AsignarEtiqueta( dataBase, lowerBoundSerie, initCrisis, FinCrisis, upperBoundSerie, pw );
				System.out.println( "[ EtiquetadoAutomatico ][ Asignar Etiqueta ]" );
				AsignarEtiqueta( dataBase, lowerBoundSerie, initCrisis, FinCrisis, upperBoundSerie, pw, frecuenciaPalabra, timeWindow, palabras_Busqueda,
												analisisTendencia, avgTendencia, stdTendencia );

				pw.close( );
     	}
      catch ( Exception e)
      {
      	System.out.println( "[ EtiquetadoAutomatico ][ Exception ]" );
      	e.printStackTrace();
      }

   	}

	}

	public static ArrayList < Double > get_MediaStd( HashMap < Long, ArrayList < Double  > > analisisTendencia )
	{
		ArrayList < Double > data = new ArrayList< Double >( );
		Iterator it_2 = analisisTendencia.entrySet( ).iterator( );
		double media = 0.0;
		double std   = 0.0;
    while( it_2.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it_2.next( );
      Long ts_minutos = ( Long ) pair.getKey( );
      ArrayList< Double > media_std_tendencia = analisisTendencia.get( ts_minutos );
			media += media_std_tendencia.get( 0 );
		}
		media = media / analisisTendencia.size( );

		Iterator it = analisisTendencia.entrySet( ).iterator( );
		while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
      Long ts_minutos = ( Long ) pair.getKey( );
      ArrayList< Double > media_std_tendencia = analisisTendencia.get( ts_minutos );
      std += Math.pow( media_std_tendencia.get( 0 ) - media, 2 );
    }
		std = std / analisisTendencia.size( );
		data.add( media );
		data.add( std   );
		return data;
	}

	public static HashMap < Long, ArrayList < Double  > > analisisHora( 	HashMap < String, Map < Long, Integer > > series,
																																				Long lowerBoundSerie, Long upperBoundSerie, Long sizeTW,
																																				String pathVectores, String pathModelo )
	{
		HashMap < Long, ArrayList < Double  > > analisisTendencia = new HashMap < Long, ArrayList < Double > >( );
		try
		{
			Iterator it_2 = series.entrySet( ).iterator( );
    	while( it_2.hasNext( ) )
    	{
      	Map.Entry pair = ( Map.Entry ) it_2.next( );
     	 	String word = ( String ) pair.getKey( );
      	Map<Long, Integer > instante_frecuencia = series.get( word );
				Integer contador = 1;
				while ( lowerBoundSerie <= upperBoundSerie)
				{
					PrintWriter pwVectores = new PrintWriter( new FileWriter( new File( pathVectores + String.valueOf( contador ) + ".xls" ) ) );
					PrintWriter pwModelo   = new PrintWriter( new FileWriter( new File( pathModelo + String.valueOf( contador ) + ".xls" ) ) );
					Long upperBoundMovil = lowerBoundSerie;
					double N      = sizeTW;
					if( ( lowerBoundSerie + sizeTW -1 ) > upperBoundSerie )
					{
						upperBoundMovil = upperBoundSerie;
						N = ( upperBoundSerie - lowerBoundSerie ) + 1;
					}
					else
						upperBoundMovil =  ( lowerBoundSerie + sizeTW -1 );
     			ArrayList < Double > sumador_X = sumX( ( int ) N );
      		double sum_X  = sumador_X.get( 0 ); /*Sumatoria de X*/
      		double sum_X2 = sumador_X.get( 1 ); /*Sumatoria de X cuadrado*/
      		ArrayList < Double > sumador_Y = sumY( series, lowerBoundSerie, upperBoundMovil );
      		double sum_Y  = sumador_Y.get( 0 ); /* Sumatoria de Y (frecuencias)*/
      		double sum_Y2 = sumador_Y.get( 1 ); /* Sumatoria de Y cuadrado (frecuencias cuadradas)*/
      		double sum_XY = sumador_Y.get( 2 ); /* Sumatoria del producto X*Y */

					double m_lineal = calcular_m( sum_X, sum_X2, sum_Y, sum_Y2, sum_XY, N );
      		double n_lineal = calcular_n( sum_X, sum_X2, sum_Y, sum_Y2, sum_XY, N );

					m_lineal = redondear( m_lineal, 4 );
					n_lineal = redondear( n_lineal, 4 );

      		pwModelo.println( "sum_X  = " + sum_X  );
      		pwModelo.println( "sum_Y  = " + sum_Y  );
      		pwModelo.println( "sum_X2 = " + sum_X2 );
      		pwModelo.println( "sum_Y2 = " + sum_Y2 );
      		pwModelo.println( "sum_XY = " + sum_XY );
      		pwModelo.println( "N      = " + N );
      		pwModelo.println( "Y = " + m_lineal + "X + " + n_lineal );
					//System.out.println( "Y = " + m_lineal + "X + " + n_lineal );
					pwModelo.println( "m = " + m_lineal );
					pwModelo.println( "n = " + n_lineal );

					/* Almacenar pendiente y punto de corte para cada minuto. */
					for ( Long instanteMinuto = lowerBoundSerie; instanteMinuto <= ( lowerBoundSerie + sizeTW - 1 ) ; instanteMinuto++  )
					{
					 	//System.out.println( instanteMinuto + " " + m_lineal + " " + n_lineal );
						ArrayList < Double > estadisticos = new ArrayList < Double >( );
						estadisticos.add( m_lineal );
						estadisticos.add( n_lineal );
						analisisTendencia.put( instanteMinuto, estadisticos );
					}

					/* Imprimir Serie de tiempo seccionada. */
					imprimeDatos( series, pwVectores, lowerBoundSerie, ( lowerBoundSerie + sizeTW - 1 ) );
					lowerBoundSerie += sizeTW;
					contador += 1;
					pwVectores.close( );
					pwModelo.close( );
				}
			}
		}
		catch( IOException io )
		{
			io.printStackTrace( );
		}

		return analisisTendencia;
	}

	public static double redondear( double numero,int digitos )
  {
    int cifras=(int) Math.pow(10,digitos);
    return Math.rint(numero*cifras)/cifras;
  }

	public static double calcular_n( double sum_X, double sum_X2, double sum_Y, double sum_Y2, double sum_XY, double N )
	{
		double n_lineal = 0;
		n_lineal = ( ( sum_Y * sum_X2 ) - ( sum_X * sum_XY ) ) / ( ( N * sum_X2 ) - ( Math.pow( sum_X, 2 ) ) );
		return n_lineal;
	}

	public static double calcular_m( double sum_X, double sum_X2, double sum_Y, double sum_Y2, double sum_XY, double N )
  {
    double m_lineal = 0;
    m_lineal = ( ( N * sum_XY ) - ( sum_X * sum_Y ) ) / ( ( N * sum_X2 ) - ( Math.pow( sum_X, 2 ) ) );
    return m_lineal;
  }

	public static void imprimeDatos( 	HashMap < String, Map< Long, Integer > > series, PrintWriter pwVectores,
																		Long lowerBoundSerie, Long upperBoundSerie )
	{
		Iterator it_2 = series.entrySet( ).iterator( );
    while( it_2.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it_2.next( );
      String word = ( String ) pair.getKey( );
      Map<Long, Integer > instante_frecuencia = series.get( word );
      for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
        pwVectores.println( word + "\t" + instante_minuto + "\t" + instante_frecuencia.get( instante_minuto ) );
    }
	}

	public static ArrayList< Double > sumY( HashMap < String, Map< Long, Integer > > series, Long lowerBoundSerie, Long upperBoundSerie )
	{
		ArrayList< Double > data = new ArrayList< Double >( );
		double sumY  = 0; /*Sumador de frecuencias*/
		double sumY2 = 0; /*Sumador de frecuencias cuadradas*/
		double sumXY = 0; /*Sumador de X*Y*/
		Iterator it_2 = series.entrySet( ).iterator( );
    while( it_2.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it_2.next( );
      String word = ( String ) pair.getKey( );
      Map<Long, Integer > instante_frecuencia = series.get( word );
			double i = 1;
      for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
			{
				sumY  += instante_frecuencia.get( instante_minuto );
				sumY2 += Math.pow( instante_frecuencia.get( instante_minuto ), 2 );
				sumXY += instante_frecuencia.get( instante_minuto ) * i;
				i += 1;
			}
    }
		data.add( sumY  );
		data.add( sumY2 );
		data.add( sumXY );
		return data;
	}

	public static ArrayList< Double > sumX( Integer cantDatos )
	{
		ArrayList< Double > data = new ArrayList < Double >( );
		double sum_X  = 0; /* sumador de X */
		double sum_X2 = 0; /* sumador de X cuadrado */
		for( int i = 1; i <= cantDatos; i++ )
		{
			sum_X += i;
			sum_X2 += Math.pow( i, 2);
		}
		data.add( sum_X );
    data.add( sum_X2 );
		return data;
	}

	public static void InsertarInstanteFrecuencia( ArrayList < String > palabras_Busqueda, HashMap < Long, Integer > frecuenciaPalabra,
																								Long ts_minutos, String text )
	{
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( );
			if ( palabras_Busqueda.contains( word ) )
				if ( ! frecuenciaPalabra.containsKey( ts_minutos ) )
					frecuenciaPalabra.put( ts_minutos, 1 );
				else
					frecuenciaPalabra.put( ts_minutos, frecuenciaPalabra.get( ts_minutos ) + 1 );
		}
	}

	public static void AsignarEtiqueta( HashMap< String, Map< Long, String > > dataBase, Long lowerBoundSerie, Long initCrisis, Long FinCrisis,
                                      Long upperBoundSerie, PrintWriter pw, HashMap < Long, Integer > frecuenciaPalabra, Long timeWindow,
																			ArrayList < String > palabras_Busqueda, HashMap < Long, ArrayList < Double  > > analisisTendencia,
																			double umbral_inferior, double umbral_superior )
	{
		String label = "";
    Iterator it = dataBase.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
      String idTuit = ( String ) pair.getKey( );
      Map< Long, String > corpus = dataBase.get( idTuit );
      Iterator it2 = corpus.entrySet( ).iterator( );
      while( it2.hasNext( ) )
      {
        Map.Entry pair2 = ( Map.Entry ) it2.next( );
        Long instante = ( Long ) pair2.getKey( );
        String metadata = ( String ) corpus.get( instante );
				//System.out.print( instante );
				if( analisisTendencia.containsKey( instante ) )
				{
					ArrayList < Double > estadisticos = analisisTendencia.get( instante );
					double m_lineal = estadisticos.get( 0 );
					double n_lineal = estadisticos.get( 1 );
					//System.out.print( "\t" + m_lineal + "\t" + n_lineal );
					if( m_lineal < umbral_inferior ) label = "NOC";
					else if ( umbral_inferior <= m_lineal /*&& m_lineal < umbral_superior*/ )label = "CRI";
				}
        pw.println( metadata + "\t" + label );
				//System.out.println( label );
      }
    }
	}


	public static void AsignarEtiqueta( HashMap< String, Map< Long, String > > dataBase, Long lowerBoundSerie, Long initCrisis, Long FinCrisis,
																			Long upperBoundSerie, PrintWriter pw )
	{
		String label = "";
		Iterator it = dataBase.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
			String idTuit = ( String ) pair.getKey( );
			Map< Long, String > corpus = dataBase.get( idTuit );
			Iterator it2 = corpus.entrySet( ).iterator( );
			while( it2.hasNext( ) )
 	 	 	{
      	Map.Entry pair2 = ( Map.Entry ) it2.next( );
				Long instante = ( Long ) pair2.getKey( );
				String metadata = ( String ) corpus.get( instante );
				if( lowerBoundSerie <= instante && instante <= initCrisis ) label = "PRECRISIS";
				if( initCrisis <= instante && instante <= FinCrisis ) label = "CRISIS";
				if( FinCrisis <= instante && instante <= upperBoundSerie ) label = "PRELUDIO";
				pw.println( metadata + "\t" + label );
			}
		}
	}

	public static Long frecuenciaMaxima( HashMap< String,Map< Long,Integer > > series, Long initCrisis, Long lowerBoundSerie, Long upperBoundSerie )
	{
		Long instante = initCrisis;
		Integer frecuenciaMax = 0;
		Iterator it_2 = series.entrySet( ).iterator( );
    while( it_2.hasNext( ) )
    {
    	Map.Entry pair = ( Map.Entry ) it_2.next( );
      String word = ( String ) pair.getKey( );
      Map<Long, Integer > instante_frecuencia = series.get( word );
      for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
				if( instante_frecuencia.get( instante_minuto ) >= frecuenciaMax && instante_minuto  > initCrisis )
				{
					instante = instante_minuto;
					frecuenciaMax = instante_frecuencia.get( instante_minuto );
				}
  	}
		return instante;
	}


	public static HashMap< String, Map< Long, String > > insertaDataBase( HashMap< String, Map< Long, String > > dataBase, String idTuit, Long ts_minutos, String corpus )
	{
		if( ! dataBase.containsKey( idTuit ) )
		{
			Map < Long, String > metadata = new HashMap<Long, String>( );
			metadata.put( ts_minutos, corpus );
			dataBase.put( idTuit, metadata );
		}
		return dataBase;
	}

	public static HashMap< String,Map< Long,Integer > > corregirSerie( HashMap< String,Map< Long,Integer > > series , Long upperBoundSerie,
																																			Long lowerBoundSerie)
	{
		Iterator it = series.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
    	Map.Entry pair = ( Map.Entry ) it.next( );
      String word = ( String ) pair.getKey( );
      Map<Long, Integer > instante_frecuencia = series.get( word );
      for ( Long instante_minuto = lowerBoundSerie; instante_minuto <= upperBoundSerie ; instante_minuto++ )
      	if ( ! instante_frecuencia.containsKey( instante_minuto ) )
        	instante_frecuencia.put( instante_minuto, 0 );
    }
		return series;
	}

	public static HashMap< String,Map< Long,Integer > > insertarSerie( String text, HashMap< String,Map< Long,Integer > > series,
																																			ArrayList<String>  palabras_Busqueda, String ts_minutos )
	{
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
    	String word = st.nextToken( );
      if( palabras_Busqueda.contains( word ) )
      {
      	if ( ! series.containsKey( word ) )
        {
        	Map<Long,Integer> instante_frecuencia = new HashMap<Long,Integer>( );
          Long instante = Long.parseLong( ts_minutos );
          instante_frecuencia.put( instante, 1 );
          series.put( word, instante_frecuencia );
       	}
				else
        {
        	Map<Long, Integer > instante_frecuencia = series.get( word );
          Long instante = Long.parseLong( ts_minutos );
          if( ! instante_frecuencia.containsKey( instante ) )
          	instante_frecuencia.put( instante, 1 );
          else
          	instante_frecuencia.put( instante, instante_frecuencia.get( instante ) + 1 );
      	}
    	}
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
          BW.add( linea );

        br.close( );
        fr.close( );

      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }

      return BW;
    }
}
