import java.io.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class main
{
	static HashMap<String, HashMap < Long, Integer > > words_serie;
	public static void main( String[] args )
  {
		if( args.length != 3 )	System.exit( 0 );
		words_serie = new HashMap<String, HashMap < Long, Integer > >( );
		BufferedReader br;
		PrintWriter pw;
		JSONParser parser = new JSONParser( );
  	Calendar calendar = Calendar.getInstance( );
    try
		{
			ArrayList < String > _hashTags = new ArrayList< String >( );
      final String TWITTER="EEE, dd MMM yyyy HH:mm:ss Z"; // Formato de la fecha.
      SimpleDateFormat sdf = new SimpleDateFormat( TWITTER, Locale.ENGLISH );
      sdf.setLenient(true);
      br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
			pw = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
			HashMap < String, String > stopwords = cargarBW( args[ 2 ] );
			String line;
      while ( (line = br.readLine( ) ) != null )
			{
      	Object obj = parser.parse( line );
        JSONObject jsonObject = (JSONObject)obj;
        String objTime = ( String )jsonObject.get( "created_at" );
        if( objTime != null )
        {
					if ( ! getLangTuit( jsonObject ).equals( "es" ) ) continue;
          String []data = objTime.split( " " ); // data[0] = Sun ; data[1] =Apr ; [2]=30; [3]=16:20:48; [4]=+0000 ; [5]=2017
          String postedTime = data[0] + ", " + data[2] + " " + data[1] + " " + data[ 5 ]+ " " + data[ 3 ] + " " + data[ 4 ];
          Date date = sdf.parse( postedTime );  //Se asigna la hora del tweet
          calendar.setTime( date ); // Se setea el valor en el calendario
          Long ts_minutos = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 ));  //Se transforma la fecha a un numero
          String body = ( ( String )jsonObject.get( "text" ) ).toLowerCase( );  //Se obtiene el texto del tweet
          body = eliminarHttp( body ); //Eliminan todas las referecnias a url.
          body = eliminarSignosPuntuacion( body );  //Eliminan signos de puntuacion.
          body = cleanText( body ); //Se eliminan tildes.
          body = eliminarSaltoLinea( body );
					body = eliminarTabulador( body );
					//System.out.println( body );
					body = onlyWords( body );
					//System.out.println( "-------" );
					body = eliminarTextoBasura( body, stopwords );
					Long tuitID = idTuit( jsonObject );
					String ht_list = ListarHashtags( getHashTags( jsonObject ) );
					String user_mentions = ListarUserMentions( getUserMentions( jsonObject ) );
					pw.println( tuitID + "\t" + ts_minutos + "\t" + ht_list + "\t" + user_mentions + "\t" +
					getLocation( jsonObject ) + "\t" + getTime_Zone( jsonObject ) + "\t" + getFollowersCount( jsonObject ) + "\t" +
					getRetuitCount( jsonObject )  + "\t" + body + "\t" + objTime );
        }
			}
			br.close( );
			pw.close( );
			// for ( int i = 0 ; i < _hashTags.size( ) ; i++ )
			// {
				// System.out.println( _hashTags.get( i ) );
			// }
		}
		catch (Exception  e )
		{
		  e.printStackTrace();
    }
	}


  public static String eliminarSignosPuntuacion( String text )
  {
    Pattern patron = Pattern.compile("[\"\u00A1!\u00BF?.+,;:'-_()`\u00A8&$%\u00AC~\u00B7]*");
    Matcher encaja = patron.matcher( text );
    String resultado = encaja.replaceAll("");
    return resultado;
  }


	public static String ListarHashtags( ArrayList< String > ht )
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

  public static String eliminarHttp( String text )
  {
    Pattern patron = Pattern.compile("ht[a-z0-9/.?=:]*");
    Matcher encaja = patron.matcher( text );
    String resultado = encaja.replaceAll("");
    return resultado;
  }

	public static ArrayList< String > getHashTags( JSONObject jsonObject )
  {
		ArrayList < String > hashtags_words = new ArrayList < String >( );
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "entities" );
		JSONArray jsonHT   = ( JSONArray ) jsonUser.get( "hashtags" );

		for ( int i = 0; i < jsonHT.size( ) ; i++ )
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

	public static ArrayList< String > getUserMentions( JSONObject jsonObject )
  {
    ArrayList < String > hashtags_words = new ArrayList < String >( );
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "entities" );
    JSONArray jsonHT   = ( JSONArray ) jsonUser.get( "user_mentions" );

    for ( int i = 0; i < jsonHT.size( ) ; i++ )
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
	public static Long idTuit( JSONObject jsonObject )
	{
		Long id_tuit = ( Long ) jsonObject.get( "id" );
		return id_tuit;
	}

	public static String getLangTuit( JSONObject jsonObject )
	{
		String lang  = ( String ) jsonObject.get( "lang" );
		return lang;
	}

  public static String userId( JSONObject jsonObject )
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    String userID ="0" /*Long.parseLong( "0" )*/;
    userID = ( String ) jsonUser.get( "id_str" );
    return userID;
  }
	public static Long getRetuitCount( JSONObject jsonObject )
  {
    Long retuit_count = ( Long ) jsonObject.get( "retweet_count" );
    return retuit_count;
  }
	public static Long getFollowersCount( JSONObject jsonObject )
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    Long followers_count = Long.parseLong( "0" );
    followers_count = ( Long ) jsonUser.get( "followers_count" );
    return followers_count;
  }

	public static String getTime_Zone( JSONObject jsonObject )
	{
		JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
		String time_zone = ( String ) jsonUser.get( "time_zone" ) ;
		if ( time_zone == null ) time_zone = "null_timeZone";
		return time_zone;
	}

	public static String getLocation( JSONObject jsonObject )
  {
    JSONObject jsonUser = ( JSONObject ) jsonObject.get( "user" );
    String time_zone = ( String ) jsonUser.get( "location" ) ;
    return time_zone;
  }

  public static String rtUserID( JSONObject jsonObject )
  {
    JSONObject jsonEntities = ( JSONObject ) jsonObject.get( "retweeted_status" );
    String idRT = "0";
    if ( jsonEntities != null )
      idRT = ( String ) jsonEntities.get( "id_str" );

    return idRT;
  }


	public static String onlyWords( String text )
	{
		StringTokenizer st = new StringTokenizer( text );
		String salida = "";
		while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( );
			word = word.replaceAll( "[^a-z]" , "");
			if ( word.length() > 3 ) salida = salida + word + " ";
			//if ( word.length() > 15 ) System.out.println( word );
		}
		return salida;
	}

	public static String eliminarTextoBasura( String text, HashMap < String, String > stopwords  )
	{
		StringTokenizer st = new StringTokenizer( text );
    String salida = "";
    while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( );
			if( ! stopwords.containsKey( word ) )
			{
					salida = salida + word + " ";
			}
			else
			{
				if( ! stopwords.get( word ).equals( "1" ) )
          salida = salida + stopwords.get( word )  + " ";
			}
		}
		return salida;
	}

  public static String cleanText( String body )
  {
    StringTokenizer st = new StringTokenizer( body );
		String salida = "";
		while( st.hasMoreTokens( ) )
    {
      String word = st.nextToken( );
    	word = word.replaceAll( "-|:|,|\/|'|!|\u00BF|\u00A1|;|&|\u00B0|\u00BA|\\\\.|\\\\?|\\\\)|\\\\(|\\\\||\\\\*|%|=|\u00AC", " " );
    	word = word.replaceAll( "\u00E1", "a" );
    	word = word.replaceAll( "\u00E9", "e" );
    	word = word.replaceAll( "\u00ED", "i" );
    	word = word.replaceAll( "\u00F3", "o" );
    	word = word.replaceAll( "\u00FA", "u" );
			word = eliminarSignosPuntuacion( word );
			salida = salida + word + " ";
		}
    return salida;
  }

  public static String eliminarSaltoLinea( String text )
  {
    text = text.replaceAll( "[\n]*", "");
    return text;
  }

	public static String eliminarTabulador( String text )
  {
    text = text.replaceAll( "[\t]*", "");
    return text;
  }

	public static HashMap<String, String> cargarBW( String archivoInput ) throws FileNotFoundException, IOException
  {
    HashMap<String, String> BW = new HashMap<String, String>( );
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
				String[] split = linea.split( "\t" );
				if ( split.length  == 2 )
				{
					//System.out.println( "1) " + split[ 0 ] + "\t2)" +  split[ 1 ]);
					BW.put( split[ 0 ], split[ 1 ] );
				}
			}

      br.close( );
      fr.close( );

    }
    catch ( IOException e)
    {
      /* Manejo de Excepciones. */
    }

    return BW;
  }


}
