package com.alipay.mp2p.signal.client;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class WebSocketIceClient extends WebSocketClient {
	private static final Logger log = LogManager.getLogger();
	public WebSocketIceClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
		this.connect();
	}
	private String roomId;
	private String clientId;
	
	public void resister(String roomId,String clientId)
	{
		this.roomId=roomId;
		this.clientId=clientId;
		
		while(!this.isOpen())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		JSONObject obj = new JSONObject();
		obj.put("cmd", "register");
		obj.put("roomid", roomId);
		obj.put("clientid", clientId);
	    
		this.send(obj.toString());
		log.info("sock send:" + obj.toString() + ",myid:" + clientId);
	}

	@Override
	public void onMessage(String message) {
		log.info("sock recv:" + message + ",myid:" + clientId);
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		log.info(clientId + " leave room:" + roomId);
	}

	@Override
	public void onError(Exception ex) {

	}

}
