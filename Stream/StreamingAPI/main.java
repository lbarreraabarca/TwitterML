import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
import java.io.*;
import java.util.*;
import twitter4j.json.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
//import org.json.simple.JSONArray;
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
//Argumentos:
//0: numero de cuenta (cuentas.dat)
//1: linea de terminos a leer (terminos.dat)
//2: path de escritura
//3: Nombre del archivo con los proxies
//4: Nombre del archivo de terminos
//5: Nombre de archivo de las cuentas
//6: Nombre de archivo de stopwords
//7: Tamano de la serie de tiempo
//8: Umbral para determinar que es rafaga y que no
//9: constante que acompana a la funcion mean + constante * std
//10: Frecuencia mínima en el instante actual
//11: Output Tweet por dia
//12: Nombre del archivo para mapear los terminos de busqueda
//13: Conjunto de Datos
public class main {
  static Integer n = 0;
  static private final Object lock = new Object( );
  static Integer largoSerie;  // Largo de la ventana movil
  static String pr; // ip del proxy por el cual se escuchara
  static Integer pu;  // puerto asociado al proxy
  static Long lowerBound; //cota inferior de la hora de inicio de la descarga
  static ArrayList < String > stopwords;  //Lista con las palabras stopwords que deben ser eliminadas de los tweets.
  static HashMap<String, ArrayList < Integer > > words_serie; //Estructura de datos principal: almacena la palabra y su serie.
	static HashMap<String, Integer > palabras_por_dia; //Estructura de datos: Que almacena palabra y la frecuencia diaria.
  static HashMap < String, Long > initWord; //almacena la palabra y el instante en el cual inicio la descarga de dicha palabra.
  static Double thresholdCV;  //Umbral para determinar que CV son rafaga y cuales no.
  static Double constantSTD;  //constante que acompana a la funcion mean + constante * std
  static Integer currentFrecuencyMin; // Frecuencia actual minima en un intervalo de tiempo determinado
  static Integer countTweetDay; //Contador de cantidad de tuits por dia
  static Long lowerBoundTweetDay; //Cota Inferior para verificar si ha pasado un dia
  static String termino;  //Termino de busqueda
  static String burstyDay; //String que almacena las rafagas detectadas en un dia
	static String conjuntoDato;
	//static MongoClient mongoClient;
  public static void main( String arg[ ] ) throws IOException, JSONException, InterruptedException, Throwable, NoSuchProviderException, MessagingException
  {
    HashMap<String,  ArrayList < Integer > >words_serie = new HashMap<String, ArrayList < Integer > >( );
    HashMap<String, Long > wordInit = new HashMap<String, Long>( );
    if( arg.length != 14 ) System.out.println( "Cantidad de Argumentos invalida" );
    else
    {
      System.out.println( "Cargando Stopwords..." );
      stopwords = cargarStopWords( arg[ 6 ] );  //Se carga las stopwords en memoria.
      largoSerie = Integer.parseInt( arg[ 7 ] );  //Se obtiene el largo de la ventana movil
      thresholdCV = Double.parseDouble( arg[ 8 ] );
      constantSTD = Double.parseDouble( arg[ 9 ] );
      currentFrecuencyMin = Integer.parseInt( arg[ 10 ] );
      termino = terminoDescarga( arg[ 1 ] , arg[ 12 ] );
      String logFile = arg[ 11 ];
			conjuntoDato = arg[ 13 ];
      burstyDay = "";
      while( true )
      {
        System.out.println( "Leyendo Listado Proxies..." );
        HashMap<String,Integer> proxies = CargarListadoProxies( arg[3] ); //Se cargan los proxies y puerto en memoria
        String proxy = null;
        Integer puerto = -1;
        Integer countProxy = 0;
        for( Map.Entry<String,Integer> entry : proxies.entrySet( ) )
        {
          proxy = entry.getKey( );  //Obtiene el proxy
          puerto = entry.getValue( ); //Obtiene el puerto
          countProxy++; //Aumenta el contador de proxy para saber cuantos ha consumido
          main objm = new main( );
          objm.listenerData(proxy, puerto, arg[0], arg[5], arg[2], arg[4], arg[1], words_serie, wordInit, logFile );  //Comienza la dload
          try
          {
            synchronized( lock )
            {
              lock.wait( ); //El hilo de descarga toma el control de la ejecucion y el main queda esperando.

            }
          }
          catch( InterruptedException e )
          {
            e.printStackTrace();
          }
          System.out.println( "Proxy cerrado..." );
          Thread.sleep( 5000 );
        }
        System.out.println( "Proxies agotados..." );
        String correo = "Se agotaron los proxies : " + termino;
        sendEmail( "l.barreraabarca@uandresbello.edu", "Proxies Agotados", correo );
      }
    }
  }

