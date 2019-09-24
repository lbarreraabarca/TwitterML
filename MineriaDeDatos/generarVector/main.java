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
args[ 2 ] : Archivo con los vectores de caracteristicas
args[ 3 ] :
args[ 4 ] : Listado de Verbos y sus conjunciones
*/
public class main
{
	public static double minCV;
	public static double maxCV;

	public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {
  	if ( args.length != 12 )
    {
    	System.out.println( "[ generarVector ][ Error en la cantidad de argumentos ]" );
   	}
    else
    {
			minCV = -1;
			maxCV = -1;
			HashMap< String,Map< Long,Integer > > series = new HashMap< String, Map< Long ,Integer> >( ); /*Almacena la series de tiempos de todas las palabras*/
			Boolean imprimeJunto = true;
			HashMap< String, String > verbos = cargarVerbos( args[ 4 ] ); /* Listado de verbos con sus conjugaciones*/
			ArrayList < Long > instantes_ts_minuto = new ArrayList < Long >( );
      ArrayList < String >  palabras_Busqueda  = cargarBW( args[ 1 ] ); /* Palabra de busqueda. KeyWords por ejemplo Terremoto*/
			ArrayList < String >  bolsaPalabrasHashtags = new ArrayList< String >( );
			HashMap < String, Integer > frecuenciaHashtags = new HashMap < String, Integer >( );
			ArrayList < String >  bolsaUserMentions = new ArrayList< String >( );
			HashMap < String, Integer > frecuenciaUserMentions = new HashMap < String, Integer >( );
			ArrayList < String >  bolsaPalabras = new ArrayList< String >( );
			HashMap < String, Integer > frecuenciaPalabras = new HashMap < String, Integer >( );

      BufferedReader br = null;
      PrintWriter pw = null;
			PrintWriter pwBOW = null;
			PrintWriter pwBOH = null;
			PrintWriter pwBUM = null;
			PrintWriter pwVEST = null;
			PrintWriter pwVBOW = null;
			PrintWriter pwVBOH = null;
			PrintWriter pwVBUM = null;
      try
      {
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
				Long largoVentanaTiempo = Long.parseLong( args[ 3 ] ); /*Controla la cantidad de minutos que tendra la ventana de tiempo*/
				String linea = "";
				System.out.print( "[ generarVector ][ Cargando en Memoria Series de Tiempo ]");
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( "\t" );
					if( data.length != 11 ) continue;
					String ts_minutos = data[ 1 ]; /* instante del tuit*/
					String hashtags = data[ 2 ]; /* conjunto de hashtags del tuit*/
					String user_mentions = data[ 3 ]; /* conjunto de usuarios mencionados */
					String text = data[ 8 ]; /* cuerpo del tuit */
					text =  steamingEspanol( text, verbos ); /* Permite obtener verbos en infinitivo o sustantivos en singular. */
					instantes_ts_minuto.add( Long.parseLong( ts_minutos ) ); /* Se agregan los minutos para generar una series de minutos */
					insertarSerie( text, series, ts_minutos ); /* Procesamiento de series de tiempo */
					frecuenciaHashtags = agregarHashtagsFrecuencia( hashtags, frecuenciaHashtags );  /* Insertan hashtags a la bolsa de Hashtags */
					frecuenciaUserMentions  = agregarUserMentionsFrecuencia( user_mentions, frecuenciaUserMentions ); /* Insertan userMentions a la bolsa de UserMentions */
					frecuenciaPalabras = agregarBolsaPalabrasFrecuencia( text, frecuenciaPalabras );
      	}
				System.out.println( "[ OK ]" );
        br.close( );
				System.out.print( "[ generarVector ][  Generando Bolsa de Hashtags ]" );
				bolsaPalabrasHashtags = agregarHashtagsBolsa( bolsaPalabrasHashtags, frecuenciaHashtags );
				System.out.println( "[ OK ]" );

				System.out.print( "[ generarVector ][ Generando Bolsa de UserMentions ]" );
				bolsaUserMentions = agregarUserMentionsBolsa( bolsaUserMentions, frecuenciaUserMentions );
				System.out.println( "[ OK ]" );

				System.out.print( "[ generarVector ][ Generando Bolsa de Palabras ]" );
				bolsaPalabras = agregarBolsaPalabrasBolsa( bolsaPalabras, frecuenciaPalabras );

				System.out.println( "[ OK ]" );


				Long upperBoundSerie = maximoSerie( instantes_ts_minuto ); /* Obtiene el minuto maximo del conjunto de tuits*/
      	Long lowerBoundSerie = minimoSerie( instantes_ts_minuto ); /* Obtiene el minuto minimo del conjunto de tuits*/
				System.out.println( "[ generarVector ][ Liberando memoria de ArrayList instantes_ts_minuto ]" );
				instantes_ts_minuto.clear( );

				System.out.println( "[ generarVector ][ Obtener minimos y maximos por feature CV ]" );
				BufferedReader br3 = null; /* Para volver a realizar lectura sobre el archivo con la finalidad de generar las caracteristicas. */
				br3 = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
				Integer count1 = 0;
				while( ( linea = br3.readLine( ) ) != null)
        {
         	String[] data = linea.split( "\t" );
         	if( data.length != 11 ) continue;
         	String ts_minutos = data[ 1 ]; /* instante del tuit*/
         	String text = data[ 8 ]; /* cuerpo del tuit */
					text =  steamingEspanol( text, verbos );
					getMinMaxCVWord( series, bolsaPalabras, text, ts_minutos, largoVentanaTiempo, false );

				}
				br3.close( );

				System.out.println( "[ generarVector ][ Min : " + minCV + " ][ Max : " + maxCV +  " ]" );


				System.out.println( "[ generarVector ][ Cargar Tuits en memoria para vectorizar ]" );

				if( imprimeJunto ) pw = new PrintWriter( new FileWriter( args[ 2 ]) ); /* Archivo de salida para los vectores en Formato SVMLigth */
				pwVEST = new PrintWriter( new FileWriter( args[ 8 ]) );
				pwVBOW = new PrintWriter( new FileWriter( args[ 9 ]) );
				pwVBOH = new PrintWriter( new FileWriter( args[ 10 ]) );
				pwVBUM = new PrintWriter( new FileWriter( args[ 11 ]) );

				BufferedReader br2 = null; /* Para volver a realizar lectura sobre el archivo con la finalidad de generar las caracteristicas. */
				br2 = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
				while( ( linea = br2.readLine( ) ) != null)
        {
         	String[] data = linea.split( "\t" );
         	if( data.length != 11 ) continue;
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
         	String etiqueta = data[ 10 ]; /* Etiqueta del tuit puede ser PRECRISIS, CRISIS, PRELUDIO. */
					text =  steamingEspanol( text, verbos );
					/* TODO: Metodo para las caracteristicas Estructurales */
					String estructurales = generarCaracteristicasEstructurales( palabras_Busqueda, user_mentions, followers_count, retweet_count, text, ts_minutos,series, largoVentanaTiempo );
					/*TODO: Metodo para las caracteristicas Temporales */
					String temporales    = generarCaracteristicasTemporales( series, bolsaPalabras, text, ts_minutos, largoVentanaTiempo, false );
					String temporalesAll = generarCaracteristicasTemporales( series, bolsaPalabras, text, ts_minutos, largoVentanaTiempo, true );
					/*TODO: Metodo para las caracteristicas Bolsa de Hashtags */
					//String hashTagsCaracteristicas = generarCaracteristicasHashTags( bolsaPalabrasHashtags, hashtags, bolsaPalabras.size( ) + 12 );
					//String hashTagsCaracteristicas = generarCaracteristicasHashTags( bolsaPalabrasHashtags, hashtags, 1 );
					/*TODO: Metodo para las caracteristicas Bolsa de User Mentions */
					//String userMentionsCaracteristicas = generarCaracteristicasUserMentions( bolsaUserMentions, user_mentions, bolsaPalabras.size( ) + 12 + bolsaPalabrasHashtags.size( ) );
					//String userMentionsCaracteristicas = generarCaracteristicasUserMentions( bolsaUserMentions, user_mentions, 1 );

					String vectorFinal = /*estructurales + */ temporales /*+ hashTagsCaracteristicas + userMentionsCaracteristicas*/ ;
					if( imprimeJunto ) pw.println( id_tuit  + "\t" + etiqueta  + "\t" + vectorFinal );
					/* SVMLight Format */
 					pwVEST.println( id_tuit  + "\t" + etiqueta  + "\t" + estructurales );
					pwVBOW.println( id_tuit  + "\t" + etiqueta  + "\t" + temporales );
					//pwVBOH.println( id_tuit  + "\t" + etiqueta  + "\t" + hashTagsCaracteristicas );
					//pwVBUM.println( id_tuit  + "\t" + etiqueta  + "\t" + userMentionsCaracteristicas );
				}
				br2.close( );

				if ( imprimeJunto ) pw.close( );
				pwBOW = new PrintWriter( new FileWriter( new File( args[ 5 ] ) ) );
				pwBOH = new PrintWriter( new FileWriter( new File( args[ 6 ] ) ) );
				pwBUM = new PrintWriter( new FileWriter( new File( args[ 7 ] ) ) );
				escribeBolsa( pwBOW, bolsaPalabras );
				//escribeBolsa( pwBOH, bolsaPalabrasHashtags );
				//escribeBolsa( pwBUM, bolsaUserMentions );
				pwBOW.close();
				//pwBOH.close();
				//pwBUM.close();
				pwVEST.close();
				pwVBOW.close();
				//pwVBOH.close();
				//pwVBUM.close();
				/* TODO: Escribir la bolsa de palabras. bolsa de hashtags y bolsa de user mentions*/
     	}
      catch ( Exception e)
      {
      	e.printStackTrace();
      }

   	}

	}

	public static void escribeBolsa( PrintWriter pwB, ArrayList < String > bolsa )
	{
		for( int i =0 ; i < bolsa.size( ) ; i++ )
			pwB.println( bolsa.get( i ) );
	}


	public static String generarCaracteristicasUserMentions( ArrayList < String >  bolsaUserMentions, String user_mentions, int inicioVectorUserMentions )
	{
		int[] vectorUserMentions = new int[ bolsaUserMentions.size( ) ];
		for( int i = 0; i < bolsaUserMentions.size( ) ; i++ )
			vectorUserMentions[ i ] = 0;
		StringTokenizer st = new StringTokenizer( user_mentions );
    while( st.hasMoreTokens( ) )
    {
      String um = st.nextToken( );
			if( ! um.equals( "null_hashtag") )
				if( bolsaUserMentions.contains( um ) )
				{
					int posicionUserMentions = bolsaUserMentions.indexOf( um );
					vectorUserMentions[ posicionUserMentions ]++;
				}
		}
		String vectorSalida= "";
		for( int i = 0; i < bolsaUserMentions.size( ) ; i++ )
			if( vectorUserMentions[ i ] > 0 )
			{
				int posicionVector = inicioVectorUserMentions + i;
				vectorSalida= vectorSalida + posicionVector + ":" + vectorUserMentions[ i ] + " ";
			}
		return vectorSalida;
	}

	public static String generarCaracteristicasHashTags( ArrayList < String >  bolsaPalabrasHashtags, String hashtags, int inicioVectorHashtags )
	{
		int[] vectorHashtags = new int[ bolsaPalabrasHashtags.size( ) ];
		for( int i = 0; i < bolsaPalabrasHashtags.size( ) ; i++ )
			vectorHashtags[ i ] = 0;
		StringTokenizer st = new StringTokenizer( hashtags );
    while( st.hasMoreTokens( ) )
    {
      String ht = st.nextToken( );
			if( ! ht.equals( "null_userMentions") )
				if( bolsaPalabrasHashtags.contains( ht ) )
				{
					int posicionHashtag = bolsaPalabrasHashtags.indexOf( ht );
					vectorHashtags[ posicionHashtag ]++;
				}
		}
		String vectorSalida= "";
		for( int i = 0; i < bolsaPalabrasHashtags.size( ) ; i++ )
			//vectorSalida= vectorSalida + vectorHashtags[ i ] + ",";
			/* SVMLingth format */
 			if( vectorHashtags[ i ] > 0 ){
				int posicionVector = inicioVectorHashtags + i;
				vectorSalida= vectorSalida + posicionVector + ":" + vectorHashtags[ i ] + " ";
			}

		return vectorSalida;
	}

	public static void getMinMaxCVWord( HashMap< String,Map< Long,Integer > > series,
																																				ArrayList < String >  bolsaPalabras, String text,
																																				String ts_minutos, Long largoVentanaTiempo, Boolean inicioFeatures )
	{
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( ); /* palabra */
			if( bolsaPalabras.contains( word ) )
			{
				int posicionPalabra = bolsaPalabras.indexOf( word ); /* Obtener la posicion de la palabra en la bolsa */
				Long instante = Long.parseLong( ts_minutos );
				Map<Long, Integer > instante_frecuencia = series.get( word );
				Long inicioVentana = instante - ( largoVentanaTiempo - 1 );
        ArrayList < Double > ventanaTiempoFrecuencia = new ArrayList< Double >( );
        double promedio = 0.0;
        double sumaFrecuencias = 0.0;
        for( Long iSerie = inicioVentana; iSerie <= instante ; iSerie++ )
        {
        	if ( instante_frecuencia.containsKey( iSerie ) ){
          	sumaFrecuencias += instante_frecuencia.get( iSerie );
            ventanaTiempoFrecuencia.add( ( double )instante_frecuencia.get( iSerie ) );
          }
          else{
            sumaFrecuencias += 0.0; /* No existe frecuencia para ese minuto*/
            ventanaTiempoFrecuencia.add( ( double ) 0.0 );
          }
        }
        promedio = sumaFrecuencias / largoVentanaTiempo;
        double desviacionEstandar = 0.0;
        double sumaDesviaciones = 0.0;
        for( int i = 0 ; i < ventanaTiempoFrecuencia.size( ); i++ )
        {
          sumaDesviaciones += Math.pow( ventanaTiempoFrecuencia.get( i ) - promedio, 2 );
        }
				ventanaTiempoFrecuencia.clear( ); /* Se borra de memoria */
        desviacionEstandar = sumaDesviaciones / largoVentanaTiempo;
        desviacionEstandar = Math.sqrt( desviacionEstandar );
				double coeficienteVariacion = desviacionEstandar / promedio ;
				coeficienteVariacion = redondear( coeficienteVariacion, 3 );

				asingMinMax( coeficienteVariacion );
			}
		}
	}

	public static void asingMinMax( double cv ){
		if ( minCV == -1 )
			minCV =  cv;
		if ( maxCV == -1 )
			maxCV = cv;

		if ( minCV > cv )
			minCV =  cv;
		if ( maxCV < cv )
			maxCV = cv;

	}


	public static String generarCaracteristicasTemporales( HashMap< String,Map< Long,Integer > > series,
																																				ArrayList < String >  bolsaPalabras, String text,
																																				String ts_minutos, Long largoVentanaTiempo, Boolean inicioFeatures )
	{
		double[] vectorTemporalidad = new double[ bolsaPalabras.size( ) ];
		/* TODO: Inicializar el vectorTemporalidad */
		for( int i = 0; i < bolsaPalabras.size( ); i++ )
			vectorTemporalidad[ i ] = 0.0;
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( ); /* palabra */
			if( bolsaPalabras.contains( word ) )
			{
				int posicionPalabra = bolsaPalabras.indexOf( word ); /* Obtener la posicion de la palabra en la bolsa */
				Long instante = Long.parseLong( ts_minutos );
				Map<Long, Integer > instante_frecuencia = series.get( word );
				Long inicioVentana = instante - ( largoVentanaTiempo - 1 );
        ArrayList < Double > ventanaTiempoFrecuencia = new ArrayList< Double >( );
        double promedio = 0.0;
        double sumaFrecuencias = 0.0;
        for( Long iSerie = inicioVentana; iSerie <= instante ; iSerie++ )
        {
        	if ( instante_frecuencia.containsKey( iSerie ) )
          {
          	sumaFrecuencias += instante_frecuencia.get( iSerie );
            ventanaTiempoFrecuencia.add( ( double )instante_frecuencia.get( iSerie ) );
          }
          else
          {
            sumaFrecuencias += 0.0; /* No existe frecuencia para ese minuto*/
            ventanaTiempoFrecuencia.add( ( double ) 0.0 );
          }
        }
        promedio = sumaFrecuencias / largoVentanaTiempo;
        double desviacionEstandar = 0.0;
        double sumaDesviaciones = 0.0;
        for( int i = 0 ; i < ventanaTiempoFrecuencia.size( ); i++ )
        {
          sumaDesviaciones += Math.pow( ventanaTiempoFrecuencia.get( i ) - promedio, 2 );
        }
				ventanaTiempoFrecuencia.clear( ); /* Se borra de memoria */
        desviacionEstandar = sumaDesviaciones / largoVentanaTiempo;
        desviacionEstandar = Math.sqrt( desviacionEstandar );
				double coeficienteVariacion = desviacionEstandar / promedio ;
				coeficienteVariacion = redondear( coeficienteVariacion, 3 );
				if( coeficienteVariacion > 0 )
					vectorTemporalidad[ posicionPalabra ] = redondear( ( ( coeficienteVariacion - minCV ) / ( maxCV - minCV  ) ), 4 );
				else
					vectorTemporalidad[ posicionPalabra ] = 0.0;
				//word
			}
		}
		String vectorSalida = "";
		for( int i = 0; i < bolsaPalabras.size( ); i++ )
			/* SVMLight Format */
 			if ( vectorTemporalidad[ i ] > 0.0 )
			{
				int posicionReal = 0;
				if ( inicioFeatures )
					posicionReal = i + 12 ;
				else
					posicionReal = i + 1  ;

				vectorSalida = vectorSalida + posicionReal + ":" + vectorTemporalidad[ i ]  + " ";
			}

		return vectorSalida;
	}

	public static HashMap < String, Integer > agregarBolsaPalabrasFrecuencia( String text,
																													HashMap < String, Integer > frecuenciaPalabras )
  {
  	StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( ); /* palabra */
			if( ! frecuenciaPalabras.containsKey( word ) )
          frecuenciaPalabras.put( word, 1 );
        else
          frecuenciaPalabras.put( word, frecuenciaPalabras.get( word ) + 1 );
    }
    return frecuenciaPalabras;
  }

	public static ArrayList < String > agregarBolsaPalabrasBolsa( ArrayList < String > bolsaPalabras,
																														HashMap < String, Integer > frecuenciaPalabras )
  {
		Iterator it = frecuenciaPalabras.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
      String word = ( String ) pair.getKey( );
      Integer frecuencia = ( Integer ) frecuenciaPalabras.get( word );
      if( frecuencia > 4 )
        if( ! bolsaPalabras.contains( word ) ) bolsaPalabras.add( word );
    }
    return bolsaPalabras;
  }

	public static HashMap < String, Integer > agregarUserMentionsFrecuencia( String user_mentions,
																													HashMap < String, Integer > frecuenciaUserMentions )
	{
		if ( ! user_mentions.equals( "null_userMentions" ) )
		{
			StringTokenizer st = new StringTokenizer( user_mentions );
    	while( st.hasMoreTokens( ) )
    	{
      	String userMentions = st.nextToken( ); /* Hashtag */
				if( ! frecuenciaUserMentions.containsKey( userMentions ) )
          frecuenciaUserMentions.put( userMentions, 1 );
        else
          frecuenciaUserMentions.put( userMentions, frecuenciaUserMentions.get( userMentions ) + 1 );
			}
		}
		return frecuenciaUserMentions;
	}

	public static ArrayList < String > agregarUserMentionsBolsa( ArrayList < String > bolsaUserMentions, HashMap < String, Integer > frecuenciaUserMentions )
  {
		Iterator it = frecuenciaUserMentions.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
      String word = ( String ) pair.getKey( );
      Integer frecuencia = ( Integer ) frecuenciaUserMentions.get( word );
      if( frecuencia > 3 )
        if( ! bolsaUserMentions.contains( word ) ) bolsaUserMentions.add( word );
    }
    return bolsaUserMentions;
  }

	public static HashMap < String, Integer > agregarHashtagsFrecuencia( String hashtags,
																											HashMap < String, Integer > frecuenciaHashtags )
  {
    if ( ! hashtags.equals( "null_hashtag" ) )
    {
      StringTokenizer st = new StringTokenizer( hashtags );
      while( st.hasMoreTokens( ) )
      {
        String ht = st.nextToken( ); /* Hashtag */
				if( ! frecuenciaHashtags.containsKey( ht ) )
					frecuenciaHashtags.put( ht, 1 );
				else
					frecuenciaHashtags.put( ht, frecuenciaHashtags.get( ht ) + 1 );
      }
    }
    return frecuenciaHashtags;
  }

	public static ArrayList < String > agregarHashtagsBolsa( ArrayList < String > bolsaPalabrasHashtags,
                                                      HashMap < String, Integer > frecuenciaHashtags )
  {
		 Iterator it = frecuenciaHashtags.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = ( Map.Entry ) it.next( );
      String word = ( String ) pair.getKey( );
      Integer frecuencia = ( Integer ) frecuenciaHashtags.get( word );
			if( frecuencia > 3 )
				if( ! bolsaPalabrasHashtags.contains( word ) ) bolsaPalabrasHashtags.add( word );
    }
    return bolsaPalabrasHashtags;
  }


	public static String 	generarCaracteristicasEstructurales( ArrayList < String > palabras_Busqueda,
																																					String user_mentions, String followers_count,
																																					String retweet_count, String text,
																																					String ts_minutos,
																																					HashMap< String,Map< Long,Integer > > series,
																																					Long largoVentanaTiempo )
	{
		ArrayList < Double > estructurales = new ArrayList< Double >( );
		int cantidadCaracteres = text.length(); /* Cantidad de Caracteres del tuit */
		String []palabras = text.split( " " );
		double cantidadPalabras = palabras.length; /* Cantidad de palabras del tuit */
		double cantidadPalabrasClaves = 0;
		for ( int i = 0 ; i < cantidadPalabras ;  i++ )
			if( palabras_Busqueda.contains( palabras[ i ] ) )
				cantidadPalabrasClaves++; /* Ocurrencia de palabras claves */

		double tasaPalabraClaves = cantidadPalabrasClaves / cantidadPalabras ; /* Porcentaje de palabras claves en el tuit */
		tasaPalabraClaves = redondear( tasaPalabraClaves, 3 );
		int cantidadRetuit = Integer.parseInt( retweet_count );   /* Cantidad de veces retuiteado */

		String []usuarios_mencionados = user_mentions.split( " " );
		double cantidadUsuariosMencionados = 0 ;
		if ( ! user_mentions.equals( "null_userMentions" ) ) cantidadUsuariosMencionados = usuarios_mencionados.length;	/* Cantidad de usuarios mencionados*/

		double cantidadSeguidores = followers_count.length();  /* Cantidad de seguidores del usuario que publico el tuit*/
		int cantidadVocales = contadorVocales( text, true );
		int cantidadConsonantes = contadorVocales( text, false );
		int posicionHashtags = posicionHashtag( palabras, palabras_Busqueda );
		int cantidadPalabrasRafaga = contadorPalabrasRafaga( text, ts_minutos, series, largoVentanaTiempo);

		/*TODO: Insertar valores al vector de caracteristicas EStructurales */
		estructurales.add( ( double ) cantidadCaracteres ); 					/* 1 */
		estructurales.add( ( double ) cantidadPalabras );							/* 2 */
		estructurales.add( ( double ) cantidadPalabrasClaves );				/* 3 */
		estructurales.add( ( double ) tasaPalabraClaves );						/* 4 */
		estructurales.add( ( double ) cantidadRetuit );								/* 5 */
		estructurales.add( ( double ) cantidadUsuariosMencionados );	/* 6 */
		estructurales.add( ( double ) cantidadSeguidores );						/* 7 */
		estructurales.add( ( double ) cantidadVocales );							/* 8 */
		estructurales.add( ( double ) cantidadConsonantes );					/* 9 */
		estructurales.add( ( double ) posicionHashtags );							/* 10 */
		estructurales.add( ( double ) cantidadPalabrasRafaga );				/* 11 */

		String vectorSalida = "";
		for( int i = 0 ; i < estructurales.size( ) ; i++ )
			//vectorSalida = vectorSalida + estructurales.get( i ) + ",";
			/* SVMLight */
 			//Lo comentado sirve para imprimir formato SVMLight
 			if( estructurales.get( i ) > 0 )
			{
				int posicionSalida = i + 1;
				vectorSalida = vectorSalida + posicionSalida + ":" + estructurales.get( i ) + " ";
			}

		return vectorSalida;
	}

	public static int contadorPalabrasRafaga( String text, String ts_minutos, HashMap< String,Map< Long,Integer > > series, Long largoVentanaTiempo )
	{
		int varContadorPalabrasRafaga = 0;
		Long instante = Long.parseLong( ts_minutos );
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( );
			if ( series.containsKey( word ) )
			{
				Map<Long, Integer > instante_frecuencia = series.get( word );
				Long inicioVentana = instante - ( largoVentanaTiempo - 1 );
				ArrayList < Double > ventanaTiempoFrecuencia = new ArrayList< Double >( );
				double promedio = 0.0;
				double sumaFrecuencias = 0.0;
				for( Long iSerie = inicioVentana; iSerie <= instante ; iSerie++ )
				{
					if ( instante_frecuencia.containsKey( iSerie ) )
					{
						sumaFrecuencias += instante_frecuencia.get( iSerie );
						ventanaTiempoFrecuencia.add( ( double )instante_frecuencia.get( iSerie ) );
					}
					else
					{
						sumaFrecuencias += 0.0; /* No existe frecuencia para ese minuto*/
						ventanaTiempoFrecuencia.add( ( double ) 0.0 );
					}
				}
				promedio = sumaFrecuencias / largoVentanaTiempo;
				double desviacionEstandar = 0.0;
				double sumaDesviaciones = 0.0;
				for( int i = 0 ; i < ventanaTiempoFrecuencia.size( ); i++ )
				{
					sumaDesviaciones += Math.pow( ventanaTiempoFrecuencia.get( i ) - promedio, 2 );
				}
				desviacionEstandar = sumaDesviaciones / largoVentanaTiempo;
				desviacionEstandar = Math.sqrt( desviacionEstandar );
				double frecuencia;
				if( instante_frecuencia.containsKey( instante ) )
					frecuencia = instante_frecuencia.get( instante );
				else
					frecuencia = 0 ;
				double upperBoundFrecuencia = promedio  + 2 * desviacionEstandar ;
				if( frecuencia >= upperBoundFrecuencia )
					varContadorPalabrasRafaga++;
			}
		}

		return varContadorPalabrasRafaga;
	}

	public static int posicionHashtag( String []palabras, ArrayList < String > palabras_Busqueda )
	{
		int varPosicionHashtag = 0;
		for( int i = 0 ; i < palabras.length ; i++ )
			for( int j = 0 ; j < palabras_Busqueda.size( ) ; j++ )
			{
				String hashtag = "#" + palabras_Busqueda.get( j );
				if ( hashtag.equals( palabras[ i ] ) )
					varPosicionHashtag++;
			}
		return varPosicionHashtag;
	}

	public static int contadorVocales( String text, Boolean contarVocales )
	{
		int varContaVocales = 0 ;

		if( contarVocales == true )
		{
			for( int i = 0 ; i < text.length(); i++ )
				if( esVocal( text.charAt( i ) ) )
					varContaVocales++;
		}
		else
		{
			for( int i = 0 ; i < text.length(); i++ )
        if( ! esVocal( text.charAt( i ) ) )
          varContaVocales++;
		}

		return varContaVocales;
	}

	public static Boolean esVocal( char c )
	{
  	if ((Character.toLowerCase(c)=='a') || (Character.toLowerCase(c)=='e') || (Character.toLowerCase(c)=='i') || (Character.toLowerCase(c)=='o') || (Character.toLowerCase(c)=='u'))
    	return true;
  	else
    	return false;
	}

	public static HashMap< String,Map< Long,Integer > > corregirSerie( HashMap< String,Map< Long,Integer > > series , Long upperBoundSerie,
																																			Long lowerBoundSerie )
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
																																			String ts_minutos )
	{
		StringTokenizer st = new StringTokenizer( text );
    while( st.hasMoreTokens( ) )
    {
    	String word = st.nextToken( );
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

	/* Funcion que permite llevar verbos a infinitivo y sustantivos en plurales a singulares.  */
	//public static String steamingEspanol( String text, ArrayList < String > verbosConjugados, ArrayList < String > verbosInfinitivo )
	public static String steamingEspanol( String text, HashMap < String , String > verbos )
  {
    text = text.toLowerCase( );
  	StringTokenizer st = new StringTokenizer( text );
    String outputText = "";
    while ( st.hasMoreTokens( ) )
    {
      	String word = st.nextToken( );
				String palabraModificada = "";
      	// Primero se debe validar si es un verbo y luego si no es un verbo se puede validar como sustantivo
      	if( verbos.containsKey( word ) )
				{
					/* Se debe llevar a la raiz del verbo */
					palabraModificada = verbos.get( word );
				}
				else
				{
					/* Sino se pregunta si es un verbo y obtener la palabra en singular. */
      		palabraModificada = tipoSustantivo( word );
      	}
				outputText = outputText + palabraModificada + " ";

    }
    return outputText;
  }

  public static String tipoSustantivo( String palabra )
  {
  	String ultimaLetra = palabra.substring( palabra.length() -1 , palabra.length() );
    String palabraSingular = "";
    if ( ultimaLetra.equals( "s" ) )
    {
				if( palabra.length( ) <= 2 ) return palabra;
      	// Verificar si la letra anterior es una A, E u O
      	String penultimaLetra = palabra.substring( palabra.length( ) -2, palabra.length( ) - 1 );
      	String antepenultimaLetra = palabra.substring( palabra.length( ) -3 , palabra.length( ) - 2 );
      	if ( penultimaLetra.equals( "a" ) )
					palabraSingular = palabra.substring( 0, palabra.length( ) -1 );
      	else if ( penultimaLetra.equals( "e" ) ) // Ojo con la E se debe validas que despues de la E no venga una S
        {
       		if ( antepenultimaLetra.equals( "i" ) )
							palabraSingular = palabra.substring( 0, palabra.length( ) - 2 );
          	else if ( antepenultimaLetra.equals( "c" ) )  // Ademas validar qe antes de la E no haya una C porque de debe transformar a Z
							palabraSingular = palabra.substring( 0, palabra.length( ) - 3 ) + "z";
             else
							palabraSingular = palabra.substring( 0, palabra.length( ) -2 );
        }
      	else if ( penultimaLetra.equals( "o" ) )
					palabraSingular = palabra.substring( 0, palabra.length( ) -1 );
    }
		else if ( ultimaLetra.equals( "r" ) )
			palabraSingular = palabra; /* Es un verbo en infinitivo. */
		else if ( ultimaEsVocal( palabra ) )
			palabraSingular = palabra; /* Es una vocal no se debe hacer palabra singular. */
    return palabraSingular;
  }


	public static Boolean ultimaEsVocal( String text )
	{
			char letra = text.charAt( text.length( ) - 1 );
			if( letra == 'a' || letra == 'e' || letra == 'i' || letra == 'o' || letra == 'u' )
				return true;
			else
				return false;
	}

	public static void cargarVerbosArray( String archivoEntrada, ArrayList < String > verbosInfinitivo,
																				ArrayList < String > verbosConjugados )  throws FileNotFoundException, IOException
	{
		BufferedReader br = null;
    try
    {
      br = new BufferedReader( new FileReader( new File( archivoEntrada ) ) );
      String linea = "";
      while( ( linea = br.readLine( ) ) != null)
      {
        String []verbos = linea.split( "\t" );
        String conjugado = verbos[ 1 ].toLowerCase( );
        String infinitivo = verbos[ 0 ].toLowerCase( );
				if( ! verbosConjugados.contains( conjugado ) )
				{
					verbosConjugados.add( conjugado );
					verbosInfinitivo.add( infinitivo );
				}
      }
    }
    catch( IOException e )
    {
    }
	}

	public static HashMap < String, String > cargarVerbos( String archivoEntrada ) throws FileNotFoundException, IOException
	{
		HashMap < String, String > listadoVerbos = new HashMap< String, String >( );
		BufferedReader br = null;
		try
    {
			br = new BufferedReader( new FileReader( new File( archivoEntrada ) ) );
			String linea = "";
			while( ( linea = br.readLine( ) ) != null)
			{
				String []verbos = linea.split( "\t" );
				String conjugado = verbos[ 1 ].toLowerCase( );
				String infinitivo = verbos[ 0 ].toLowerCase( );
				if ( ! listadoVerbos.containsKey( conjugado ) )
					listadoVerbos.put( conjugado, infinitivo );
			}
		}
		catch( IOException e )
		{
		}
		return listadoVerbos;
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
    catch ( IOException e)
    {
    	/* Manejo de Excepciones. */
    }

    return BW;
	}

	public static double redondear( double numero,int digitos )
  {
    int cifras=(int) Math.pow(10,digitos);
    return Math.rint(numero*cifras)/cifras;
  }


}
