import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
import java.io.*;
import java.util.*;
import twitter4j.json.*;
import java.io.BufferedWriter;
//Argumentos:
//0: archivo de proxies
//1: archivo de salida
//2: archivo de cuenta
//3: ok_proxies viejo
public class main{
  static Integer n = 0;
	static Integer cta_prueba = 0;
  public static void main(String arg[]) throws IOException, JSONException, InterruptedException
  {
	  File fdrop = null;
    try
    {

      fdrop = new File( arg[1] );
      fdrop.delete( );
      
      if( arg.length == 4 )
      {
        Integer cantProxiesOK=0;
        //cargar el listado de proxies
        HashMap<String,Integer> proxies = CargarListadoProxies( arg[ 0 ] );
        HashMap<String,Integer> proxies_viejos = CargarListadoProxies( arg[ 3 ] );
        ArrayList < String > proxiesOK = new ArrayList < String >( );
        String proxy = null;
        Integer puerto = -1;
        Boolean res = false;
		    //PrintWriter pw = new PrintWriter( new FileWriter( arg[ 1 ], true ) );
        //testear los N proxies
        for( Map.Entry<String,Integer> entry : proxies.entrySet( ) )
        {
          proxy = entry.getKey( );
          puerto = entry.getValue( );
          System.out.println( "------------------------" );
          System.out.println( "Testeando Proxy=" + proxy + ":" + puerto );
          main objm = new main( );
          //espera por 2 segundos
          Thread.sleep(3000);
          long start_time = System.nanoTime();
          res = objm.testingProxy( arg[2], proxy, puerto );
          long end_time = System.nanoTime();
          Integer difference = Integer.valueOf( (int)( ( end_time - start_time )/1e6 ) );
          if( res )
          {
            Integer puertoOld = proxies_viejos.get( proxy );
			      if( !proxies_viejos.containsKey( proxy ) &&  puertoOld != puerto )
			      {
              cantProxiesOK++;
              //System.out.println( "OK=" + proxy + " " + puerto + " " + difference + " " + cantProxiesOK );
              //pw.println( proxy + " " + puerto + " " + difference );
              proxiesOK.add( proxy + " " + puerto + " " + difference );
              System.out.println( "OK=" + proxy + " " + puerto + " " + difference + " " + cantProxiesOK + " " + proxiesOK.size( ) );
            }
          }
          else
          {
            System.out.println( "NOK=" + proxy );
          }
        }
        imprimirProxiesOK( proxiesOK, arg[ 1 ] );
      }
      else
      {
        
        System.out.println("Cantidad de argumentos distinto de 2");
      }
    }
    catch( Exception e )
    {
      System.out.println( "EXCEPTION" );
      e.printStackTrace( );
    }



  }


  public static void imprimirProxiesOK( ArrayList < String > proxiesOK, String argFile ) throws IOException
  {
    PrintWriter pw = new PrintWriter( new FileWriter( new File( argFile ) ) );
    for ( int i = 0; i < proxiesOK.size( ) ; i++ )
      pw.println( proxiesOK.get( i ) );
    pw.close( );
  }

  Boolean flag = false;
  private final Object lock = new Object();

