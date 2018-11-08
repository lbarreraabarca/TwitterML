import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection.Response;
import org.jsoup.select.Elements;

public class GetProxyList
{
  public static void main (String args[]) throws IOException
  {
		if( args.length != 2 )
		{
			System.out.println( "Error en la cantidad de argumentos..." );
			System.exit( 0 );
		}
		BufferedReader br = null;// = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
		PrintWriter pw = null;
		ArrayList < String > listado = new ArrayList < String >( );
		try
		{
			String []obtenerVerbo = args[ 0 ].split( "/" );
			String tmpVerbo = obtenerVerbo[ obtenerVerbo.length - 1 ]; 
			String verbo = tmpVerbo.substring( 0, tmpVerbo.length( ) - 5 );
			br  = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) );
			pw = new PrintWriter ( new FileWriter( new File( args[ 1 ]  + verbo + ".conjugados") ) );
			String linea, datahtml = "";
			while( ( linea = br.readLine( ) ) != null )
			{
				datahtml =  datahtml  + linea + "\n" ;
			}
			listado = getVerbos( listado, datahtml, verbo );
			for ( int kk =  0; kk < listado.size( ) ; kk++ )
				pw.println( listado.get( kk ) );
			br.close( );
    	pw.close( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
  }

  public static ArrayList< String > getVerbos( ArrayList < String > listado, String html, String verboInf ) throws IOException
  {
    Document doc = null;
    try
    {
      doc = Jsoup.parse( html );
    }
    catch (Exception e)
    {
      e.printStackTrace( );
    }
				
		Elements body = doc.select( "body" );
		for( int i = 0 ; i < body.size( ) ; i++ )
		{
			Elements font = body.select( "font" );
			for( int j = 0 ; j < font.size( ) ; j++ )
			{
				Elements ielem = font.select( "i" );
				for ( int k  = 0; k < ielem.size( ) ; k++ )
				{
					String verboConjugado = ielem.get( k ).toString( );
					verboConjugado = eliminarTags( verboConjugado );
					verboConjugado = cleanText( verboConjugado );
					String []comprobar = verboConjugado.split( " " );
					if ( comprobar.length == 1 ) 
					{	
						if ( verboConjugado.length( ) > 2 )
							if ( verboInf.substring( 0, 1 ).equals( verboConjugado.substring(0 , 1 ) ) )
							{	
								String tmpInsert = verboInf  + "\t" + verboConjugado;
								if ( ! listado.contains( tmpInsert ) )
									listado.add( tmpInsert );
							}		
					}
				}
			}
		}
		return listado;
  }
	
	public static String cleanText( String word )
  {
      word = word.replaceAll( "-|:|,|/|'|!|¿|¡|;|&|°|º|\\.|\\?|\\)|\\(|\\||\\*|%|=|¬", " " );
      word = word.replaceAll( "á", "a" );
      word = word.replaceAll( "é", "e" );
      word = word.replaceAll( "í", "i" );
      word = word.replaceAll( "ó", "o" );
      word = word.replaceAll( "ú", "u" );
    	return word;
  }
	
	public static String eliminarTags( String text )
  {
    String resultado = text.replaceAll("<[^>]*>", "");
    return resultado;
  }

}




