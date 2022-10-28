package fr.NVT.TopOneReacher.modules.ressources;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.modules.viewers.WebViewer; 

public class WebSocketServer {

	private static final int port = 80;
	private ServerSocket server;
	private boolean isRunning = true;
	private List<WebSocketSession> wsSessions = new ArrayList<>();
	
	private  WebSocketServer wsServer;
	private final WebViewer wViewer;
	
	
	public WebSocketServer(WebViewer webViewer) {
		try {
			server = new ServerSocket(port);
			System.out.println("Démarrage du serveur sur 127.0.0.1:" + port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.wViewer = webViewer;
	}

	public void open() {
		System.out.println("Attente d'une connexion...");
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (isRunning == true) {
					try {
						Socket client = server.accept();
						System.out.println("Connexion cliente re�ue.");
						WebSocketSession wsSession = new WebSocketSession(wsServer, client);
						Thread t = new Thread(wsSession);
						t.start();
						wsSessions.add(wsSession);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
					server = null;
				}
			}
		});
		t.start();
	}

	public void removeSession(WebSocketSession session) {
		wsSessions.remove(session);
	}
	
	public void close() {
		isRunning = false;
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendToAll(String str) throws IOException {
		for (WebSocketSession wsSession : wsSessions) {
			wsSession.send(str);
		}
	}

	public WebViewer getwViewer() {
		return wViewer;
	}
}
