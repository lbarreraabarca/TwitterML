import java.io.*;
import java.util.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import java.net.URLClassLoader;
import java.net.*;
import java.net.URL;
import java.lang.Object;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/*
 * [ 0 ] : Archivo con las instancias.
 * [ 1 ] : Bolsa con las caracteristicas.
 * [ 2 ] : Archivo de salida de instancias vectorizadas.
 * [ 3 ] : Conjunto de Datos.
 * [ 4 ] : Listado de Verbos.
 * [ 5 ] : Largo Ventana de Tiempo.
 * [ 6 ] : Archivo Salida de instancias clasificadas.
 * [ 7 ] : Archivo con el modelo.
 * */
public class main
{
	public static void main( String[] args )
  {
		if ( args.length != 8 )
		{
			System.out.println( "Error en la cantidad de argumentos. " );
			System.exit( 0 );
		}
    try
		{
			Calendar calendar = Calendar.getInstance( );
	    Long ts_minutos_main  = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 ));
			ts_minutos_main--;
			Long ts_minutos_anterior = ts_minutos_main;
			System.out.print( "Cargando Caracteristicas " );
			ArrayList < String > bolsaPalabras = cargarBW( args[ 1 ] );
			System.out.println( "OK ! ");
			HashMap< String,Map< Long,Integer > > series = new HashMap< String, Map< Long ,Integer> >( );
			HashMap< String, String > verbos = cargarVerbos( args[ 4 ] ); /* Listado de verbos con sus conjugaciones*/
			ArrayList < String >  palabras_Busqueda  = new ArrayList< String > ( ) ; /* Palabra de busqueda. KeyWords por ejemplo Terremoto*/
			palabras_Busqueda.add( args[ 3 ] );
			while ( true )
			{
				calendar = Calendar.getInstance( );
	      ts_minutos_main  = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 ));
				ts_minutos_main--;
				Long diferencia_minutos = ts_minutos_main - ts_minutos_anterior;
				if( diferencia_minutos >= 1 )
				{
					System.out.print( "Leyendo Base de datos " + args[ 3 ] );
					/* TODO: Consultar a la base de datos, para el intervalo [ts_minutos - 4, ts_minutos] y conjunto dato Definido. Luego esa data escribirla a un archivo temporal. La base de datos entrega el input.... */
					consultarBaseDeDatos( Long.parseLong("25279119"), args[ 3 ], args[ 0 ], Long.parseLong( args[ 5 ] ) );
					System.out.println( " OK !" );
					leerVectorizarInstancias( args[ 0 ], args[ 2 ], series,  args[ 5 ], verbos, Long.parseLong("25279119") /*ts_minutos_main*/,  palabras_Busqueda, bolsaPalabras );
					System.out.println( "OK !");
					/* TODO: Ejecutar el modelo.*/
					ejecutarClasificacion( args[ 2 ], args[ 6 ], args[ 7 ] );
					/* TODO: Leer el archivo entregado por el modelo, y actualizar los tuits con el datos que fueron etiquetados.*/
					System.out.print( "Escribir en la Base de Datos" );
					escribirBaseDatos( args[ 6 ], args[ 0 ] );
					System.out.println( " OK!");
					ts_minutos_anterior = ts_minutos_main;
					series.clear( );
				}
			}
		}
		catch (Exception  e )
		{
		  e.printStackTrace();
    }
	}

	public static void escribirBaseDatos( String pathArchivosClasificados, String pathInstances )
	{
		ArrayList< String > etiquetas = new ArrayList < String >( );
		Integer contadorLineas = 0;
		BufferedReader br = null;
		String linea = "";
		try
		{
			br = new BufferedReader( new FileReader( new File( pathArchivosClasificados ) ) );
			while( ( linea = br.readLine( ) ) != null)
			{
				String etiquetaSeleccionada = "";
				String[] data = linea.split( "\t" );
				if( data.length == 7 )
				{
						double ponderacionCrisis = 0.0;
						double ponderacionPreludio = 0.0;
						double ponderacionPrecrisis = 0.0;
						/* CRISIS */
						if( data[ 1 ].equals( "CRISIS" ) ) ponderacionCrisis = Double.parseDouble( data[ 2 ] );
						else if ( data[ 3 ].equals( "CRISIS" ) ) ponderacionCrisis = Double.parseDouble( data[ 4 ] );
						else if ( data[ 5 ].equals( "CRISIS" ) ) ponderacionCrisis = Double.parseDouble( data[ 6 ] );

						/* PRELUDIO */
						if( data[ 1 ].equals( "PRELUDIO" ) ) ponderacionPreludio = Double.parseDouble( data[ 2 ] );
            else if ( data[ 3 ].equals( "PRELUDIO" ) ) ponderacionPreludio = Double.parseDouble( data[ 4 ] );
            else if ( data[ 5 ].equals( "PRELUDIO" ) ) ponderacionPreludio = Double.parseDouble( data[ 6 ] );

						/* PRECRISIS */
						if( data[ 1 ].equals( "PRECRISIS" ) ) ponderacionPrecrisis = Double.parseDouble( data[ 2 ] );
            else if ( data[ 3 ].equals( "PRECRISIS" ) ) ponderacionPrecrisis = Double.parseDouble( data[ 4 ] );
            else if ( data[ 5 ].equals( "PRECRISIS" ) ) ponderacionPrecrisis = Double.parseDouble( data[ 6 ] );

						if( ponderacionCrisis >= ponderacionPreludio && ponderacionCrisis >= ponderacionPrecrisis )
							etiquetaSeleccionada = "CRISIS";
						else if( ponderacionPreludio >= ponderacionCrisis && ponderacionPreludio >= ponderacionPrecrisis )
              etiquetaSeleccionada = "PRELUDIO";
						else if( ponderacionPrecrisis >= ponderacionPreludio && ponderacionPrecrisis >= ponderacionCrisis )
              etiquetaSeleccionada = "PRECRISIS";

						etiquetas.add( etiquetaSeleccionada );
				}
				else
					etiquetas.add( "NOLABEL ");
			}
			br.close( );
			BufferedReader brInstances = null;
			brInstances = new BufferedReader( new FileReader( new File ( pathInstances ) ) );
			while( ( linea = brInstances.readLine( ) ) != null)
			{
				String[] data = linea.split( "\t" );
		    		if( data.length != 12 ) continue;
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
						String conjuntoDato = data[ 11 ]; /*Conjunto dato que se quiere almacenar*/
						writeTwitterMongoDBClass( Long.parseLong( id_tuit ), Long.parseLong( ts_minutos ), hashtags, user_mentions, time_zone,
                                     	Long.parseLong( followers_count ), Long.parseLong( retweet_count ), text, horaUTC,
																			etiquetas.get( contadorLineas ), conjuntoDato );

						contadorLineas++;
			}
			brInstances.close( );
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
	}


	public static void consultarBaseDeDatos( Long ts_minutos, String conjuntoDato, String pathEscritura, Long timeWindow )
	{
		try
		{
			PrintWriter pw =  new PrintWriter( new FileWriter( pathEscritura ) );
			JSONParser parser = new JSONParser( );
			MongoClient mongoClient = new MongoClient("localhost");
      DB db = mongoClient.getDB("DB_TUITS_ONLINE");
      DBCollection collection = db.getCollection("tuits"); /*Debe leer de tuits*/
			while( timeWindow >= 1 )
			{
				BasicDBObject o = new BasicDBObject();
      	o.append("ts_minutos", ts_minutos);
      	o.append("conjuntoDato", conjuntoDato);
	    	for ( DBObject doc : collection.find( o ) )
				{
					String dataLine = doc.toString( );
					Object obj = parser.parse( dataLine );
        	JSONObject jsonObject = ( JSONObject ) obj;
        	Long idTuit = ( Long )jsonObject.get( "idTuit" );
					String hashtags = ( String )jsonObject.get( "hashtags" );
					String userMentions = ( String )jsonObject.get( "userMentions" );
					String location = "nullLocation";
					String timeZone = ( String )jsonObject.get( "timeZone" );
					Long followersCount = ( Long )jsonObject.get( "followersCount" );
					Long retweetCount = ( Long )jsonObject.get( "retweetCount" );
					String text = ( String )jsonObject.get( "text" );
					String horaUTC = ( String )jsonObject.get( "horaUTC" );
					String etiqueta = ( String )jsonObject.get( "etiqueta" );
					if( etiqueta.equals( "" )  ) etiqueta = "nullLabel";
					pw.println( idTuit + "\t" + ts_minutos + "\t" + hashtags + "\t" + userMentions + "\t" + location  + "\t" +
														timeZone + "\t" + followersCount + "\t" + retweetCount + "\t" + text +  "\t" + horaUTC + "\t" + etiqueta + "\t"
														+ conjuntoDato );
				}
				System.out.println( ts_minutos );
				ts_minutos--;
				timeWindow--;
			}
      mongoClient.close();
		}
		catch (Exception  e )
		{
		  e.printStackTrace();
   	}
	}

	public static void writeTwitterMongoDBClass( Long idTuit, Long ts_minutos, String hashtags, String userMentions, String timeZone,
																								Long followersCount, Long retweetCount, String text, String horaUTC, String etiqueta,
																								String conjuntoDato )
	{
		try
		{
			Long ts_day = ts_minutos / ( 60 * 24 );
			File jarMongo  = new File("lib/mongodb/mongo-java-driver-2.11.1.jar");
    	URL url = jarMongo.toURL();
	  	URL[] urls = new URL[]{url};
    	ClassLoader classloader = new URLClassLoader(urls);
			/*Clase MongoClient*/
    	Class classMongoClient = classloader.loadClass("com.mongodb.MongoClient");
    	Constructor constructorMongoClient = classMongoClient.getConstructor( String.class );
			Object mongoClient = constructorMongoClient.newInstance( "localhost" );
			/*Clase DB */
			Class classDB = classloader.loadClass("com.mongodb.DB");
			Method getDB = classMongoClient.getMethod( "getDB", String.class );
			Object DBObject = getDB.invoke( mongoClient, "DB_TUITS_ONLINE" );
			/*Clase DBCollection*/
			Class classDBCollection = classloader.loadClass("com.mongodb.DBCollection");
			Method getCollection = classDB.getMethod( "getCollection", String.class );
			Object CollectionObject = getCollection.invoke( DBObject, "ml_tuits" );
			/*Clase BasicDBObject*/
			Class classBasicDBObject = classloader.loadClass( "com.mongodb.BasicDBObject" );
			Constructor constructorBasicDBObject = classBasicDBObject.getConstructor( );
			Object BasicDBObject = constructorBasicDBObject.newInstance( );
			Method BasicPut = classBasicDBObject.getMethod( "append", String.class, Object.class );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "idTuit", idTuit );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "ts_minutos", ts_minutos );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "hashtags", hashtags );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "userMentions", userMentions );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "timeZone", timeZone );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "followersCount", followersCount );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "retweetCount", retweetCount );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "text", text );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "horaUTC", horaUTC );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "etiqueta", etiqueta );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "conjuntoDato", conjuntoDato );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "ts_day", ts_day );

			/*Clase WriteConcern*/
			Class classWriteConcern = classloader.loadClass( "com.mongodb.WriteConcern" );
			Field fieldWriteConcernSAFE = classWriteConcern.getField( "SAFE" );
			/*Clase DBObject*/
			Class classDBObject = classloader.loadClass( "com.mongodb.DBObject" );
			/*Llamada a insert de Clase Collection*/
			Method insertCollection = classDBCollection.getMethod( "save", classDBObject );
      Object DBObjectInstance = classDBObject.cast( BasicDBObject );
			/*Insertar documento en la base de datos*/
			insertCollection.invoke(CollectionObject, BasicDBObject );
			/*Cerrar conexion */
			Method closeMongoClient = classMongoClient.getMethod( "close" );
			closeMongoClient.invoke( mongoClient );
    }
		catch( Exception e )
		{
			e.printStackTrace( );
		}
	}

	public static void ejecutarClasificacion( String pathVectores, String pathArchivosClasificados, String pathModel ) throws IOException
	{
		String[] cmd  = {"../../MineriaDeDatos/Mallet/bin/mallet", "classify-file" , "--input" , pathVectores, "--output", pathArchivosClasificados,
										"--classifier", pathModel };
		Process pb = Runtime.getRuntime( ).exec( cmd );
		BufferedReader stdInput = new BufferedReader(new InputStreamReader( pb.getInputStream( ) ) );
		BufferedReader stdError = new BufferedReader(new InputStreamReader( pb.getErrorStream( ) ) );
		String s = "";
		while ( ( s = stdInput.readLine( ) ) != null ) System.out.println(s);
		stdInput.close( );
		while ((s = stdError.readLine()) != null) System.out.println(s);
		stdError.close( );
		System.out.println( "Clasificacion OK!" );
	}

	public static void leerVectorizarInstancias( String pathInstancias, String pathEscritura, HashMap< String,Map< Long,Integer > > series, String ventana, HashMap< String, String > verbos, Long ts_minutos_main, ArrayList < String >  palabras_Busqueda, ArrayList < String > bolsaPalabras )
	{
		BufferedReader br = null;
		String linea = "";
		System.out.print( "Cargando en Memoria Series de Tiempo ");
		try
		{
			br = new BufferedReader( new FileReader( new File( pathInstancias ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
			Long largoVentanaTiempo = Long.parseLong( ventana ); /*Controla la cantidad de minutos que tendra la ventana de tiempo*/
      while( ( linea = br.readLine( ) ) != null)
      {
				String[] data = linea.split( "\t" );
				if( data.length != 12 ) continue;
				String ts_minutos = data[ 1 ]; /* instante del tuit*/
				String hashtags = data[ 2 ]; /* conjunto de hashtags del tuit*/
				String user_mentions = data[ 3 ]; /* conjunto de usuarios mencionados */
				String text = data[ 8 ]; /* cuerpo del tuit */
				text =  steamingEspanol( text, verbos ); /* Permite obtener verbos en infinitivo o sustantivos en singular. */
				insertarSerie( text, series, ts_minutos ); /* Procesamiento de series de tiempo */
      }
			Long upperBoundSerie = ts_minutos_main; /* Obtiene el minuto maximo del conjunto de tuits*/
    	Long lowerBoundSerie = ts_minutos_main - largoVentanaTiempo  + 1; /* Obtiene el minuto minimo del conjunto de tuits*/
			br.close();
			BufferedReader br2 = null; /* Para volver a realizar lectura sobre el archivo con la finalidad de generar las caracteristicas. */
			br2 = new BufferedReader( new FileReader( new File( pathInstancias ) ) );
			PrintWriter pw =  new PrintWriter( new FileWriter( pathEscritura ) );
			while( ( linea = br2.readLine( ) ) != null)
		  {
		  	String[] data = linea.split( "\t" );
		    if( data.length != 12 ) continue;
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
				//String estructurales = generarCaracteristicasEstructurales( palabras_Busqueda, user_mentions, followers_count, retweet_count, text, ts_minutos,series, largoVentanaTiempo );
				/*TODO: Metodo para las caracteristicas Temporales */
				String temporales = generarCaracteristicasTemporales( series, bolsaPalabras, text, ts_minutos, largoVentanaTiempo );
				/*TODO: Metodo para las caracteristicas Bolsa de Hashtags */
				//String hashTagsCaracteristicas = generarCaracteristicasHashTags( bolsaPalabrasHashtags, hashtags, bolsaPalabras.size( ) + 12 );
				//String hashTagsCaracteristicas = generarCaracteristicasHashTags( bolsaPalabrasHashtags, hashtags, 1 );
				/*TODO: Metodo para las caracteristicas Bolsa de User Mentions */
				//String userMentionsCaracteristicas = generarCaracteristicasUserMentions( bolsaUserMentions, user_mentions, bolsaPalabras.size( ) + 12 + bolsaPalabrasHashtags.size( ) );
				//String userMentionsCaracteristicas = generarCaracteristicasUserMentions( bolsaUserMentions, user_mentions, 1 );
				//String vectorFinal = estructurales + temporales + hashTagsCaracteristicas + userMentionsCaracteristicas ;
				String vectorFinal = /*estructurales +*/ temporales; //+ hashTagsCaracteristicas + userMentionsCaracteristicas ;
				//System.out.println(  id_tuit  + "\t" + etiqueta  + "\t" + vectorFinal );
				pw.println( vectorFinal );
			}
			br2.close( );
			pw.close( );
		}
		catch( Exception lectura )
		{
			lectura.printStackTrace( );
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
			if( estructurales.get( i ) > 0 )
			{
				int posicionSalida = i + 1;
				vectorSalida = vectorSalida + posicionSalida + ":" + estructurales.get( i ) + " ";
			}
		return vectorSalida;
	}


	public static String generarCaracteristicasTemporales( HashMap< String,Map< Long,Integer > > series,
																																				ArrayList < String >  bolsaPalabras, String text,
																																				String ts_minutos, Long largoVentanaTiempo )
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
				vectorTemporalidad[ posicionPalabra ] = coeficienteVariacion;
			}
		}
		String vectorSalida = "";
		for( int i = 0; i < bolsaPalabras.size( ); i++ )
      if ( vectorTemporalidad[ i ] > 0.0 )
			{
				int posicionReal = i + 1 ;
				//int posicionReal = i + 12 ;
				vectorSalida = vectorSalida + posicionReal + ":" + vectorTemporalidad[ i ]  + " ";
			}
		return vectorSalida;
	}


	public static double redondear( double numero,int digitos )
  {
    int cifras=(int) Math.pow(10,digitos);
    return Math.rint(numero*cifras)/cifras;
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

	public static Boolean esVocal( char c )
	{
  	if ((Character.toLowerCase(c)=='a') || (Character.toLowerCase(c)=='e') || (Character.toLowerCase(c)=='i') || (Character.toLowerCase(c)=='o') || (Character.toLowerCase(c)=='u'))
    	return true;
  	else
    	return false;
	}

}
