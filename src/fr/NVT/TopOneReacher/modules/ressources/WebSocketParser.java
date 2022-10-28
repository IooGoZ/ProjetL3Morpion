package fr.NVT.TopOneReacher.modules.ressources;

import java.io.IOException;

import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.viewers.WebViewer;

public class WebSocketParser {
	private final static String ID_CHAR = "µ";
	private final static String SPLIT_CHAR = "¤";
	
	private WebViewer wViewer;
	
	public WebSocketParser(WebViewer viewer) {
		this.wViewer = viewer;
	}
	
	public void mainParser(String info)
	{
		// #### DE MESSAGE PARSEES A VALEURS ####
		
	    // On recupere chaque partie du message separes par le symbole SPLIT_CHAR
	    String[] infos = info.split(ID_CHAR);

	    int length = infos.length;
	    if (length != 2)
	    {
	    	System.out.println("ERROR in parserMaster : infos.length is not equal to 2, it is egal to " + length);
	        return;
	    }
	    else
	    {
	        // On recupere la valeur
	        int id = Integer.parseInt(infos[0]);
	        // On recupere le message
	        String msg = infos[1];

	        switch (id)
	        {
	            case '0' : 
	            	parserCreateNewGame(msg);
	                break;
	            case '1' : 
	            	parserSetDelay(msg);
	                break;
	            case '2' : 
	            	parserRunGame();
	                break;
	            case '3' : 
	            	parserPauseResume();
	                break;
	            case '4' : 
	            	parserDisplayAction(msg);
	                break;
	            default :
	            	System.out.println("ERROR in parserMaster : the ID doesn't correspond to any command");
	        }
	    }
	}
	
	// Parser pour creer une nouvelle partie 
	private void parserCreateNewGame(String msg)
	{
	    // On recupere chaque partie du message separes par le symbole SPLIT_CHAR
		String[] msgs = msg.split(SPLIT_CHAR);
	    // On verifie si le message a la bonne taille
	    int length = msgs.length;
	    if (length != 3)
	    {
	    	System.out.println("ERROR in parserCreateNewGame : msgs.length is not equal to 3, it is egal to " + length);
	        return;
	    }
	    else
	    {
	        // On recupere les valeurs
	        int width = Integer.parseInt(msgs[0]);
	        int height = Integer.parseInt(msgs[1]);
	        int depth = Integer.parseInt(msgs[2]);
	        
	        // On appelle la fonction avec les valeurs recuperes
	        wViewer.createNewGame(width, height, depth);
	    }
	}
	
	// Parser pour transmettre un delai 
	private void parserSetDelay(String msg)
	{
		double delay = Double.parseDouble(msg);
			
		// On appelle la fonction avec la valeur
		wViewer.setDelay(delay);
	}
		
	// Parser pour lancer une nouvelle partie 
	private void parserRunGame() // pas besoin de valeur
	{	        
		// On appelle la fonction qui n'a pas de valeur
		wViewer.runGame();
	}
	
	// Parser pour mettre et defaire la pause
	private void parserPauseResume()
	{	        
		wViewer.pauseResume();
	}
	
	// Parser pour jouer une action 
	private void parserDisplayAction(String msg)
	{
		// On recupere chaque partie du message separes par le symbole SPLIT_CHAR
		String[] msgs = msg.split(SPLIT_CHAR);
		// On verifie si le message a la bonne taille
		int length = msgs.length;
		if (length != 3)
		{
			System.out.println("ERROR in parserDisplayAction : msgs.length is not equal to 3, it is egal to " + length);
			return;
		}
		else
		{
			// On recupere les valeurs
			int x = Integer.parseInt(msgs[0]);
			int y = Integer.parseInt(msgs[1]);
			int z = Integer.parseInt(msgs[2]);
				        
			// On appelle la fonction avec les valeurs recuperes
			//displayAction(x, y, z);                 A FINIR
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------
	
	
	// #### DE VALEURS A MESSAGE PARSEES ####
	
	// Permet "d'empaqueter" un message avec son id et son contenu puis l'envoyer
	// C'est comme un envoi a la poste : le timbre (id) et la lettre (msg)
	private static void parseMake(WebSocketServer ws_server, int id, String msg) throws IOException 
	{
		// On prepare le paquet
		msg = id + ID_CHAR + msg;
		
		// On envoie le paquet parse 
		ws_server.sendToAll(msg);
	}
	
	
	// Parse les dimensions du plateau
	public static void unparserInitBoardLengths(WebSocketServer ws_server, int width, int height, int depth)
	{
		//On definit l'id
		int id = 0;
		
		//On redige le "message" : les 3 valeurs separes par le separateur
		String msg = width + SPLIT_CHAR + height + SPLIT_CHAR + depth;
		
		// On envoie au parseMaker
		try {
			parseMake(ws_server, id, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Parse toutes les positions du plateau
	public static void unparserShowBoard(WebSocketServer ws_server, Position[] positions)
	{
		//On definit l'id
		int id = 1;
		
		//On redige le "message"
		String msg = null;
		for (Position pos : positions) {
			msg = msg + pos.getX() + SPLIT_CHAR + pos.getY() + SPLIT_CHAR + pos.getZ() + SPLIT_CHAR;
		}
		
		//On retire le dernier caractere, le SPLIT_CHAR qui n'est precede de rien
		msg = msg.substring(0, msg.length()-1); // <----- PAS CERTAIN, A TESTER
		
		// On envoie au parseMaker
		try {
			parseMake(ws_server, id, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Parse les dimensions du plateau
	public static void unparserPlayerPos(WebSocketServer ws_server, Position pos)
	{
		//On definit l'id
		int id = 2;
		
		//On redige le "message"
		String msg = pos.getX() + SPLIT_CHAR + pos.getY() + SPLIT_CHAR + pos.getZ();
		
		// On envoie le timbre (id) et la lettre (msg) a la "poste"
		try {
			parseMake(ws_server, id, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Parse le message de victoire
	public static void unparserShowWinner(WebSocketServer ws_server, String winnerTxt)
	{
		//On definit l'id
		int id = 3;
		
		// On envoie au parseMaker
		try {
			parseMake(ws_server, id, winnerTxt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Parse le message de victoire
	public static void unparserPauseResume(WebSocketServer ws_server, Boolean pausing)
	{
		//On definit l'id
		int id = 4;
		
		//On redige le "message"
		String msg;
		if (pausing)
		{
			msg = "0";
		}
		else
		{
			msg = "1";
		}
		
		// On envoie au parseMaker
		try {
			parseMake(ws_server, id, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Parse le message de victoire
	public static void unparserException(WebSocketServer ws_server, String exceptionTxt)
	{
		//On definit l'id
		int id = 5;
		
		// On envoie au parseMaker
		try {
			parseMake(ws_server, id, exceptionTxt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}











