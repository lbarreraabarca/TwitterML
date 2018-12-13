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
    	System.out.println( "[ divideTrainTestSVM ][ Error en la cantidad de argumentos ]" );
   	}
    else
    {
			BufferedReader br = null;
			PrintWriter pwTrain = null;
			PrintWriter pwTest = null;
			ArrayList< String > noCrisis = new ArrayList< String >( );
			ArrayList< String > crisis    = new ArrayList< String >( );
			ArrayList< String > trainData = new ArrayList< String >( );
			ArrayList< String > testData = new ArrayList< String >( );
      try
      {
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
				pwTrain = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
				pwTest = new PrintWriter( new FileWriter( new File( args[ 2 ] ) ) );

				String linea = "";
				System.out.println( "[ divideTrainTestSVM ][ Load Data ]" );
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( ";" );
					if( data.length != 2 ) continue;
					String etiqueta = data[ 0 ];
					String caracteristicas = data[ 1 ];
					if( etiqueta.equals( "-1" ) )
					{
						noCrisis = insertarDatos( noCrisis, linea );
					}
					else if( etiqueta.equals( "1" ) )
					{
						crisis = insertarDatos( crisis, linea );
					}
      	}

				double percentTrain = Double.parseDouble( args[ 3 ] );
				int tamCrisis = noCrisis.size( );
				int tamNoCrisis = crisis.size( );

				distribuirTrainTest( crisis, noCrisis, trainData, testData, percentTrain );

				System.out.println( "[ divideTrainTestSVM ][ Main TrainData : " + trainData.size( ) + " ]" );
				System.out.println( "[ divideTrainTestSVM ][ Main TestData  : " + testData.size( ) + " ]" );

				escribeSeleccionados( pwTrain, trainData );
				escribeSeleccionados( pwTest, testData );
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

	public static double redondear( double numero,int digitos ){
		int cifras=(int) Math.pow(10,digitos);
		return Math.rint(numero*cifras)/cifras;
	}

	public static void distribuirTrainTest( 	ArrayList< String > crisis, ArrayList< String > noCrisis, ArrayList< String > trainData, ArrayList< String > testData, double percentTrain  )
	{
		double cantidadTrainC		=	redondear((crisis.size()*percentTrain), 0)	;
		double cantidadTestC		=	redondear((crisis.size()*(1 - percentTrain)), 0) ;
		double cantidadTrainNC	=	redondear(noCrisis.size()*percentTrain,0) ;
		double cantidadTestNC		=	redondear((noCrisis.size()*(1 - percentTrain)), 0) ;

		int conjunto, posicion;

		while ( crisis.size( ) > 0 ){
			conjunto = (int) (Math.random() * 1);
			posicion = (int) (Math.random() * crisis.size( ) );

			if ( conjunto == 0 ){
					if( cantidadTrainC > 0){
						trainData.add( crisis.get( posicion ) );
						cantidadTrainC--;
					}else{
						testData.add( crisis.get( posicion ) );
						cantidadTestC--;
					}
			}
			else{
				if( cantidadTestC > 0 ){
						testData.add( crisis.get( posicion ) );
						cantidadTestC--;
				}else{
					trainData.add( crisis.get( posicion ) );
					cantidadTrainC--;
				}
			}
			crisis.remove( posicion );
		}

		while ( noCrisis.size( ) > 0 ){
			conjunto = (int) (Math.random() * 1);
			posicion = (int) (Math.random() * noCrisis.size( ) );

			if ( conjunto == 0 ){
					if( cantidadTrainNC > 0){
						trainData.add( noCrisis.get( posicion ) );
						cantidadTrainNC--;
					}else{
						testData.add( noCrisis.get( posicion ) );
						cantidadTestNC--;
					}
			}
			else{
				if( cantidadTestNC > 0 ){
						testData.add( noCrisis.get( posicion ) );
						cantidadTestNC--;
				}else{
					trainData.add( noCrisis.get( posicion ) );
					cantidadTrainNC--;
				}
			}
			noCrisis.remove( posicion );
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


	public static Integer obtenerMinimoClase( Integer noCrisis, Integer crisis, Integer preludio )
	{
		if( noCrisis < crisis /*&& noCrisis < preludio*/ ) return noCrisis;
		else if ( crisis < noCrisis /*&& crisis < preludio*/ ) return crisis;
		return 0;
		//else return preludio;
	}

}
