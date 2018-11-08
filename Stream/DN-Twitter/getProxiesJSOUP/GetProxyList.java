import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.*;

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

    File tmp = null;
    PrintWriter bw = null;
    tmp = new File ( "./proxies.dat" );
    bw = new PrintWriter ( new FileWriter( tmp ) );
    Boolean res;
    res = getUSProxy( bw );
    if( res ) System.out.println( "http://www.us-proxy.org/ OK" );
    res = getUSProxyAr( bw );
    if( res ) System.out.println( "http://www.proxys.com.ar/ OK" );
    //res = getIncloak ( bw );
    //if( res ) System.out.println( "https://hidemy.name/en/proxy-list/ OK" );
	  res = getsocksProxy( bw );
    if( res ) System.out.println( "https://www.socks-proxy.net/ OK" );
	  res = getfreeProxy( bw );
  	if( res ) System.out.println( "https://free-proxy-list.net/ OK" );
    //res = getProxydb(bw) ; 
    //if ( res ) System.out.println( "http://proxydb.net/ OK" );

/*
    //TODO: por hacer
		OK:	
		getUSProxyAr (bw) (OK)
		getUSProxy (bw) (OK)
		getIncloak (bw) (OK)
		getsocksProxy(bw) (OK)
		getfreeProxy(bw); (OK)
		getProxydb(bw) (Revisar Estructura);

		NOK:
		gettorvpn(bw);	
		getfreeProxy(bw);
*/

    bw.close ();
  }

  public static Boolean getIncloak (PrintWriter bw) throws IOException
  {
    URL url = null;
    Document doc = null;

    try
    {
			Response res =
      	Jsoup.connect ("https://hidemy.name/es/proxy-list/").userAgent ("Mozilla").
      	timeout (5000).execute ();
      doc = res.parse ();
      /*url = new URL ("https://hidemy.name/es/proxy-list/");
      doc = Jsoup.parse (url, 3000);*/

    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1 al N-1)
    for (int i = 1; i < rows.size () - 1; i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
    	bw.write (ip);
    	bw.write (ip);
    	//System.out.println( ip );
    }
    return true;
  }

  public static Boolean getUSProxy (PrintWriter bw) throws IOException
  {
    Document doc = null;

    try
    {
      Response res =
      	Jsoup.connect ("http://www.us-proxy.org/").userAgent ("Mozilla").
      	timeout (5000).execute ();
      doc = res.parse ();
    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1 al N-1)
    for (int i = 1; i < rows.size () - 1; i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
    	bw.write (ip);
    	bw.write (ip);
    	//System.out.println( ip );
    	//pw.append( ip );
    }
    return true;
  }

  public static Boolean getUSProxyAr (PrintWriter bw) throws IOException
  {
    URL url = null;
    Document doc = null;

    try
    {
			Response res =
		    	Jsoup.connect ("http://www.proxys.com.ar/").userAgent ("Mozilla").
		    	timeout (5000).execute ();
		  doc = res.parse ();
			
     /* url = new URL ("http://www.proxys.com.ar/");
      doc = Jsoup.parse (url, 3000);*/
    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1)
    for (int i = 2; i < rows.size (); i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
    	//System.out.println( ip );
    	bw.write (ip);
    	bw.write (ip);
    	//pw.append( ip );
    }
    return true;
  }


  public static Boolean getProxydb(PrintWriter bw) throws IOException
  {
    Document doc = null;

    try
    {
      Response res =
      	Jsoup.connect ("http://proxydb.net/").userAgent ("Mozilla").
      	timeout( 5000 ).execute( );
      doc = res.parse( );
    }
    catch (Exception e)
    {
      System.out.println( e );
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get( 0 );
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1 al N-1)
    for (int i = 1; i < rows.size () - 1; i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
      String[ ] data = ip.split( " " ); 
      String[ ] ipport = data[ 0 ].split( ":" ) ;
      bw.write( ipport[ 0 ] + " " + ipport[ 1 ] );
    	bw.write( ipport[ 0 ] + " " + ipport[ 1 ] );
    }
    return true;
  }



	public static Boolean getfreeProxy (PrintWriter bw) throws IOException
  {
    Document doc = null;

    try
    {
      Response res =
      	Jsoup.connect ("https://free-proxy-list.net/").userAgent ("Mozilla").
      	timeout (5000).execute ();
      doc = res.parse ();
    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1 al N-1)
    for (int i = 1; i < rows.size () - 1; i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
    	bw.write (ip);
    	bw.write (ip);
    	//System.out.println( ip );
    	//pw.append( ip );
    }
    return true;
  }


	public static Boolean gettorvpn (PrintWriter bw) throws IOException
  {
    URL url = null;
    Document doc = null;

    try
    {

			Response res =
      	Jsoup.connect ("https://www.torvpn.com/en/proxy-list").userAgent ("Mozilla").
      	timeout (5000).execute ();
      doc = res.parse ();

/*      url = new URL ("https://www.torvpn.com/en/proxy-list");
      doc = Jsoup.parse (url, 10000);*/
    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1)
    for (int i = 1; i < rows.size (); i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text ();// + " " + cols.get (1).text () + "\n";
    	//System.out.println( ip );
    	bw.write (ip);
    	bw.write (ip);
    	//pw.append( ip );
    }
    return true;
  }



	public static Boolean getsocksProxy (PrintWriter bw) throws IOException
  {
    Document doc = null;

    try
    {
      Response res =
      	Jsoup.connect ("https://www.socks-proxy.net/").userAgent ("Mozilla").
      	timeout (5000).execute ();
      doc = res.parse ();
    }
    catch (Exception e)
    {
      System.out.println (e);
      e.printStackTrace( );
      return false;
    }

    ArrayList < String > downServers = new ArrayList <> ();
    Element table = doc.select ("table").get (0);
    Elements rows = table.select ("tr");

    //se recorren las filas de la tabla (desde la 1 al N-1)
    for (int i = 1; i < rows.size () - 1; i++)
    {
    	Element row = rows.get (i);	//se obtiene la fila "i"
    	Elements cols = row.select ("td");	//se obtiene columnas de esa fila
    	String ip = cols.get (0).text () + " " + cols.get (1).text () + "\n";
    	bw.write (ip);
    	bw.write (ip);
    	//System.out.println( ip );
    	//pw.append( ip );
    }
    return true;
  }


}

