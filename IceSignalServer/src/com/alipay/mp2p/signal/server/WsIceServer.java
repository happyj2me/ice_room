package com.alipay.mp2p.signal.server;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WsIceServer extends WebSocketServer {
	private static final Logger log = LogManager.getLogger();
	public WsIceServer(int listenPort)
	{
		super(new InetSocketAddress(listenPort));
		roomService=new RoomService();
	}

	private RoomService roomService;
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		log.info("conn quit:" + conn.getRemoteSocketAddress());
		roomService.unregister(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception exec) {
		log.error("error happened:" + exec);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		log.info("sock recv:" + conn.getRemoteSocketAddress() + ",msg:" + message);
		try{
			JSONObject obj = new JSONObject(message);
			String type = obj.getString("cmd").toUpperCase();
			String roomId="",clientId="";
			switch(type)
			{
				case "REGISTER":
				{
					roomId=obj.getString("roomid");
					clientId=obj.getString("clientid");
					roomService.register(roomId, clientId, conn);
					break;
				}
				case "SEND":
				{
					Object msg=new JSONObject(obj.getString("msg"));
					roomService.pushMessage(conn, msg);
					break;
				}
				case "SIGNAL":
				{
					roomId=obj.getString("roomid");
					clientId=obj.getString("clientid");
					Object msg=obj.getJSONObject("msg");
					roomService.pushMessage(roomId, clientId, msg);
					break;
				}
				default:
					System.out.println("message's cmd error:" + message + ",conn:" + conn.getRemoteSocketAddress());
					break;
			}
		}
		catch(JSONException e)
		{
			log.error("error parse message:" + message + ",conn:" + conn.getRemoteSocketAddress());
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		log.info("conn recv:" + conn.getRemoteSocketAddress() +
				" hash " + conn.getRemoteSocketAddress().hashCode());
	}
	
	public static void main(String args[]){
		WsIceServer webSocketServer = new WsIceServer(8887);
		webSocketServer.start();
	}
}
