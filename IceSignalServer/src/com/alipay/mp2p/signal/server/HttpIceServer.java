package com.alipay.mp2p.signal.server;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.mp2p.signal.util.Util;
import com.sun.net.httpserver.HttpServer;

public class HttpIceServer {
	private static final Logger log = LogManager.getLogger();
	public HttpIceServer(int port) {
		listenPort = port;

	}

	public int getPort() {
		return listenPort;
	}

	private int listenPort;

	private void startHttpServers() throws Exception {
		// 实现HTTP SERVER
		HttpServer hs = HttpServer.create(new InetSocketAddress(listenPort), 0);// 设置HttpServer的端口为80

		HttpService handler = new HttpService(Util.wsUrlLocal);
		handler.connectToWss();

		hs.createContext("/", handler);// 用MyHandler类内处理到/的请求
		hs.setExecutor(null); // creates a default executor
		hs.start();

		/**
		 * //实现HTTPS SERVER //keytool -genkey -keystore serverkeys -keyalg rsa
		 * -alias qusay HttpsServer hss = HttpsServer.create(new
		 * InetSocketAddress(443),0);//设置HTTPS端口这443 KeyStore ks =
		 * KeyStore.getInstance("JKS"); //建立证书库 ks.load(new
		 * FileInputStream("mykey"), "12121".toCharArray());//载入证书
		 * KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		 * //建立一个密钥管理工厂 kmf.init(ks, "mypwd".toCharArray()); //初始工厂 SSLContext
		 * sslContext = SSLContext.getInstance("SSLv3"); //建立证书实体
		 * sslContext.init(kmf.getKeyManagers(), null, null); //初始化证书
		 * HttpsConfigurator conf = new HttpsConfigurator(sslContext);
		 * //在https配置 hss.setHttpsConfigurator(conf); //在https server载入配置
		 * hss.setExecutor(null); // creates a default executor
		 * hss.createContext("/", new MyHandler());// 用MyHandler类内处理到/的请求
		 * hss.start();
		 **/
	}

	public int start() {
		try {
			startHttpServers();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public static void main(String[] args) {
		new HttpIceServer(8888).start();
	}

}