  public void listenerData( String proxy, Integer puerto, String n, String fileCuentas,
                            String fileSalida, String fileTerminos, String nTerms,
                            HashMap<String,  ArrayList < Integer > > ws, HashMap<String, Long > wordInitial, String logFile )
                            throws NoSuchMethodError, IOException, InterruptedException,JSONException
  {
    initWord = wordInitial; //Se inicializa initWord con wordInitial que es parametro de la funcion.
    SimpleDateFormat format1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"); //Define el formato de entrada de las fechas.
    String initDate = format1.format(new Date()); //Se toma la fecha actual con el reloj interno del servidor.
    try
    {
      Date lowerDate = format1.parse( initDate );
      lowerBound = lowerDate.getTime( ) / ( 60 * 1000 ) ; //Se asigna la cota inferior al inicio de la descarga
    }
    catch( Exception edate )
    {
      //Exception Date
    }
    ConfigurationBuilder cb = new ConfigurationBuilder( );  // iniciar con su constructor el cb para comenzar descarga
    cb.setDebugEnabled( true );
    cb.setJSONStoreEnabled( true );
    cb.setHttpProxyHost( proxy ); //Seteo los valores para proxy:
    cb.setHttpProxyPort( puerto ); //Seteo los valores para el puerto asociado al proxy:
    System.out.println( "Cargando datos de la cuenta..." );
    cargarCuenta( n , cb, fileCuentas );  //Cargo los datos de la cuenta donde n es el numero de la cta a utilizar
    final String file_out = fileSalida;   //Definir archivo de salida donde seran escritos los tweets.
    final FileWriter file = new FileWriter( file_out , true ); //Archivo de salida donde se escriben los tuits en formato json.
    final FileWriter logF = new FileWriter( logFile, true ); //Archivo que muestra la cantidad de tuits por dia.
    countTweetDay = 0; //Contador de tuits por dia.
    lowerBoundTweetDay = Long.parseLong( "0" );
    TwitterStream ts = new TwitterStreamFactory( cb.build() ).getInstance();  //Configuracion http
    StatusListener listener;
    pr  = proxy;  // Se inicializa pr con el proxy a descargar.
    pu = puerto;  // Se inicializa pu con el puerto asociado al proxy por el cual se descarga.
    words_serie = ws; // Se inicializa la estrcutura de datos principal con lo que viene por parametro de la funcion.
		palabras_por_dia = new HashMap < String, Integer >( );
    listener = new StatusListener( )
    {
		  @Override
			public void onStatus( Status status ) // Se logro descargar un tweet en esta funcion se realiza el procesamiento del tweet
			{
		    try
		    {
		      String json = TwitterObjectFactory.getRawJSON( status ); // Obtiene el tweet en formato string
          JSONObject jsonObject = new JSONObject( json ); // Obtiene el tweet en formato JSON
          Calendar calendar = Calendar.getInstance( );    // Se genera una instancia de la hora y fecha actual
          final String TWITTER="EEE, dd MMM yyyy HH:mm:ss Z"; // Formato de la fecha.
          SimpleDateFormat sdf = new SimpleDateFormat( TWITTER, Locale.ENGLISH ); //Se define que debe venir en formato Ingles
          sdf.setLenient(true);
          try
				  {
					  file.write( jsonObject.toString() );  //Se escribe el tweet completo en formato JSON
            file.write( "\n" );
          }
          catch( IOException e )
          {
		        e.printStackTrace( ); // Si hubo algún error en la escritura
          }

          try
          {
            String objTime = ( String )jsonObject.getString( "created_at" );  //Se obtiene la fecha de creacion del tweet
            String idStr = (String)jsonObject.getString( "id_str" );
            if( objTime != null )
            {
              String []data = objTime.split( " " ); // data[0] = Sun ; data[1] =Apr ; [2]=30; [3]=16:20:48; [4]=+0000 ; [5]=2017
              String postedTime = data[0] + ", " + data[2] + " " + data[1] + " " + data[ 5 ]+ " " + data[ 3 ] + " " + data[ 4 ];
              Date date = sdf.parse( postedTime );  //Se asigna la hora del tweet
              calendar.setTime( date ); // Se setea el valor en el calendario
              Long ts_minutos = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 ));  //Se transforma la fecha a un numero expresado en minutos.
              String body = ( ( String )jsonObject.getString( "text" ) ).toLowerCase( );  //Se obtiene el texto del tweet
              body = eliminarHttp( body ); //Eliminan todas las referecnias a url.
              body = eliminarSignosPuntuacion( body );  //Eliminan signos de puntuacion.
              body = cleanText( body ); //Se eliminan tildes.
              Long deltaTime = ts_minutos - lowerBound; //Variable de control para saber si aumento en minuto
              countTweetDay++;
              Long ts_day = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 * 60 * 24 )); // Obtener la fecha del tweet en dias
							/* Control y procesamiento de series de tiempo. */
              if ( deltaTime < 0 )
              {
                lowerBound = ts_minutos;  //Se asigan el lowerbound como el primer tweet descargado
                insertWord( body, ts_minutos ); //Se insertan las palabras del tweet a la struct
              }
              else if ( deltaTime == 0 )  //Todabia esta en el mismo minuto
                insertWord( body, ts_minutos ); //Se insertan las palabras del tweet a la struct
              else if ( deltaTime > 0 ) //Paso un minuto se deben procesar los datos
              {
                repair( lowerBound ); //Si una palabra no fue mencionada en ese minuto su frecuencia debe ser 0.
                frecuencyNull( lowerBound ); //Si la suma de las frecuencias de la ventana movil es 0 entonces se borra.
                burstyDetection( thresholdCV, constantSTD, lowerBound );  //Se analiza si la palabra es rafaga o no.
                lowerBound = ts_minutos;  //lowerbound va a ser el ts_minutos actual para hacer movil la cota inferior
                insertWord( body, ts_minutos ); //Se insertan las palabras del tweet a la struct
                tweetsForDay( logF, ts_day, postedTime );
                String correo = "Cantidad de tuits : " + countTweetDay + "\n";
                //sendEmail( "l.barreraabarca@uandresbello.edu" , "cantidad de tuits" , correo );
              }

							Long tuitID = idTuit( jsonObject );
							String ht_list = ListarHashtags( getHashTags( jsonObject ) );
							String user_mentions = ListarUserMentions( getUserMentions( jsonObject ) );
							writeTwitterMongoDBClass( tuitID , ts_minutos , ht_list , user_mentions, getTime_Zone( jsonObject ),
                  											getFollowersCount( jsonObject ), getRetuitCount( jsonObject ), body, objTime, "");
							/* Control y procesamiento de palabras por dia. */
							Long deltaTimeDay = ts_day - lowerBoundTweetDay;
							if( deltaTimeDay  < 0 )
							{
								lowerBoundTweetDay = ts_day;
								insertWordsForDay( palabras_por_dia, ts_day, body );
							}
							else if ( deltaTimeDay == 0 )
								insertWordsForDay( palabras_por_dia, ts_day, body );
							else if ( deltaTimeDay > 0 )
							{
								lowerBoundTweetDay = ts_day;
								insertWordsForDay( palabras_por_dia, ts_day, body );
								writeWordsForDayMongoDB( palabras_por_dia, ts_day );
								palabras_por_dia.clear();
							}
						}
           }
           catch( Exception eo )
           {
            eo.printStackTrace( );
           }
		    }
		    catch( JSONException js )
        {
		      System.out.println( "error JSONException");
		    }
			}

      @Override
      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
      {
        //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
      }

      @Override
      public void onTrackLimitationNotice(int numberOfLimitedStatuses)
      {
         System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
      }

      @Override
      public void onScrubGeo(long userId, long upToStatusId)
      {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
      }

      @Override
      public void onStallWarning(StallWarning warning)
      {
        System.out.println("Got stall warning:" + warning);
      }

      @Override
      public void onException(Exception ex)
      {
        synchronized( lock )
        {
          System.out.println("Se cayo la descarga por proxy : " + pr);
          lock.notify( ); //El hilo de descarga suelta el control del procesador y lo reasigna al main.
          Thread.currentThread().stop( ); //Se elimina el hilo de descarga.
        }
      }
    };
    String[] keywords = cargarTerminos( nTerms, fileTerminos ); //Se definen los terminos de busqueda de los tweets
    FilterQuery fq = new FilterQuery( );  //Se inicializa el filterQuery
    String lang [] = {"es"};  //Se define que el lenguaje de descarga de los tweets deben ser en espanol
    fq.language( lang );   //SE setea el valor del filterQuery con lang.
    fq.track( keywords ); //Se setea las palabras claves al filterQuery
    ts.addListener( listener ); //Comienza el sistema que escucha el flujo de tweets que vienen.
    ts.filter( fq );
  }

