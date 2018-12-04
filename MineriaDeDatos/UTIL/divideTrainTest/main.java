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
  	if ( args.length != 4 )
    {
    	System.out.println( "Error en la cantidad de argumentos " );
   	}
    else
    {
			BufferedReader br = null;
			PrintWriter pwTrain = null;
			PrintWriter pwTest = null;
			ArrayList< String > precrisis = new ArrayList< String >( );
			ArrayList< String > crisis    = new ArrayList< String >( );
			//ArrayList< String > preludio  = new ArrayList< String >( );
			/* Cambiar por HashMap*/
			//HashMap < Integer, String > precrisis  = new HashMap < Integer, String >( );
			//HashMap < Integer, String > crisis     = new HashMap < Integer, String >( );
			//HashMap < Integer, String > preludio   = new HashMap < Integer, String >( );
      try
      {
				//int contadorPreludio = 1;
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
				pwTrain = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
				pwTest = new PrintWriter( new FileWriter( new File( args[ 2 ] ) ) );
				String linea = "";
				System.out.println( "Cargando instancias..." );
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( ";" );
					if( data.length != 2 ) continue;
					//String idTuit = data[ 0 ];
					String etiqueta = data[ 0 ];
					String caracteristicas = data[ 1 ];
					if( etiqueta.equals( "-1" ) )
					{
						precrisis = insertarDatos( precrisis, linea );
					}
					else if( etiqueta.equals( "1" ) )
					{
						crisis = insertarDatos( crisis, linea );
					}
      	}

				System.out.println( args[ 3 ] );
				double percentTrain = Double.parseDouble( args[ 3 ] );
				double percentTest = 1 - percentTrain;
				System.out.println( "NOC : " + precrisis.size( ) );
				System.out.println( "CRI : " + crisis.size( ) );
				System.out.println( "percentTrain : " + percentTrain );
				System.out.println( "percentTest : " + percentTest  );


				//ArrayList < String> selectPrecrisis = tuitsSeleccionadosAzar( precrisis, lowerBound );

				//ArrayList < String> selectCrisis = tuitsSeleccionadosAzar( crisis, lowerBound );
				//System.out.println( "Seleccionando al azar PRELUDIO" );
				//ArrayList < String> selectPreludio = tuitsSeleccionadosAzar( preludio, lowerBound );

				//escribeSeleccionados( pwTrain, selectPrecrisis );
				//escribeSeleccionados( pwTrain, selectCrisis );
				//escribeSeleccionados( pwTrain, selectPreludio );

        br.close( );
				pwTrain.close( );
				pwTest.close( );
     	}
      catch ( Exception e)
      {
      	e.printStackTrace();
      }
   	}
	}

	public static void escribeSeleccionados( PrintWriter pwB, ArrayList < String > select )
  {
    for( int i =0 ; i < select.size( ) ; i++ )
      pwB.println( select.get( i ) );
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

	// public static HashMap< Integer, String > insertarDatos( HashMap < String, String > datos, Integer posicion)
	// {
	// 	if( ! datos.containsKey( posicion ) )
	// 		datos.put( posicion, idTuit );
	// 	return datos;
	// }

	public static ArrayList< String > insertarDatos( ArrayList< String > datos, String feature )
  {
    if( ! datos.contains( feature ) )
      datos.add( feature );
    return datos;
  }


	public static Integer obtenerMinimoClase( Integer precrisis, Integer crisis, Integer preludio )
	{
		if( precrisis < crisis /*&& precrisis < preludio*/ ) return precrisis;
		else if ( crisis < precrisis /*&& crisis < preludio*/ ) return crisis;
		return 0;
		//else return preludio;
	}

}
