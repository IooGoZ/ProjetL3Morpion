package fr.NVT.TopOneReacher.modules.ressources;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketSession implements Runnable {
	
	//Created by IooGoZ
	//With :
	//https://stackoverrun.com/fr/q/3411329#12984227
	//https://developer.mozilla.org/fr/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
	//https://openclassrooms.com/fr/courses/2654601-java-et-la-programmation-reseau/2668874-les-sockets-cote-serveur
	
	private WebSocketServer server;
	private Socket client;
	
	private static final int MASK_SIZE = 4;
	private static final int SINGLE_FRAME_UNMASKED = 0x81;
	
	private BufferedOutputStream os;
	private WebSocketParser parser;
	

	public WebSocketSession(WebSocketServer server, Socket client) {
		this.client = client;
		this.server = server;
		try {
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			os = new BufferedOutputStream(client.getOutputStream());

			@SuppressWarnings("resource")
			String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();

			Matcher get = Pattern.compile("^GET").matcher(data);

			if (get.find()) {
				Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
				match.find();
				byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n" + "Connection: Upgrade\r\n"
						+ "Upgrade: websocket\r\n" + "Sec-WebSocket-Accept: "
						+ Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1")
								.digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
						+ "\r\n\r\n").getBytes("UTF-8");

				out.write(response, 0, response.length);
				System.out.println("Client Connection...");
			} else {

			}

		} catch (IOException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.parser = new WebSocketParser(server.getwViewer());
	}

	@Override
	public void run() {
		try {
			while (!client.isClosed()) {
				String response = read();
				//Parser------------------------------------------------------------
				parser.mainParser(response);
			}
			System.out.println("Client in is close");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void send(String str) throws IOException {
		System.out.println("out - " + str);
		
		byte[] msg = str.getBytes();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(SINGLE_FRAME_UNMASKED);
		baos.write(msg.length);
		baos.write(msg);
		baos.flush();
		baos.close();
		os.write(baos.toByteArray(), 0, baos.size());
		os.flush();
	}
	
	private String read() throws IOException {
		byte[] buf = readBytes(2);
		int opcode = buf[0] & 0x0F;
		if (opcode == 8) {
			System.out.println("Client closed!");
			client.close();
			server.removeSession(this);
			return "{CLOSESESSION}";
		} else {
			final int payloadSize = getSizeOfPayload(buf[1]);
			buf = readBytes(MASK_SIZE + payloadSize);
			buf = unMask(Arrays.copyOfRange(buf, 0, 4), Arrays.copyOfRange(buf, 4, buf.length));
			String message = new String(buf);
			System.out.println("in - " + message);
			return message;
		}
	}
	
	//Ressources-----------------------------------------------
	private byte[] readBytes(int numOfBytes) throws IOException {
		byte[] b = new byte[numOfBytes];
		client.getInputStream().read(b);
		return b;
	}
	private int getSizeOfPayload(byte b) {
		// Must subtract 0x80 from masked frames
		return ((b & 0xFF) - 0x80);
	}
	private byte[] unMask(byte[] mask, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (data[i] ^ mask[i % mask.length]);
		}
		return data;
	}
	//----------------------------------------------------------------

	public WebSocketServer getServer() {
		return server;
	}
}