//*********************************************************************************************
//Metodos estaticos:
//******************************************************************************************

	public static void insertWordsForDay( HashMap < String, Integer > palabras_por_dia, Long ts_day, String body )
	{
		StringTokenizer st = new StringTokenizer( body );
    while( st.hasMoreTokens( ) )
    {
      String token = st.nextToken( );
      token = onlyWords( token );
      if ( stopwords.contains( token ) || token.length( ) <= 3 ) continue;
			if ( ! palabras_por_dia.containsKey( token ) )
				palabras_por_dia.put( token , 1 );
			else
				palabras_por_dia.put( token, palabras_por_dia.get( token ) + 1 );
		}
	}

	public static void writeWordsForDayMongoDB( HashMap <String, Integer > palabras_por_dia, Long ts_day )
	{
		try
    {
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
      Object CollectionObject = getCollection.invoke( DBObject, "tuits_por_dias" );

			/*Recorrer estructura de datos.*/
			Iterator it = palabras_por_dia.entrySet( ).iterator( );
    	while( it.hasNext( ) )
    	{
      	Map.Entry pair = (Map.Entry)it.next( );
      	String wordi = ( String )  pair.getKey( );
 				Integer frecuencia = palabras_por_dia.get( wordi );

				/*Clase BasicDBObject*/
				Class classBasicDBObject = classloader.loadClass( "com.mongodb.BasicDBObject" );
      	Constructor constructorBasicDBObject = classBasicDBObject.getConstructor( );
      	Object BasicDBObject = constructorBasicDBObject.newInstance( );
      	Method BasicPut = classBasicDBObject.getMethod( "append", String.class, Object.class );
     	 	BasicDBObject = BasicPut.invoke( BasicDBObject, "palabra", wordi );
      	BasicDBObject = BasicPut.invoke( BasicDBObject, "ts_day", ts_day );
      	BasicDBObject = BasicPut.invoke( BasicDBObject, "frecuencia",  frecuencia );
				BasicDBObject = BasicPut.invoke( BasicDBObject, "conjuntoDato",  conjuntoDato );

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
      }
			/*Cerrar conexion */
			Method closeMongoClient = classMongoClient.getMethod( "close" );
			closeMongoClient.invoke( mongoClient );
    }
    catch( Exception e )
    {
      e.printStackTrace( );
    }
	}


	public static void writeTwitterMongoDBClass( Long idTuit, Long ts_minutos, String hashtags, String userMentions, String timeZone,
																								Long followersCount, Long retweetCount, String text, String horaUTC, String etiqueta )
	{
		try
		{
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
			Object CollectionObject = getCollection.invoke( DBObject, "tuits" );
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



  public static void sendEmail( String destinatario, String subject, String text ) throws NoSuchProviderException, MessagingException
  {
    Properties props = new Properties( );
    // Nombre del host de correo, es smtp.gmail.com
    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    // TLS si está disponible
    props.setProperty("mail.smtp.starttls.enable", "true");
    // Puerto de gmail para envio de correos
    props.setProperty("mail.smtp.port", "587");
    // Nombre del usuario
    props.setProperty("mail.smtp.user", "lu.barreraabarca@gmail.com");
    // Si requiere o no usuario y password para conectarse.
    props.setProperty("mail.smtp.auth", "true");
    Session session = Session.getDefaultInstance(props);
    // Para obtener un log de salida mas extenso
    session.setDebug( true );
    MimeMessage message = new MimeMessage( session );
    // Quien envia el correo
    message.setFrom(new InternetAddress( "lu.barreraabarca@gmail.com" ) );
    // A quien va dirigido
    message.addRecipient( Message.RecipientType.TO, new InternetAddress( destinatario ) );
    message.setSubject( subject );
    message.setText( text );
    message.saveChanges();
    /*ENVIAR EL MENSAJE*/
    Transport t = session.getTransport( "smtp" );
    // Aqui usuario y password de gmail
    t.connect( "smtp.gmail.com", 587, "lu.barreraabarca@gmail.com", "f5d9791611" );
    t.sendMessage( message, message.getAllRecipients( ) );
    t.close( );
  }
	/*
	public static void writeTweetsMongoDB( Long idTuit, Long ts_minutos, String hashtags, String userMentions, String timeZone, Long followersCount,
																					Long retweetCount, String text, String horaUTC, String etiqueta)
	{
		try
    {
      MongoClient mongoClient = new MongoClient("localhost");
     	DB db = mongoClient.getDB("DB_TUITS_ONLINE");
			DBCollection collection = db.getCollection("tuits");
			BasicDBObject objectTuit = new BasicDBObject( );
			objectTuit.put( "idTuit", idTuit );
			objectTuit.put( "ts_minutos", Long.toString( ts_minutos ) );
			objectTuit.put( "hashtags", hashtags );
			objectTuit.put( "userMentions", userMentions );
			objectTuit.put( "timeZone", timeZone );
			objectTuit.put( "followersCount", followersCount );
			objectTuit.put( "retweetCount", retweetCount );
			objectTuit.put( "text", text );
			objectTuit.put( "horaUTC", horaUTC );
			objectTuit.put( "etiqueta", etiqueta );
 			collection.insert( WriteConcern.SAFE, objectTuit );
      mongoClient.close();
    }
    catch (Exception  e )
    {
      e.printStackTrace();
    }
	}*/

  public static void tweetsForDay( FileWriter fw, Long ts_day, String dateDay ) throws NoSuchProviderException
  {
    Long diff = ts_day - lowerBoundTweetDay;
    if( diff == 1 )
    {
      try
      {
        fw.write(  dateDay + " " + countTweetDay );
        String correo = "Cantidad de tuits : " + countTweetDay + "\n";
        correo = correo + "Fecha : " + dateDay + "\n";
        correo = correo  + "Termino : " + termino + "\n";
        correo = correo + "-------------------------------------\n";
        correo = correo + burstyDay + "\n";
        sendEmail( "l.barreraabarca@uandresbello.edu" , "Cantidad de tuits " + termino , correo );
        //sendEmail( "carlos.gomez.pantoja@unab.cl", "Cantidad de tuits " + termino, correo );

      }
      catch ( Exception e )
      {
        e.printStackTrace( );
      }
      //String correo = "Cantidad de tuits : " + countTweetDay + "\n";
      //correo = correo + "Fecha : " + dateDay + "\n";
      //sendEmail( "l.barreraabarca@uandresbello.edu" , "Cantidad de tuits" , correo );
      countTweetDay = 0 ;
      lowerBoundTweetDay = ts_day;
    }
  }

  public static void frecuencyNull( Long ts_minutos )
  {
    ArrayList< String > eliminar = new ArrayList < String >( );
    Iterator it = words_serie.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      double zero = 0;
      ArrayList< Double > list = new ArrayList< Double >( );
      Map.Entry pair = (Map.Entry)it.next( );
      String wordi = ( String )  pair.getKey( );
      ArrayList< Integer > windowSerie = ( ArrayList< Integer > )pair.getValue( );
      Long inicio = initWord.get( wordi );
      if ( windowSerie.size( ) == largoSerie )
      {
        for ( int iw = 0; iw < windowSerie.size( ); iw++ )
        {
          Integer xx = ( Integer )windowSerie.get( iw );
          zero+= xx;
        }
        if ( zero == 0 )
        {
          eliminar.add( wordi );  //System.out.print( "Delete. \t" ); ImprimirSerie( wordi, ts_minutos, true );
        }
      }
    }

    for ( int i = 0; i < eliminar.size( ); i++)
    {
      words_serie.remove( eliminar.get( i ) );
      initWord.remove( eliminar.get( i ) );
    }
  }

  public static void repair( Long ts_minutos )
  {
    for ( Map.Entry< String, Long> entry : initWord.entrySet( ) )
    {
      Long initWindow = entry.getValue( );
      String word = entry.getKey( );
      ArrayList< Integer > windowSerie = ( ArrayList< Integer > ) words_serie.get( word );
      Long last = initWindow + windowSerie.size( ) - 1 ;
      Long diff = ts_minutos - last;
      if ( diff == 1 )
      {
        if (  windowSerie.size( ) < largoSerie )
          windowSerie.add( 0 );
        else if( windowSerie.size( ) == largoSerie )
        {
          windowSerie.remove( 0 );
          windowSerie.add( 0 );
          initWord.put( word, initWord.get( word ) + 1 );
        }
      }
    }
  }

  public static String onlyWords( String text )
  {
    String resultado = text.replaceAll("[^a-z\u00F1#@]*", "");
    return resultado;
  }

  public static String cleanText( String body )
  {
    body = body.replaceAll( "-|:|,|/|'|!|\u00BF|\u00A1|;|&|\u00B0|\u00BA|\\\\.|\\\\?|\\\\)|\\\\(|\\\\||\\\\*|%|=|\u00AC", " " );
    body = body.replaceAll( "\u00E1", "a" );
    body = body.replaceAll( "\u00E9", "e" );
    body = body.replaceAll( "\u00ED", "i" );
    body = body.replaceAll( "\u00F3", "o" );
    body = body.replaceAll( "\u00FA", "u" );
    return body;
  }

  public static String ImprimirSerie( String word, Long ts_minutos, Boolean active )
  {
    String out = word + "\t" + ts_minutos + "\t";
    ArrayList < Integer > window = ( ArrayList < Integer > ) words_serie.get( word );
    for( int i = 0; i < window.size( ) ; i++ )
    {
      out = out + window.get( i ) + " ";
    }
    if ( active ) System.out.print( "\n" );
    return out;
  }

  public static void insertWord( String body, Long ts_minutos )
  {
    StringTokenizer st = new StringTokenizer( body );
    while( st.hasMoreTokens( ) )
    {
      String token = st.nextToken( );
      token = onlyWords( token );
      if ( stopwords.contains( token ) || token.length( ) <= 3 ) continue;
      if( ! words_serie.containsKey( token ) )  /* Si la palabra no existe entonces se inserta */
      {
        ArrayList< Integer > window = new ArrayList< Integer >( );
        window.add( 1 );
        initWord.put( token, ts_minutos );
        words_serie.put( token, window );
      }
      else  /* Si la palabra ya existe en memoria */
      {
        ArrayList < Integer > window =  words_serie.get( token );
        Long last = initWord.get( token );
        Integer anterior = window.size( ) - 1 ;
        last = last + Long.parseLong( anterior.toString( ) );
        Long diff = ts_minutos - last;
        if ( window.size( ) < largoSerie )  /*No se ha llenado la venatan movil */
        {
          if ( diff == 1 )  /*Preguntamos en que instante estamos*/
            window.add( 1 );
          else if ( diff == 0 )
            window.set( window.size( ) - 1 , window.get( window.size( ) - 1 ) + 1 );
        }
        else if ( window.size( ) == largoSerie )  /*Se lleno la ventana movil se debe sacar el primero y colocar al ultimo */
        {
          if ( diff == 1 )
          {
            window.remove( 0 );
            window.add( 1 );
            initWord.put( token, initWord.get( token ) + 1 ) ;
          }
          else if ( diff == 0 )
             window.set( largoSerie - 1 , window.get( largoSerie - 1 ) + 1 );
        }
      }
    }
  }

  public static void burstyDetection( double thresholdCV, double constantFrecuency, Long ts_minutos ) throws NoSuchProviderException, MessagingException
  {
    Iterator it = words_serie.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      double mean = 0;
      double std = 0;
      double cv = 0;
      ArrayList< Double > list = new ArrayList< Double >( );
      Map.Entry pair = (Map.Entry)it.next( );
      String wordi = ( String )  pair.getKey( );
      ArrayList< Integer > windowSerie = ( ArrayList< Integer > )pair.getValue( );
      if ( windowSerie.size( ) == largoSerie )
      {
        double currentFrecuency = ( double ) windowSerie.get( largoSerie - 1 );
        if ( currentFrecuency >= currentFrecuencyMin )
        {
          for ( int iw = 0; iw < windowSerie.size( ); iw++ )
          {
            double xx = ( double )windowSerie.get( iw );
            list.add( xx );
            mean+= xx;
          }
          mean= mean / windowSerie.size( ) ;
          mean = redondear( mean, 2 );
          for( int ij= 0; ij < list.size( ); ij++)
          {
            double y = list.get( ij );
            std+= Math.pow( y - mean, 2);
          }
          std = Math.sqrt( std / windowSerie.size( ) );
          std = redondear( std, 2 );
          cv = std / mean ;
          cv = redondear( cv, 2);
          String seriePrint= ImprimirSerie( wordi, ts_minutos, false );
          seriePrint = seriePrint + "\tavg : " + mean + "\tstd : " + std + "\tcv : " + cv;
          System.out.println( seriePrint );
          double thresholdFrecuency = mean + std * constantFrecuency;
          if ( cv >= thresholdCV && thresholdFrecuency <= currentFrecuency )
          {
            String correo = seriePrint + "\n";
            correo = correo + "RAFAGA : " + wordi ;
            burstyDay = burstyDay + correo + "\n";
            System.out.println( "RAFAGA : " + wordi + "\t\t");
            //sendEmail( "l.barreraabarca@uandresbello.edu" , "Rafaga " + termino , correo );
            //sendEmail( "carlos.gomez.pantoja@unab.cl", "Rafaga " +termino , correo );
          }
        }
				System.out.println( "Series Escribiendo : " + wordi + "\t\t");
				writeSerieTiempoMongoDB( wordi, ts_minutos, currentFrecuency, mean, std, cv );
      }
    }
  }

