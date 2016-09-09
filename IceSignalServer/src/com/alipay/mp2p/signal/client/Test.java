package com.alipay.mp2p.signal.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONObject;

import com.alipay.mp2p.signal.util.Util;

public class Test {
	static {
		System.setProperty("log4j.configurationFile","conf/log4j2.xml");
	}
	
	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		String loginBase="http://127.0.0.1:8888/join/";
		String msgBase="http://127.0.0.1:8888/message/";
		String leaveBase="http://127.0.0.1:8888/leave/";
		String wssUrl="ws://127.0.0.1:8887";
		
		//A,B 2p聊天室
		String room=Util.generateId();
		
		JSONObject sdpA = new JSONObject();
		Util.jsonPut(sdpA, "sdp", "this sdp is from A");
		Util.jsonPut(sdpA, "type", "offer");
		
		JSONObject sdpB = new JSONObject();
		Util.jsonPut(sdpB, "sdp", "this sdp is from B");
		Util.jsonPut(sdpB, "type", "offer");
		
		HttpIceClient clientA = new HttpIceClient("POST");
		HttpIceClient clientB = new HttpIceClient("POST");
		
		WebSocketIceClient wscA = new WebSocketIceClient(new URI(wssUrl),new Draft_17());
		WebSocketIceClient wscB = new WebSocketIceClient(new URI(wssUrl),new Draft_17());
		
		clientA.sendHttpMessage(loginBase + room, "A login");
		Thread.sleep(200);
		wscA.resister(clientA.getRoomId(), clientA.getClientId());
		
		clientA.sendHttpMessage(msgBase + clientA.getRoomId() + "/" + clientA.getClientId(),sdpA.toString());
		
		clientB.sendHttpMessage(loginBase + room, "B login");
		Thread.sleep(200);
		wscB.resister(clientB.getRoomId(), clientB.getClientId());
		
		clientB.sendHttpMessage(msgBase + clientB.getRoomId() + "/" + clientB.getClientId(),sdpB.toString());
		
		clientA.sendHttpMessage(leaveBase+clientA.getRoomId() + "/" + clientA.getClientId(), "A leave");
		clientB.sendHttpMessage(leaveBase+clientB.getRoomId() + "/" + clientB.getClientId(), "B leave");
		
		wscA.close();
		wscB.close();
	}

}
