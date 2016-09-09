package com.alipay.mp2p.signal.util;

import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	public final static String wsUrlLocal="ws://127.0.0.1:8887";
    public final static String wsUrl="ws://120.76.204.118:8887";
    public final static String turnUrl="turn:120.76.204.118:3478";
    public final static String stunUrl="stun:120.76.204.118:3478";
    
    private static final Logger log = LogManager.getLogger();
    
	public static  String generateId(){
		
		return String.valueOf(Math.abs(new Random().nextInt()));
	}
	  // Return the contents of an InputStream as a String.
	public static String drainStream(InputStream in) {
	    Scanner s = new Scanner(in).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	  }

	public static void jsonPut(JSONObject json, String key, Object value) {
		    try {
		      json.put(key, value);
		    } catch (JSONException e) {
		      throw new RuntimeException(e);
		    }
	 }
	
	public static void main(String args[]){
		log.info("id:" + Util.generateId());
		log.info("id:" + Util.generateId());
		log.info("id:" + Util.generateId());
		log.info("id:" + Util.generateId());
		log.info("id:" + Util.generateId());
		log.info("id:" + Util.generateId());
	}
}
