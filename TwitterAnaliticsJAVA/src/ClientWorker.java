import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class ClientWorker implements Runnable {
  private Socket client;

//Constructor
  ClientWorker(Socket client) {
    this.client = client;
  }

  public void run(){
    String line;
    String line2;
    BufferedReader in = null;
    PrintWriter out = null;
    try{
      in = new BufferedReader(new 
        InputStreamReader(client.getInputStream()));
      out = new 
        PrintWriter(client.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("in or out failed");
      System.exit(-1);
    }

    while(true){
      try{
        line = in.readLine();
        System.out.println(line);
        int indArg = line.indexOf("|");
		if(indArg >= 0){
			String primArg = line.substring(0, indArg);
			String secArg = line.substring(indArg+1);
			Tweets tweets = new Tweets(primArg,secArg);
			out.println(tweets.getCollectionString());
        } else{
			Tweets tweets = new Tweets(line);
			out.println(tweets.getCollectionString());
		}
        //Send data back to client
        
        out.println("KILL");
        this.client.close();
        break;
       }catch (IOException e) {
        System.out.println("Read failed");
        System.exit(-1);
       }
    }
  }
}