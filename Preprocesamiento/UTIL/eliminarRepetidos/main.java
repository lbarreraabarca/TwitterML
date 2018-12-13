import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class main
{
	public static void main( String[] args )
  {
    try
		{
			if( args.length != 2 )
			{
				System.out.println( "[ eliminarRepetidos ][ Error en la cantidad de argumentos. Debe ingresar un archivo de entrada y otro de salida. ]" );
				System.exit( 0 );
			}
			else
			{
				BufferedReader br = null;
				PrintWriter pw = null;
				try
				{
					br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
					pw = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
					ArrayList< String > data = new ArrayList< String >( );
					String line;
					System.out.println( "[ eliminarRepetidos ][ Load data ] " );
					while ( (line = br.readLine( ) ) != null )
					{
						//System.out.println( line ) ;
						if( ! data.contains( line ) )
							data.add( line );
					}
					br.close( );
					System.out.println( "[ eliminarRepetidos ][ Print output ]" );
					for ( int i = 0 ; i < data.size( ) ; i++ )
					{
						pw.println( data.get( i ) );
					}
					pw.close( );
				}
				catch (Exception  e )
				{
					e.printStackTrace();
				}
			}

		}
		catch (Exception  e )
		{
		  e.printStackTrace();
    }
	}
}
