import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.BasicBSONList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class Tweets {

	private HttpGet request = null;
	private int number = 0;
	private String collectionString = "";
	//MongoDB connection
	private DB db = null;

	//MongoDB server
	private Mongo m = null;
	
	//DBList for top 10 Hashtags
	private Hashtable<String, Integer> hashlist = null;
	
	public Tweets(String arg) {
		this(arg,"");
	}
	
	public Tweets(String arg, String secArg) {
	
		hashlist = new Hashtable();
		
		Authentication app = getConnectionData("app.xml");
		connect();
		
		request = connect(app);
	
		if(arg.equals("tweets")) retrieve(request);
		if(arg.equals("search")) search();
		if(arg.equals("socket")) socket();
		if(arg.equals("tweetAccount")) tweetAccount(request, secArg);
	}

	

	/**
	 * The connection method connects to the MongoDB database
	 */
	private void connect() {
		
		try {
			
			m = new Mongo( Configuration.mongo_location , Configuration.mongo_port );
			db = m.getDB(Configuration.mongo_database);
			
		} //try
		catch (UnknownHostException | MongoException e) {
			
			e.printStackTrace();
			
		} //catch		
		
	} //connect
	
	/**
	 * Launch Socket
	 */
	private void socket() {
		TweeterSocket tweeterSocket = new TweeterSocket();
		tweeterSocket.listenSocket();
	}
	
	/**
	 * Get 10 best Hashtag
	 */
	private void anal(BasicDBList list)
	{
		System.out.println("LISTE DES HASHTAGS : "+list);
		//on va lister les tweets qui sont dans la list qu'on a envoyé
		for(int i = 0; i < list.size(); i++)
		{
			System.out.println("Hashtag : "+i+" = "+((BasicDBObject) list.get(i)).get("text"));
			
			BasicDBObject key = new BasicDBObject();
			key = ((BasicDBObject) list.get(i));
			
			//Si notre hashlist contiens le tweet
			if(hashlist.containsKey(key.get("text"))) {
				hashlist.put((String) key.get("text"), hashlist.get(key.get("text")) + 1);
			}
			else hashlist.put((String) key.get("text"), 1);
			
			ArrayList l = sortValueHashtable(hashlist);
				System.out.println(l);
			
		}
	}
	/**
	 * Sort value hashtable
	 * @param t
	 */
	public static ArrayList sortValueHashtable(Hashtable<?, Integer> t){

	       //Transfer as List and sort it
	       ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
	       Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

	         public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
	            return o1.getValue().compareTo(o2.getValue());
	        }});
	       
	       return l;
	    }
	
	/**
	 * Search a tweet
	 */
	private void search() {
		System.out.println("Que voulez vous rechercher : ");
		Scanner sc = new Scanner(System.in);
		String search = sc.nextLine();
		
		DBCollection collection = db.getCollection(Configuration.mongo_collection);
		BasicDBObject recherche = new BasicDBObject();
		recherche.put("text", Pattern.compile(search));
		DBCursor cursor = collection.find(recherche);
		
		while(cursor.hasNext()){
			BasicDBObject r = (BasicDBObject) cursor.next();
			System.out.println("Le : "+r.get("created_at")); //format : M j H:i:s P Y
			System.out.println("Par : "+r.get("screen_name"));
			System.out.println("Texte : "+r.get("text"));
		}
		
	}
	
	private void tweetAccount(HttpGet request, String secArg) {
		String in;
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				this.collectionString = "10 derniers tweets de "+ secArg +" : \n";
				// since this is a continuous stream, there is no end with the
				// readLine, we just check whether live boolean variable is set
				// to false
				while ((in = reader.readLine()) != null) {
					//System.out.println(in);
					if(!in.startsWith("{\"delete")) //si le tweet c'est pas un delete
					{
						/*On écrit dans la BDD noSQL*/
						DBObject object = (DBObject)JSON.parse(in);
						System.out.println(object);
						BasicDBObject object2 = new BasicDBObject();
						DBObject objectUser = (DBObject) object.get("user");
						//On prend que la partie du tweet qui nous intéresse
						object2.put("created_at", object.get("created_at"));
						object2.put("screen_name", objectUser.get("screen_name"));
						object2.put("text", object.get("text"));
						object2.put("entities", object.get("entities"));
						
						number++;
						//System.out.println(object2);
						this.collectionString +=  object2 +"\n";
						System.out.println(this.collectionString);
					}
					if(number == 10){
						break;
					}
						
				} // while

			} // if

		} // try
		catch (Exception e) {

			e.printStackTrace();

		} // catch

	} // tweetAccount()
	
	/**
	 * The retrieve method is responsible to read the content from the stream
	 * and to provide to consumers
	 * 
	 * @param request
	 *            the request to access the stream
	 */
	private void retrieve(HttpGet request) {

		String in;
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				this.collectionString = "10 derniers tweets : \n";
				// since this is a continuous stream, there is no end with the
				// readLine, we just check whether live boolean variable is set
				// to false
				while ((in = reader.readLine()) != null) {
					//System.out.println(in);
					if(!in.startsWith("{\"delete")) //si le tweet c'est pas un delete
					{
						/*On écrit dans la BDD noSQL*/
						DBObject object = (DBObject)JSON.parse(in);
						BasicDBObject object2 = new BasicDBObject();
						DBObject objectUser = (DBObject) object.get("user");
						//On prend que la partie du tweet qui nous intéresse
						object2.put("created_at", object.get("created_at"));
						object2.put("screen_name", objectUser.get("screen_name"));
						object2.put("text", object.get("text"));
						object2.put("entities", object.get("entities"));
						
						DBCollection collection = db.getCollection(Configuration.mongo_collection);
						collection.insert(object2);
						number++;
						//System.out.println(object2);
						this.collectionString +=  object2 +"\n";
						System.out.println(this.collectionString);
						DBObject objectEntities = (DBObject) object.get("entities");
						anal((BasicDBList) objectEntities.get("hashtags"));
					}
					if(number == 10){
						break;
					}
						
				} // while

			} // if

		} // try
		catch (Exception e) {

			e.printStackTrace();

		} // catch

	} // retrieve()
	
	/**
	 * getConnectionData retrieves the data for the authentication
	 * @param stFile the file containing the data for the OAuth authentication
	 * @return an instance of Authentication containing the data
	 */
	private Authentication getConnectionData(String stFile) {
	
		Authentication sr = null;
		
		try {
			
			//open the file and parse it to retrieve the four required information
			File file = new File(stFile);
			InputStream inputStream;
			inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
	
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			
			XMLReader saxReader = XMLReaderFactory.createXMLReader();
			sr = new Authentication();
			saxReader.setContentHandler(sr);
			saxReader.parse(is);
			
		} //try
		catch (FileNotFoundException e) {

			e.printStackTrace();

		} // catch
		
		catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		} // catch
		
		catch (SAXException e) {

			e.printStackTrace();

		} // catch
		
		catch (IOException e) {

			e.printStackTrace();

		} // catch

		return sr;
		
	} //getConnectionData()
	
	/**
	 * The connect method connects to the stream via OAuth
	 * 
	 * @param app
	 *            the data for connection
	 * @return the request
	 */
	private HttpGet connect(Authentication app) {

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				app.getConsumerKey(), app.getConsumerSecret());

		consumer.setTokenWithSecret(app.getAccessToken(), app.getAccessSecret());
		HttpGet request = new HttpGet(Configuration.oauthstream);

		try {

			consumer.sign(request);

		} // try
		catch (OAuthMessageSignerException e) {

			e.printStackTrace();

		} // catch
		catch (OAuthExpectationFailedException e) {

			e.printStackTrace();

		} // catch
		catch (OAuthCommunicationException e) {

			e.printStackTrace();

		} // catch

		return request;

	} // connect()
	public String getCollectionString()
	{
		return collectionString;
	}
	public static void main(String[] args) {
		if(args.length != 0) 
		{
			new Tweets(args[0]);
		}
		else System.out.println("Pas de valeur d'entré !");
	}
}