  public Boolean testingProxy( String file_cta, String proxy, Integer puerto ) throws Exception
  {
		ConfigurationBuilder cb = new ConfigurationBuilder( );
		cb.setDebugEnabled(true);
		cb.setJSONStoreEnabled(true);

		//configurar el proxy
		cb.setHttpProxyHost( proxy );
		cb.setHttpProxyPort( puerto );

		//cargar la cuenta
		cargarCuenta( file_cta , cb );

		//iniciar la descarga
		final TwitterStream ts = new TwitterStreamFactory( cb.build() ).getInstance();
		StatusListener listener;
		
		listener = new StatusListener( )
		{
				@Override
				public void onStatus( Status status )
				{
					flag = true;
					System.out.println( "\t\tRECIBIENDO" );
					synchronized( lock )
					{
						lock.notify( );
					}
				}

				@Override
				public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
				{
		  		}
				@Override
				public void onTrackLimitationNotice(int numberOfLimitedStatuses)
				{
				}

				@Override
				public void onScrubGeo(long userId, long upToStatusId)
				{
				}

				@Override
				public void onStallWarning(StallWarning warning)
				{
				}

				@Override
				public void onException( Exception ex ) 
				{
					//ex.printStackTrace( );
					
					synchronized( lock ) 
					{
					  System.out.println( "\t\tPROBLEMA ");
					  lock.notify( );
					  Thread.currentThread().stop( );
					}
				}
		};
		ts.addListener( listener );
		ts.sample( );
		try 
		{
			synchronized( lock )
		  	{
		    	lock.wait( 10000 );
		    	if( flag )
		      		System.out.println( "OK!" );
		    	else
		      		System.out.println( "NOK!" );
		  	}
		}
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			//System.out.println("Se esta cayendo en InterruptedException entre el OK.8 y OK.9");
			e.printStackTrace();
		}

		System.out.println( "\t\tRETORNANDO TESTING..." );
		ts.cleanUp( );
		ts.shutdown( );
		ts.clearListeners( );

		return flag;
  }

//*********************************************************************************************
//Métodos estáticos:
//*********************************************************************************************
  public static HashMap<String, Integer> CargarListadoProxies( String file_input ) throws IOException
  {
    HashMap<String, Integer> conjunto_proxies = new HashMap<String, Integer>();
    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;
    System.out.println("file input : "+file_input);
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
    finally
    {
      if( fr != null )
      {
        fr.close( );
      }
    }

    return conjunto_proxies;
  }

	public static void cargarCuenta( String file_cta , ConfigurationBuilder cb ) throws IOException
	{
		try
		{
			File file = new File( file_cta );
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			String line;		
			if ( cta_prueba == 0 )
			{
				line = br.readLine( );
				cb.setOAuthConsumerKey( line );
				System.out.println( "Cuenta : " + line );
				
				line = br.readLine( );
		  	cb.setOAuthConsumerSecret( line );
				
				line = br.readLine( );
		  	cb.setOAuthAccessToken( line );
				
				line = br.readLine( );
		  	cb.setOAuthAccessTokenSecret( line );

				cta_prueba++;
			}
			else if ( cta_prueba == 1 )
			{
				for( int i = 0 ; i < 4 ; i++ )
					line = br.readLine( );

				line = br.readLine( );
        cb.setOAuthConsumerKey( line );
        System.out.println( "Cuenta : " + line );
        
        line = br.readLine( );
        cb.setOAuthConsumerSecret( line );
        
        line = br.readLine( );
        cb.setOAuthAccessToken( line );
        
        line = br.readLine( );
        cb.setOAuthAccessTokenSecret( line ); 
				
				cta_prueba++;
			}
			else if ( cta_prueba == 2 )
      {
        for( int i = 0 ; i < 8 ; i++ )
          line = br.readLine( );

        line = br.readLine( );
        cb.setOAuthConsumerKey( line );
        System.out.println( "Cuenta : " + line );

        line = br.readLine( );
        cb.setOAuthConsumerSecret( line );

        line = br.readLine( );
        cb.setOAuthAccessToken( line );

        line = br.readLine( );
        cb.setOAuthAccessTokenSecret( line );

        cta_prueba++;
      }
			else if ( cta_prueba == 3 )
      {
        for( int i = 0 ; i < 12 ; i++ )
          line = br.readLine( );

        line = br.readLine( );
        cb.setOAuthConsumerKey( line );
        System.out.println( "Cuenta : " + line );

        line = br.readLine( );
        cb.setOAuthConsumerSecret( line );

        line = br.readLine( );
        cb.setOAuthAccessToken( line );

        line = br.readLine( );
        cb.setOAuthAccessTokenSecret( line );

        cta_prueba = 0;
      }
		}
    catch( IOException e )
    {
			e.printStackTrace();
			return;
		}
	}
}
