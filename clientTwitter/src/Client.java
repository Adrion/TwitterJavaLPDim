import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	public Client(String[] args){
		String params = args[0] ;
		if(args.length>1)
			params += "|"+args[1];
		this.listenSocket(params);
	}
	
	public static void main(String[] args) {
		Client client = new Client(args);
	}
	
	public void listenSocket(String param){
	//Create socket connection
	   try{
	     Socket socket = new Socket("localhost", 4444);
	     PrintWriter out = new PrintWriter(socket.getOutputStream(), 
	                true);
	     out.println(param);
	     BufferedReader in = new BufferedReader(new InputStreamReader(
	                socket.getInputStream()));
	     String tmp;
	     while((tmp = in.readLine()) != null){
	    	 System.out.println(tmp);
	    	 if(tmp == "KILL"){
	    		 socket.close();
	    		 break;
	    	 }
	     }
	   } catch (UnknownHostException e) {
	     System.out.println("Unknown host: localhost");
	     System.exit(1);
	   } catch  (IOException e) {
	     System.out.println("No I/O");
	     System.exit(1);
	   }
	}
}
