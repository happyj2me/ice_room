package com.alipay.mp2p.signal.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.mp2p.signal.util.Util;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpService implements HttpHandler {
	private static final Logger log = LogManager.getLogger();
	public HttpService(String wssLocation){
		this.wssLoaction = wssLocation;
		rooms = new ArrayList<>();
	}
	//收到来自客户端的上行消息后，转发给信令服务器，通知它下发给其他客户端
	private WebSocketClient wsc;
	private String wssLoaction;
	private List<String> rooms;
	
	public void handle(HttpExchange t) throws IOException {
		String reqUrl = t.getRequestURI().toString();
		String reqBody = Util.drainStream(t.getRequestBody());
		log.info("http recv:" + t.getRemoteAddress() + reqUrl + ",body:" + reqBody);
		
		String rsp="";
		String args[] = reqUrl.split("/");
		if(args.length == 3 && args[1].equals("join"))
		{
			String roomId=args[2];
			rsp=processJoin(roomId);
		}else if(args.length == 4){
			String action=args[1];
			String roomId=args[2];
			String clientId = args[3];
			switch(action)
			{
				case "leave":
					processLeave(roomId,clientId);
					break;
				case "message":
					processMessage(roomId,clientId,reqBody);
					break;
				default:
					break;
			}
	        JSONObject json = new JSONObject();

	        Util.jsonPut(json, "result", "SUCCESS");
		    rsp=json.toString();
		}
		t.sendResponseHeaders(200, rsp.length());
		OutputStream os = t.getResponseBody();
		os.write(rsp.getBytes());
		os.close();
		log.info("http send:" + rsp);
	}
	//"pc_config": "{\"rtcpMuxPolicy\": \"require\", \"bundlePolicy\": \"max-bundle\", \"iceServers\": []}"}, "result": "SUCCESS"}
	private String processJoin(String roomId)
	{
		JSONArray messsages = new JSONArray();
		
		//为新加入房间的客户分配ID
		String clientId=Util.generateId();
		boolean isOld = rooms.contains(roomId);
		if(!isOld)
			rooms.add(roomId);
		
        JSONObject iceServer_1 = new JSONObject();
        Util.jsonPut(iceServer_1,"urls", Util.turnUrl);
        Util.jsonPut(iceServer_1,"username", "AuthrizedUser");
        Util.jsonPut(iceServer_1,"credential", "password");
        
        JSONObject iceServer_2 = new JSONObject();
        Util.jsonPut(iceServer_2,"urls", Util.stunUrl);
        Util.jsonPut(iceServer_2,"username", "AuthrizedUser");
        Util.jsonPut(iceServer_2,"credential", "password");
        
        JSONArray iceServers = new JSONArray();
        iceServers.put(iceServer_1);
        iceServers.put(iceServer_2);
        
        JSONObject pcConfig = new JSONObject();
        Util.jsonPut(pcConfig,"rtcpMuxPolicy", "require");
        Util.jsonPut(pcConfig,"bundlePolicy", "max-bundle");
        Util.jsonPut(pcConfig,"iceServers", iceServers);

        
        JSONObject param = new JSONObject();
        Util.jsonPut(param, "room_id", roomId);
        Util.jsonPut(param, "client_id",clientId);
        Util.jsonPut(param, "wss_url", Util.wsUrl);
        Util.jsonPut(param, "wss_post_url", Util.wsUrl);
        Util.jsonPut(param, "is_initiator", !isOld);
        Util.jsonPut(param, "pc_config", pcConfig);
        Util.jsonPut(param, "messages",messsages);

        JSONObject paramResponse = new JSONObject();
        Util.jsonPut(paramResponse, "params", param);
        Util.jsonPut(paramResponse, "result", "SUCCESS");
        
	    return paramResponse.toString();
	}
	private void processLeave(String roomId,String clientId)
	{
		rooms.remove(roomId);
	}
	private void processMessage(String roomId,String clientId,String message)
	{
		//来自客户端的json消息
		JSONObject msg = new JSONObject(message);
		
		JSONObject object = new JSONObject();
		Util.jsonPut(object, "cmd", "SIGNAL");
        Util.jsonPut(object, "roomid", roomId);
        Util.jsonPut(object, "clientid",clientId);
        Util.jsonPut(object, "msg", msg);
        wsc.send(object.toString());
	}
	public void connectToWss() throws URISyntaxException 
	{
		wsc = new WebSocketClient( new URI( wssLoaction ), new Draft_17() ) {

			@Override
			public void onMessage( String message ) {
				
			}

			@Override
			public void onOpen( ServerHandshake handshake ) {
			}

			@Override
			public void onClose( int code, String reason, boolean remote ) {

			}

			@Override
			public void onError( Exception ex ) {

			}
		};
		
		wsc.connect();
	}
}
