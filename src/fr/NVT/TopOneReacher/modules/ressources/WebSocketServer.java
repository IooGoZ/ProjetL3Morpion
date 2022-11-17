package fr.NVT.TopOneReacher.modules.ressources;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.modules.viewers.WebViewer; 

public class WebSocketServer extends ServerSocket {

	private static final int PORT = 80;
	private boolean isRunning = true;
	private List<WebSocketSession> wsSessions = new ArrayList<>();
	
	private final WebSocketServer wsServer;
	private final WebViewer wViewer;
	
	
	public WebSocketServer(WebViewer webViewer) throws IOException {
		super(PORT);
		System.out.println("Démarrage du serveur sur 127.0.0.1:" + PORT);
		
		this.wViewer = webViewer;
		this.wsServer = this;
	}

	public void open() {
		System.out.println("Attente d'une connexion...");
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (isRunning == true) {
					try {
						Socket client = accept();
						System.out.println("Connexion cliente re�ue.");
						WebSocketSession wsSession = new WebSocketSession(wsServer, client);
						Thread t = new Thread(wsSession);
						t.start();
						wsSessions.add(wsSession);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				close();
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
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendToAll(String str) throws IOException {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (WebSocketSession wsSession : wsSessions) {
					try {
						wsSession.send(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
		
	}

	public WebViewer getwViewer() {
		return wViewer;
	}
}
