import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class main
{
	public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {
  	if ( args.length != 2 )
    {
    	System.out.println( "Error en la cantidad de argumentos " );
   	}
    else
    {
			BufferedReader br = null;
			PrintWriter pw = null;
			/* Cambiar por HashMap*/
			HashMap < Integer, String > precrisis  = new HashMap < Integer, String >( );
			HashMap < Integer, String > crisis     = new HashMap < Integer, String >( );
			HashMap < Integer, String > preludio   = new HashMap < Integer, String >( );
			HashMap < String, String > instancias = new HashMap < String, String >( );
      try
      {
				int contadorPrecrisis = 1;
				int contadorCrisis = 1;
				int contadorPreludio = 1;
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
				pw = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
				String linea = "";
				System.out.println( "Cargando instancias..." );
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( "\t" );
					if( data.length != 11 ) continue;
					String idTuit = data[ 0 ];
					String etiqueta = data[ 10 ];
					String text = data[ 8 ];
					if( etiqueta.equals( "NOC" ) ) 
					{	
						precrisis = insertarDatos( precrisis, contadorPrecrisis, idTuit );
						contadorPrecrisis++;
					}
					else if( etiqueta.equals( "CRI" ) ) 
					{
						crisis = insertarDatos( crisis, contadorCrisis, idTuit );
						contadorCrisis++;
					}
					/*else if( etiqueta.equals( "PSD" ) ) 
					{
						preludio = insertarDatos( preludio, contadorPreludio, idTuit );
						contadorPreludio++;
					}*/
					instancias.put( idTuit, text );
      	}
				System.out.println( "Obteniendo cota inferior..." );
				int lowerBound = 100;//obtenerMinimoClase( precrisis.size( ), crisis.size( ) , preludio.size( ) );
				double subconjunto = 0;
				
 				if( lowerBound >= 10000 )
				{	
					subconjunto = lowerBound * 0.1;
					lowerBound = ( int ) subconjunto;
				}
				
				System.out.println( "Seleccionando al azar PRECRISIS" );
				ArrayList < String> selectPrecrisis = tuitsSeleccionadosAzar( precrisis, lowerBound );
				System.out.println( "Seleccionando al azar CRISIS" );
				ArrayList < String> selectCrisis = tuitsSeleccionadosAzar( crisis, lowerBound );
				System.out.println( "Seleccionando al azar PRELUDIO" );
				//ArrayList < String> selectPreludio = tuitsSeleccionadosAzar( preludio, lowerBound );

				System.out.println( "Seleccionando al azar PRECRISIS" );
        ArrayList < String> selectPrecrisisLabel = tuitsSeleccionadosAzarLabel( precrisis, lowerBound, selectPrecrisis );
        System.out.println( "Seleccionando al azar CRISIS" );
        ArrayList < String> selectCrisisLabel = tuitsSeleccionadosAzarLabel( crisis, lowerBound, selectCrisis );
        System.out.println( "Seleccionando al azar PRELUDIO" );
        //ArrayList < String> selectPreludioLabel = tuitsSeleccionadosAzarLabel( preludio, lowerBound, selectPreludio );
				
				escribeSeleccionados( pw, selectPrecrisis, instancias, false, "" );
				escribeSeleccionados( pw, selectCrisis, instancias, false, "" );
				//escribeSeleccionados( pw, selectPreludio, instancias, false, "" );
				escribeSeleccionados( pw, selectPrecrisisLabel, instancias, true, "NOC" );
        escribeSeleccionados( pw, selectCrisisLabel, instancias, true, "CRI" );
        //escribeSeleccionados( pw, selectPreludioLabel, instancias, true, "PRELUDIO" );
			
        br.close( );
				pw.close( ); 
     	}
      catch ( Exception e)
      {
      	e.printStackTrace();
      }
   	}
	}

	public static void escribeSeleccionados( PrintWriter pwB, ArrayList < String > select, HashMap < String, String > data, 
																						Boolean printClass, String clase )
  {
		System.out.println( select.size( ) );
    for( int i =0 ; i < select.size( ) ; i++ )
			if ( printClass )
      	pwB.println( select.get( i ) + "\t" + data.get( select.get( i ) ) + "\t"  + clase );
			else
				pwB.println( select.get( i ) + "\t" + data.get( select.get( i ) ) );
  }


	public static ArrayList< String > tuitsSeleccionadosAzar( HashMap< Integer, String > datos , Integer lowerBound )
	{
		ArrayList < String > listado = new ArrayList < String >( );	
		ArrayList < Integer > posicionesElegidas = new ArrayList < Integer >( );
		int cantidadSeleccionados = lowerBound;
		if ( lowerBound != datos.size( ) )
		{
			while ( cantidadSeleccionados > 0 )
			{
				int numeroAzar = ( int )( Math.random( ) * datos.size( ) );
				while( posicionesElegidas.contains( numeroAzar ) )
					numeroAzar = ( int )( Math.random( ) * datos.size( ) );

				posicionesElegidas.add( numeroAzar );
				listado.add( datos.get( numeroAzar ) );
				cantidadSeleccionados--;
			}
		}
		else
		{
			Iterator it = datos.entrySet( ).iterator( );
    	while( it.hasNext( ) )
    	{
      	Map.Entry pair = ( Map.Entry ) it.next( );
      	Integer posicion = ( Integer ) pair.getKey( );
      	listado.add( datos.get( posicion ) );
			}
		}
		return listado;
	}

	public static ArrayList< String > tuitsSeleccionadosAzarLabel( HashMap< Integer, String > datos , Integer lowerBound, ArrayList < String > usados )
  {
    ArrayList < String > listado = new ArrayList < String >( );
    ArrayList < Integer > posicionesElegidas = new ArrayList < Integer >( );
    int cantidadSeleccionados = lowerBound;
    if ( lowerBound != datos.size( ) )
    {
      while ( cantidadSeleccionados > 0 )
      {

        int numeroAzar = ( int )( Math.random( ) * datos.size( ) );
        while( posicionesElegidas.contains( numeroAzar ) )
          numeroAzar = ( int )( Math.random( ) * datos.size( ) );

				if ( ! usados.contains( datos.get( numeroAzar ) ) )
				{
        	posicionesElegidas.add( numeroAzar );
        	listado.add( datos.get( numeroAzar ) );
        	cantidadSeleccionados--;
				}
      }
    }
    else
    {
      Iterator it = datos.entrySet( ).iterator( );
      while( it.hasNext( ) )
      {
        Map.Entry pair = ( Map.Entry ) it.next( );
        Integer posicion = ( Integer ) pair.getKey( );
				if ( ! usados.contains( datos.get( posicion ) ) )
        	listado.add( datos.get( posicion ) );
      }
    }
    return listado;
  }


	
	public static HashMap< Integer, String > insertarDatos( HashMap < Integer, String > datos, Integer posicion, String idTuit )
	{
		if( ! datos.containsKey( posicion ) )
			datos.put( posicion, idTuit );
		return datos;
	}
	
	public static ArrayList< String > insertarDatosCrisis( ArrayList< String > datos, String idTuit )
  {
    if( ! datos.contains( idTuit ) )
      datos.add( idTuit );
    return datos;
  }

	public static ArrayList< String > insertarDatosPreludio( ArrayList< String > datos, String idTuit )
  {
    if( ! datos.contains( idTuit ) )
      datos.add( idTuit );
    return datos;
  }

	
	public static Integer obtenerMinimoClase( Integer precrisis, Integer crisis, Integer preludio )
	{
		if( precrisis < crisis && precrisis < preludio ) return precrisis;
		else if ( crisis < precrisis && crisis < preludio ) return crisis;
		else return preludio;
	}

}




