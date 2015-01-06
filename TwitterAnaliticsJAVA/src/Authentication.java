import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;


public class Authentication implements ContentHandler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Authentication() {
        super();
        // On definit le locator par defaut.
        locator = new LocatorImpl();
	}

	public void setDocumentLocator(Locator value) {
	        locator =  value;
	}
	
	public void startDocument() throws SAXException {
	        System.out.println("Debut de l'analyse du document");
	}
	
	public void endDocument() throws SAXException {
	        System.out.println("Fin de l'analyse du document" );
	}
	
	public void startPrefixMapping(String prefix, String URI) throws SAXException {
	        System.out.println("Traitement de l'espace de nommage : " + URI + ", prefixe choisi : " + prefix);
	}
	
	public void endPrefixMapping(String prefix) throws SAXException {
	        System.out.println("Fin de traitement de l'espace de nommage : " + prefix);
	}
	
	public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
	        System.out.println("Ouverture de la balise : " + localName);
	        System.out.println();
	        if(localName == "ConsumerKey"){
	        	boolConsumerKey = true;
	        }
	        if(localName == "ConsumerSecret"){
	        	boolConsumerSecret = true;
	        }
	        if(localName == "AccessToken"){
	        	boolAccessToken = true;
	        }
	        if(localName == "AccessSecret"){
	        	boolAccessSecret = true;
	        }
	        
	}
	
	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
	        System.out.print("Fermeture de la balise : " + localName);
	        System.out.println();
	        if(localName == "ConsumerKey"){
	        	boolConsumerKey = false;
	        }
	        if(localName == "ConsumerSecret"){
	        	boolConsumerSecret = false;
	        }
	        if(localName == "AccessToken"){
	        	boolAccessToken = false;
	        }
	        if(localName == "AccessSecret"){
	        	boolAccessSecret = false;
	        }
	        
	        
	}

	public void characters(char[] ch, int start, int end) throws SAXException {
	        System.out.println("#PCDATA : " + new String(ch, start, end));
	        System.out.println();
	        
	        if(boolConsumerKey){
	        	ConsumerKey = new String(ch, start, end);
	        }
	    	if(boolConsumerSecret){
	    		ConsumerSecret = new String(ch, start, end);
	    	}
	    	if(boolAccessToken){
	    		AccessToken = new String(ch, start, end);
	    	}
	    	if(boolAccessSecret){
	    		AccessSecret = new String(ch, start, end);
	    	}
	}

	public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
	        System.out.println("espaces inutiles rencontres : ..." + new String(ch, start, end) +  "...");
	}

	public void processingInstruction(String target, String data) throws SAXException {
	        System.out.println("Instruction de fonctionnement : " + target);
	        System.out.println("  dont les arguments sont : " + data);
	}

	public void skippedEntity(String arg0) throws SAXException {
	        // Je ne fais rien, ce qui se passe n'est pas franchement normal.
	        // Pour eviter cet evenement, le mieux est quand meme de specifier une DTD pour vos
	        // documents XML et de les faire valider par votre parser.              
	}
	
	private Locator locator;
	
	private String ConsumerKey;
	private String ConsumerSecret;
	private String AccessToken;
	private String AccessSecret;
	
	private boolean boolConsumerKey;
	private boolean boolConsumerSecret;
	private boolean boolAccessToken;
	private boolean boolAccessSecret;
	
	public String getConsumerKey()
	{
		return ConsumerKey;
	}
	
	public String getConsumerSecret()
	{
		return ConsumerSecret;
	}
	
	public String getAccessToken()
	{
		return AccessToken;
	}
	
	public String getAccessSecret()
	{
		return AccessSecret;
	}
	public void afficherAttr()
	{
		System.out.println(ConsumerKey+" \n"+ConsumerSecret+" \n"+AccessToken+" \n"+AccessSecret);
	}
}
