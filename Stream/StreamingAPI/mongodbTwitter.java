import java.util.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class mongodbTwitter
{

	private int estado;

	public mongodbTwitter( )
	{
		this.estado = 0;
	}
	
	public static void writeTweetsMongoDB( Long idTuit, Long ts_minutos, String hashtags, String userMentions, String timeZone, Long followersCount,
                                          Long retweetCount, String text, String horaUTC, String etiqueta, Class clase)
  {
    try
    {
      MongoClient mongoClient = new MongoClient("localhost");
      DB db = mongoClient.getDB("DB_TUITS_ONLINE");
      DBCollection collection = db.getCollection("tuits");
      BasicDBObject objectTuit = new BasicDBObject( );
      objectTuit.append( "idTuit", idTuit );
      objectTuit.append( "ts_minutos", Long.toString( ts_minutos ) );
      objectTuit.append( "hashtags", hashtags );
      objectTuit.append( "userMentions", userMentions );
      objectTuit.append( "timeZone", timeZone );
      objectTuit.append( "followersCount", followersCount );
      objectTuit.append( "retweetCount", retweetCount );
      objectTuit.append( "text", text );
      objectTuit.append( "horaUTC", horaUTC );
      objectTuit.append( "etiqueta", etiqueta );
      collection.insert( WriteConcern.SAFE, objectTuit );
      mongoClient.close();
    }
    catch (Exception  e )
    {
      e.printStackTrace();
    }
  }


	public static void writeSerieTiempoMongoDB( String palabra, Long ts_minutos, double frecuencia, double promedio, double desviacionEstandar,
                                              double coeficienteVariacion )
  {
    try
    {
      MongoClient mongoClient = new MongoClient("localhost");
      DB db = mongoClient.getDB("DB_TUITS_ONLINE");
      DBCollection collection = db.getCollection("serieTiempos");
      BasicDBObject objectTuit = new BasicDBObject( );
      objectTuit.put( "palabra", palabra );
      objectTuit.put( "ts_minutos", Long.toString( ts_minutos ) );
      objectTuit.put( "frecuencia", frecuencia );
      objectTuit.put( "promedio", promedio );
      objectTuit.put( "desviacionEstandar", desviacionEstandar );
      objectTuit.put( "coeficienteVariacion", coeficienteVariacion );
      collection.insert( WriteConcern.SAFE, objectTuit );
      mongoClient.close();
    }
    catch( Exception e )
    {
      e.printStackTrace( );
    }
  }

}
