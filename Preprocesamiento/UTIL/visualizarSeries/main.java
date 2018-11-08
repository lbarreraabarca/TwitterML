import java.util.*; 
import java.io.*;

/*	En primer lugar generar las series de tiempo */
public class main 
{
	public static void main(String[] args)
  {
		if( args.length != 4 ) 
		{
			System.out.println( "Necesita 4 argumentos : Archivo de entrada con los datos\nArchivo con palabras de busqueda\nCarpeta de Salida\nArchivo salida del modelo");
			System.exit( 0 );
		}
		HashMap< String,Map< Long,Integer > > series = new HashMap< String, Map< Long ,Integer> >( );
		BufferedReader br = null, brVector = null;
		//PrintWriter pwVectores = null;
		//PrintWriter pwModelo   = null;
		String linea;
		ArrayList < Long > instantes_ts_minuto = new ArrayList < Long >( );
		try
		{
			br = new BufferedReader( new FileReader( new File ( args[ 0 ] ) ) );
			ArrayList < String > palabras_Busqueda = cargarBW( args[ 1 ] );
			if ( palabras_Busqueda.size( ) == 0 )
			{
				System.out.println( "Archivo con palabras de busqueda vacio. Favor ingresar una palabra al archivo");
     	 	System.exit( 0 );
			} 
			//System.out.println( palabras_Busqueda.get( 0 ) );
			//pwVectores = new PrintWriter( new FileWriter( new File( args[ 2 ] ) ) );
			//pwModelo   = new PrintWriter( new FileWriter( new File( args[ 3 ] ) ) );
			while( ( linea = br.readLine( ) ) != null )
			{
				String[] data = linea.split( "\t" );
				//System.out.println( data.length + " " + linea );
				if( data.length != 10 ) continue;
				instantes_ts_minuto.add( Long.parseLong( data[ 1 ] ) );
				StringTokenizer st = new StringTokenizer( data[ 8 ] );
				while( st.hasMoreTokens( ) )
				{
					String word = st.nextToken( );
					if( palabras_Busqueda.contains( word ) )
					{	
						if ( ! series.containsKey( word ) )
						{
							Map<Long,Integer> instante_frecuencia = new HashMap<Long,Integer>( );
							Long instante = Long.parseLong( data[ 1 ] );
							instante_frecuencia.put( instante, 1 );
							series.put( word, instante_frecuencia );
						}
						else
						{
							Map<Long, Integer > instante_frecuencia = series.get( word );
							Long instante = Long.parseLong( data[ 1 ] );
							if( ! instante_frecuencia.containsKey( instante ) )
								instante_frecuencia.put( instante, 1 );
							else
								instante_frecuencia.put( instante, instante_frecuencia.get( instante ) + 1 );
						}
					}
				}
			}	
			br.close( );

			Long upperBoundSerie = maximoSerie( instantes_ts_minuto );
			Long lowerBoundSerie = minimoSerie( instantes_ts_minuto );
			/* CORREGIR SERIES DE TIEMPO */
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
			
			/*OBTENER TENDENCIA ABSOLUTA*/
			Long sizeTW = Long.parseLong( "60" ); /* Tamano de las ventanas de tiempo. */
			analisisHora( series, lowerBoundSerie, upperBoundSerie, sizeTW, args[ 2 ], args[ 3 ] );
		}
		catch ( Exception e)
		{
			e.printStackTrace( );
		}
	}

	public static void analisisHora( 	HashMap < String, Map < Long, Integer > > series, Long lowerBoundSerie, Long upperBoundSerie, Long sizeTW,
																		String pathVectores, String pathModelo  )
	{

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
					System.out.println( "Y = " + m_lineal + "X + " + n_lineal );
					pwModelo.println( "m = " + m_lineal );
					pwModelo.println( "n = " + n_lineal );
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

	public static double redondearDecimales(double valorInicial, int numeroDecimales) 
	{
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
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
        {
          BW.add( linea );
        }
        
        br.close( );
        fr.close( );

      }
      catch ( IOException e)
      {
				
      }
      return BW;
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
}
