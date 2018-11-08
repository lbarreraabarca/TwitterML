import java.io.*;
import java.util.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

public class main
{
	public static void main( String[] args )
  {
  	Calendar calendar = Calendar.getInstance( );
    try
		{
			MongoClient mongoClient = new MongoClient("localhost");
      /*List<String> databases = mongoClient.getDatabaseNames();
      for (String dbName : databases) 
			{
      	System.out.println("- Database: " + dbName);
				DB db = mongoClient.getDB(dbName);        
     		Set<String> collections = db.getCollectionNames();
      	for (String colName : collections) 
				{
      		System.out.println("\t + Collection: " + colName);
       	}
      }*/
			DB db = mongoClient.getDB("DB_TUITS_ONLINE");
      DBCollection collection = db.getCollection("jugadors");
      BasicDBObject o = new BasicDBObject();
      o.append("nombre", "KAKAKAK");
      o.append("apellido", "test");
			o.append("username", "testeador1");
			o.append("password", "1234578");
			o.append("edad", 10);
      collection.insert( o );
			
			 for ( DBObject doc : collection.find() )
            System.out.println(doc);

      mongoClient.close();
		}
		catch (Exception  e )
		{
		  e.printStackTrace();
    }
	}
}
