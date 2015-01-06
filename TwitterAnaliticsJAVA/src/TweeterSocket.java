import java.io.IOException;
import java.net.ServerSocket;


public class TweeterSocket {
	public TweeterSocket ()
	{
		
	}

public void listenSocket(){
  ServerSocket server = null;
try{
	System.out.println("--------------- SOCKET Launched ----------------");
	server = new ServerSocket(4444);
  } catch (IOException e) {
    System.out.println("Could not listen on port 4444");
    System.exit(-1);
  }
  while(true){
    ClientWorker w;
    try{
//server.accept returns a client connection
      w = new ClientWorker(server.accept());
      Thread t = new Thread(w);
      t.start();
    } catch (IOException e) {
      System.out.println("Accept failed: 4444");
      System.exit(-1);
    }
  }
}
}