public static void writeSerieTiempoMongoDB( String palabra, Long ts_minutos, double frecuencia, double promedio, double desviacionEstandar,
                                              double coeficienteVariacion)
	{
		try
		{
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
			Object CollectionObject = getCollection.invoke( DBObject, "serietiempos" );
			/*Clase BasicDBObject*/
			Class classBasicDBObject = classloader.loadClass( "com.mongodb.BasicDBObject" );
			Constructor constructorBasicDBObject = classBasicDBObject.getConstructor( );
			Object BasicDBObject = constructorBasicDBObject.newInstance( );
			Method BasicPut = classBasicDBObject.getMethod( "append", String.class, Object.class );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "palabra", palabra );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "ts_minutos", ts_minutos );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "frecuencia", frecuencia );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "promedio", promedio );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "desviacionEstandar", desviacionEstandar );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "coeficienteVariacion", coeficienteVariacion );
			BasicDBObject = BasicPut.invoke( BasicDBObject, "conjuntoDato", conjuntoDato );

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

	/*
	public static void writeSerieTiempoMongoDB( String palabra, Long ts_minutos, double frecuencia, double promedio, double desviacionEstandar,
																							double coeficienteVariacion )
	{
		try
		{
			MongoClient mongoClient = new MongoClient("localhost");
    	DB db = mongoClient.getDB("DB_TUITS_ONLINE");
    	DBCollection collection = db.getCollection("serieTiempos");
   	 	BasicDBObject objectTuit = new BasicDBObject( );
    	objectTuit.put( "palabra", palabra );
    	objectTuit.put( "ts_minutos", Long.toString( ts_minutos ) );
   	 	objectTuit.put( "frecuencia", frecuencia );
    	objectTuit.put( "promedio", promedio );
   	 	objectTuit.put( "desviacionEstandar", desviacionEstandar );
    	objectTuit.put( "coeficienteVariacion", coeficienteVariacion );
    	collection.insert( WriteConcern.SAFE, objectTuit );
    	mongoClient.close();
		}
		catch( Exception e )
		{
			e.printStackTrace( );
		}
	}*/

  public static double redondear( double numero,int digitos )
  {
    int cifras=(int) Math.pow(10,digitos);
    return Math.rint(numero*cifras)/cifras;
  }

  public static String eliminarSignosPuntuacion( String text )
  {
    Pattern patron = Pattern.compile("[\"\u00A1!\u00BF?.+,;:'-_()`\u00A8&$%\u00AC~\u00B7]*");
    Matcher encaja = patron.matcher( text );
    String resultado = encaja.replaceAll("");
    return resultado;
  }

  public static String eliminarHttp( String text )
  {
    Pattern patron = Pattern.compile("http[a-z0-9/.?=:]*");
    Matcher encaja = patron.matcher( text );
    String resultado = encaja.replaceAll("");
    return resultado;
  }


  public static HashMap<String, Integer> CargarListadoProxies( String file_input ) throws IOException
  {
    HashMap<String, Integer> conjunto_proxies = new HashMap<String, Integer>();
    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    try
    {
      archivo = new File ( file_input  );
      fr = new FileReader ( archivo );
      br = new BufferedReader ( fr );
      String linea;
      while( ( linea = br.readLine( ) ) != null )
      {
        String[] parts = linea.split( " " );
        String proxy = parts[0];
        Integer puerto = Integer.parseInt( parts[1] );
        conjunto_proxies.put( proxy, puerto );
      }
    }
    catch( Exception e )
    {
      e.printStackTrace( );
    }
    fr.close( );
    return conjunto_proxies;
  }

	public static void cargarCuenta( String num_cta , ConfigurationBuilder cb, String file_cuentas ) throws IOException
	{
		try
    {
			File file = new File( file_cuentas );
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			String line;
			int bit = 0; //Variable para control de flujo
			while( (line = br.readLine()) != null )
			{
				if( bit > 0 )
				{
					if( bit == 1 ){ cb.setOAuthConsumerKey( line ); }
					if( bit == 2 ){ cb.setOAuthConsumerSecret( line ); }
					if( bit == 3 ){ cb.setOAuthAccessToken( line ); }
					if( bit == 4 ){ cb.setOAuthAccessTokenSecret( line ); break; }
					bit++;
				}
				if( line.compareTo( num_cta) == 0 ){ bit = 1; }
			}
			bit = 0;
      br.close();
		}catch( IOException e ){
			e.printStackTrace();
			return;
		}
	}

	static String[] cargarTerminos( String num_conjunto, String file_terminos ) throws IOException
	{
    ArrayList<String> terminos = new ArrayList<String>( );
    try{
      File file = new File( file_terminos );
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			String line;
			int bit = 0; //Variable para control de flujo
			while( (line = br.readLine()) != null )
			{
				if( bit > 0 )
				{
          StringTokenizer tokenizer = new StringTokenizer( line," " );
          while( tokenizer.hasMoreTokens( ) )
          {
            String token = tokenizer.nextToken( );
            terminos.add( token );
          }
          bit = 0;
				}
				if( line.compareTo( num_conjunto ) == 0 ){ bit = 1; }
			}
			bit = 0;
      br.close();
		}
    catch( IOException e )
    {
			e.printStackTrace();
		}
    return terminos.toArray( new String[terminos.size( )] );
	}

  public static String terminoDescarga( String numTerm, String archivoInput ) throws FileNotFoundException, IOException
  {
    String term="";
    BufferedReader br = null;
    try
    {
      br = new BufferedReader( new FileReader( new File( archivoInput ) ) );
      String linea = "";
      while( ( linea = br.readLine( ) ) != null)
      {
        String[ ] data = linea.split( " " );
        if( data[ 0 ].equals( numTerm ) )
          term = data[ 1 ];
      }
      br.close( );
    }
    catch ( IOException e)
    {
      e.printStackTrace( );
    }
    return term;
  }


  public static ArrayList< String > cargarStopWords( String archivoInput ) throws FileNotFoundException, IOException
  {
    ArrayList< String > stopWords = new ArrayList< String >( );
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
        stopWords.add( linea );
      }
      br.close( );
      fr.close( );
    }
    catch ( IOException e)
    {
      System.out.println( "Error al leer el archivo de stopwords" );
    }
    return stopWords;
  }

	public static ArrayList< String > getHashTags( JSONObject jsonObject ) throws JSONException
  {
		ArrayList < String > hashtags_words = new ArrayList < String >( );
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "entities" );
		JSONArray jsonHT   = ( JSONArray ) jsonUser.get( "hashtags" );

		for( int i = 0; i < jsonHT.length() ; i++ )
		//for ( int i = 0; i < jsonHT.size( ) ; i++ )
		{
			JSONObject jsonTmp = ( JSONObject ) jsonHT.get( i );
			String word_ = ( String ) jsonTmp.get( "text" );
			word_ = word_.toLowerCase( );
			word_ = cleanText( word_ );
			if( ! hashtags_words.contains( word_ ) )
				hashtags_words.add( word_ );
		}
		return hashtags_words;
  }

	public static ArrayList< String > getUserMentions( JSONObject jsonObject ) throws JSONException
  {
    ArrayList < String > hashtags_words = new ArrayList < String >( );
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "entities" );
    JSONArray jsonHT   = ( JSONArray ) jsonUser.get( "user_mentions" );

		for ( int i = 0; i < jsonHT.length( ); i++ )
    //for ( int i = 0; i < jsonHT.size( ) ; i++ )
    {
      JSONObject jsonTmp = ( JSONObject ) jsonHT.get( i );
      String word_ = ( String ) jsonTmp.get( "screen_name" );
      word_ = word_.toLowerCase( );
      if( ! hashtags_words.contains( word_ ) )
        hashtags_words.add( word_ );
    }
    return hashtags_words;
  }

	/* Obtiene el id de tuit*/
	public static Long idTuit( JSONObject jsonObject ) throws JSONException
	{
		Long id_tuit = ( Long ) jsonObject.get( "id" );
		return id_tuit;
	}

	public static String getLangTuit( JSONObject jsonObject ) throws JSONException
	{
		String lang  = ( String ) jsonObject.get( "lang" );
		return lang;
	}

  public static String userId( JSONObject jsonObject ) throws JSONException
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    String userID ="0" /*Long.parseLong( "0" )*/;
    userID = ( String ) jsonUser.get( "id_str" );
    return userID;
  }
	public static Long getRetuitCount( JSONObject jsonObject ) throws JSONException
  {
    int retuit_count = ( int ) jsonObject.get( "retweet_count" );
    return ( long ) retuit_count;
  }
	public static Long getFollowersCount( JSONObject jsonObject ) throws JSONException
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    int followers_count = 0;
    followers_count = ( int ) jsonUser.get( "followers_count" );
    return ( long ) followers_count;
  }

	public static String getTime_Zone( JSONObject jsonObject ) throws JSONException
	{
		JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
		String time_zone = "null_timeZone";
		/*if ( jsonUser.get( "time_zone" ) != null )
			time_zone = ( String ) jsonUser.get( "time_zone" ) ;
		else
			time_zone = "null_timeZone";*/
		return time_zone;
	}

	public static String getLocation( JSONObject jsonObject ) throws JSONException
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    String time_zone = ( String ) jsonUser.get( "location" ) ;
    return time_zone;
  }

  public static String rtUserID( JSONObject jsonObject ) throws JSONException
  {
    JSONObject jsonEntities = ( JSONObject ) jsonObject.get( "retweeted_status" );
    String idRT = "0";
    if ( jsonEntities != null )
      idRT = ( String ) jsonEntities.get( "id_str" );

    return idRT;
  }

	public static String ListarHashtags( ArrayList< String > ht ) throws JSONException
	{
		String output = "";
		for ( int i = 0 ; i < ht.size( ) ; i++ )
			output = output + ht.get( i ) + " ";
		if( output.length() == 0 ) output = "null_hashtag";
		return output;
	}

	public static String ListarUserMentions( ArrayList< String > ht )
  {
    String output = "";
    for ( int i = 0 ; i < ht.size( ) ; i++ )
      output = output + ht.get( i ) + " ";
    if( output.length() == 0 ) output = "null_userMentions";
    return output;
  }
}
