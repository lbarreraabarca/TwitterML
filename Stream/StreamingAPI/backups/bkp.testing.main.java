import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
import java.io.*;
import java.util.*;
import twitter4j.json.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//Argumentos:
//0: número de cuenta (cuentas.dat)
//1: linea de terminos a leer (terminos.dat)
//2: path de escritura
//3: Nombre del archivo con los proxies
//4: Nombre del archivo de terminos
//5: Nombre de archivo de las cuentas
//6: Nombre de archivo de stopwords
//7: Tamaño de la serie de tiempo
//8: Umbral para determinar que es ráfaga y que no
//9: constante que acompana a la funcion mean + constante * std
//10: Frecuencia mínima en el instante actual
public class main {
  static Integer n = 0;
  static private final Object lock = new Object( );
  static Integer largoSerie;  // Largo de la ventana movil
  static String pr; // ip del proxy por el cual se escuchara
  static Integer pu;  // puerto asociado al proxy
  static Long lowerBound; //cota inferior de la hora de inicio de la descarga
  static ArrayList < String > stopwords;  //Lista con las palabras stopwords que deben ser eliminadas de los tweets.
  static HashMap<String, ArrayList < Integer > > words_serie; //Estructura de datos principal: almacena la palabra y su serie.
  static HashMap < String, Long > initWord; //almacena la palabra y el instante en el cual inicio la descarga de dicha palabra.
  static Double thresholdCV;  //Umbral para determinar que CV son ráfaga y cuales no.
  static Double constantSTD;  //constante que acompana a la funcion mean + constante * std
  static Integer currentFrecuencyMin;
  public static void main(String arg[]) throws IOException, JSONException, InterruptedException, Throwable
  {  
    HashMap<String,  ArrayList < Integer > >words_serie = new HashMap<String, ArrayList < Integer > >( );
    HashMap<String, Long > wordInit = new HashMap<String, Long>( );
    if( arg.length != 11 ) System.out.println( "Cantidad de Argumentos invalida" );
    else
    {
      System.out.println( "Cargando Stopwords..." );
      stopwords = cargarStopWords( arg[ 6 ] );  //Se carga las stopwords en memoria.
      largoSerie = Integer.parseInt( arg[ 7 ] );  //Se obtiene el largo de la ventana movil
      thresholdCV = Double.parseDouble( arg[ 8 ] );
      constantSTD = Double.parseDouble( arg[ 9 ] );
      currentFrecuencyMin = Integer.parseInt( arg[ 10 ] );
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
          objm.listenerData(proxy, puerto, arg[0], arg[5], arg[2], arg[4], arg[1], words_serie, wordInit);  //Comienza la dload
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
          Thread.sleep( 10000 );
        }
        System.out.println( "Proxies agotados..." );
      }
    }
  }
  
  public void listenerData( String proxy, Integer puerto, String n, String fileCuentas, 
                            String fileSalida, String fileTerminos, String nTerms, 
                            HashMap<String,  ArrayList < Integer > > ws, HashMap<String, Long > wordInitial)
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
    final FileWriter file = new FileWriter( file_out , true );
    TwitterStream ts = new TwitterStreamFactory( cb.build() ).getInstance();  //Configuracion http
    StatusListener listener;  
    pr  = proxy;  // Se inicializa pr con el proxy a descargar.
    pu = puerto;  // Se inicializa pu con el puerto asociado al proxy por el cual se descarga.
    words_serie = ws; // Se inicializa la estrcutura de datos principal con lo que viene por parametro de la funcion.
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
          /*try 
				  {
					  //file.write( jsonObject.toString() );  //Se escribe el tweet completo en formato JSON
            //file.write( "\n" );
          }
          catch( IOException e )
          {
		        e.printStackTrace( ); // Si hubo algún error en la escritura
          }*/
          
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
              Long ts_minutos = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 ));  //Se transforma la fecha a un numero
              String body = ( ( String )jsonObject.getString( "text" ) ).toLowerCase( );  //Se obtiene el texto del tweet
              body = eliminarHttp( body ); //Eliminan todas las referecnias a url.
              body = eliminarSignosPuntuacion( body );  //Eliminan signos de puntuacion.
              body = cleanText( body ); //Se eliminan tildes.
              Long deltaTime = ts_minutos - lowerBound; //Variable de control para saber si aumento en minuto
              file.write( body + "\t" + ts_minutos );
              file.write( "\n" );
              if ( deltaTime < 0 )
              {  
                lowerBound = ts_minutos;  //Se asigan el lowerbound como el primer tweet descargado
                System.out.println( lowerBound ); 
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
    String lang [] = {"es"};  //Se define que el lenguaje de descarga de los tweets deben ser en español
    fq.language( lang );   //SE setea el valor del filterQuery con lang.
    fq.track( keywords ); //Se setea las palabras claves al filterQuery
    ts.addListener( listener ); //Comienza el sistema que escucha el flujo de tweets que vienen.
    ts.filter( fq );
  }

//*********************************************************************************************
//Métodos estáticos:
//*********************************************************************************************
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
  
  public static String cleanText( String body )
  {
    body = body.replaceAll( "-|:|,|/|'|!|¿|¡|;|&|°|º|\\.|\\?|\\)|\\(|\\||\\*|%|=|¬", " " );
    body = body.replaceAll( "á", "a" );
    body = body.replaceAll( "é", "e" );
    body = body.replaceAll( "í", "i" );
    body = body.replaceAll( "ó", "o" );
    body = body.replaceAll( "ú", "u" );
    return body;
  }

  public static void recorrerMap( Long ts_minutos )
  {
    Iterator it = words_serie.entrySet( ).iterator( );
    while( it.hasNext( ) )
    {
      Map.Entry pair = (Map.Entry)it.next( );
      String wordi = ( String )  pair.getKey( );
      ImprimirSerie( wordi, ts_minutos, true ); 
    }
  }

  public static void ImprimirSerie( String word, Long ts_minutos, Boolean active )
  {
    System.out.print( word + "\t\t" + ts_minutos + "\t\t" );
    ArrayList < Integer > window = ( ArrayList < Integer > ) words_serie.get( word );
    for( int i = 0; i < window.size( ) ; i++ )
    {
      System.out.print( window.get( i ) + " " );
    }
    if ( active ) System.out.print( "\n" );
  }

  public static void insertWord( String body, Long ts_minutos )
  {
    StringTokenizer st = new StringTokenizer( body );
    while( st.hasMoreTokens( ) )
    {
      String token = st.nextToken( );
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

  public static void burstyDetection( double thresholdCV, double constantFrecuency, Long ts_minutos )
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
          ImprimirSerie( wordi, ts_minutos, false );
          System.out.println( "\tavg : " + mean + "\tstd : " + std + "\tcv : " + cv );
          double thresholdFrecuency = mean + std * constantFrecuency;
          if ( cv >= thresholdCV && thresholdFrecuency <= currentFrecuency )
          {
            System.out.println( "RAFAGA : " + wordi + "\t\t");
          }
        }
      }
    }
  }

  public static double redondear( double numero,int digitos )
  {
    int cifras=(int) Math.pow(10,digitos);
    return Math.rint(numero*cifras)/cifras;
  }

  public static String eliminarSignosPuntuacion( String text )
  {
    Pattern patron = Pattern.compile("[\"¡!¿?.+,;:'-_()`¨&$%¬~·]*");
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
		try{
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
}
