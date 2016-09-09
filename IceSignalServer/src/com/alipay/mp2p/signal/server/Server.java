package com.alipay.mp2p.signal.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
	
	static 
	{
		System.setProperty("log4j.configurationFile","conf/log4j2.xml");
	}
	
	private static final Logger log = LogManager.getLogger();
	
	public static void main(String args[]){
		
		WsIceServer webSocketServer = new WsIceServer(8887);
		webSocketServer.start();
		
		HttpIceServer httpServer = new HttpIceServer(8888);
		httpServer.start();
		
		log.info( "WebSocke listen on port: " + webSocketServer.getPort() );
		log.info( "http listen on port: " + httpServer.getPort() );
	}
}
