package com.alipay.mp2p.signal.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.mp2p.signal.server.AsyncHttpEvents;
import com.alipay.mp2p.signal.util.Util;

public class HttpIceClient implements AsyncHttpEvents {
	private static final Logger log = LogManager.getLogger();
	private static final int HTTP_TIMEOUT_MS = 3000;
	private static final String HTTP_ORIGIN = "https://apprtc.appspot.com";
	private final String method;
	
	private String contentType;

	private String roomId;
	private String clientId;

	public String getRoomId(){return roomId;}
	public String getClientId(){return clientId;}
	
	public void onHttpError(String errorMessage) {
		log.error("http error: " + errorMessage);
	}

	@Override
	public void onHttpComplete(String response) {
		try {
			JSONObject roomJson = new JSONObject(response);
			String result = roomJson.getString("result");
			if (!result.equals("SUCCESS")) {
				log.error("signal server response error: " + result);
			} else if (roomJson.has("params")){
				clientId = roomJson.getJSONObject("params").getString("client_id");
				roomId = roomJson.getJSONObject("params").getString("room_id");
			}
		} catch (JSONException e) {
			log.error("post to signal server error: " + e.toString());
		}
	}

	public HttpIceClient(String method) {
		this.method = method;
	}

	public void sendHttpMessage(String url,String message) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			byte[] postData = new byte[0];
			if (message != null) {
				postData = message.getBytes("UTF-8");
			}
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(HTTP_TIMEOUT_MS);
			connection.setReadTimeout(HTTP_TIMEOUT_MS);
			connection.addRequestProperty("origin", HTTP_ORIGIN);
			boolean doOutput = false;
			if (method.equals("POST")) {
				doOutput = true;
				connection.setDoOutput(true);
				connection.setFixedLengthStreamingMode(postData.length);
			}
			if (contentType == null) {
				connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			} else {
				connection.setRequestProperty("Content-Type", contentType);
			}

			// Send POST request.
			if (doOutput && postData.length > 0) {
				OutputStream outStream = connection.getOutputStream();
				outStream.write(postData);
				outStream.close();
			}

			// Get response.
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				onHttpError(
						"Non-200 response to " + method + " to URL: " + url + " : " + connection.getHeaderField(null));
				connection.disconnect();
				return;
			}
			InputStream responseStream = connection.getInputStream();
			String response = Util.drainStream(responseStream);
			responseStream.close();
			connection.disconnect();
			onHttpComplete(response);
		} catch (SocketTimeoutException e) {
			onHttpError("HTTP " + method + " to " + url + " timeout");
		} catch (IOException e) {
			onHttpError("HTTP " + method + " to " + url + " error: " + e.getMessage());
		}
	}
	
	
}
