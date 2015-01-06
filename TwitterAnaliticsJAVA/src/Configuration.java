/**
 * The Configuration class stores parameters for the different Entities
 * @author MPH
 *
 */
public class Configuration {

	public static String oauthstream = "https://stream.twitter.com/1.1/statuses/sample.json";
	public static String userapi = "api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
	public static String mongo_location = "localhost";
	public static int mongo_port = 27017;
	public static String mongo_database = "twitterJava";
	public static String mongo_collection = "tweets";
		
}
