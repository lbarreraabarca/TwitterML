import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class main
{
	public static void main( String[] args )
  {
  	Calendar calendar = Calendar.getInstance( );
    try
		{
			Scanner sc = new Scanner(System.in); 
      final String TWITTER="EEE, dd MMM yyyy HH:mm:ss Z"; // Formato de la fecha.
      SimpleDateFormat sdf = new SimpleDateFormat( TWITTER, Locale.ENGLISH );
      sdf.setLenient(true);
			String fecha;
			System.out.println( "Introduzca la Fecha" );
			fecha = sc.nextLine();
      String []data = fecha.split( " " ); // data[0] = Sun ; data[1] =Apr ; [2]=30; [3]=16:20:48; [4]=+0000 ; [5]=2017
      String postedTime = data[0] + ", " + data[2] + " " + data[1] + " " + data[ 5 ]+ " " + data[ 3 ] + " " + data[ 4 ];
      Date date = sdf.parse( postedTime );  //Se asigna la hora del tweet
      calendar.setTime( date ); // Se setea el valor en el calendario
      Long ts_minutos = new Long(calendar.getTimeInMillis( ) / ( 1000 * 60 * 60 *24 ));  //Se transforma la fecha a un numero
			System.out.println( "ts_minutos : " + ts_minutos );
		}
		catch (Exception  e )
		{
		  e.printStackTrace();
    }
	}
}
