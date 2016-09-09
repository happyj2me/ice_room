package com.alipay.mp2p.signal.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class RoomService {
	private static final Logger log = LogManager.getLogger();
	private class Room{
		public Room(String roomId,String clientId,WebSocket sock)
		{
			members=new HashMap<>();
			reseverdMsg=new HashMap<>();
			this.roomId=roomId;
			members.put(clientId,sock);
		}
		public void add(String client,WebSocket sock)
		{
			members.put(client, sock);
		}
		public void remove(String client)
		{
			members.remove(client);
		}
		public boolean isEmpty()
		{
			return members.size()==0;
		}
		public void saveMessage(String clientId,Object message)
		{
			List<Object> msgs = reseverdMsg.get(clientId);
			if(msgs==null)
			{
				msgs = new ArrayList<Object>();
				reseverdMsg.put(clientId,msgs);
			}
			msgs.add(message);
		}
		public void clearMessage()
		{
			reseverdMsg.clear();
		}
		public int pushMessage(String from,Object message)
		{
			int sent=0;
			if(members.containsKey(from))
			{
				for(Map.Entry<String,WebSocket> entryMember : members.entrySet())
				{
					if(!entryMember.getKey().equals(from))
					{
						JSONObject obj=new JSONObject();
						obj.put("msg", message);
						obj.put("error", "");
						log.info("sock push,room:" + roomId + ",id:" + entryMember.getKey() + ",msg:" + obj.toString());
						entryMember.getValue().send(obj.toString());
						sent++;
					}
				}
			}
			return sent;
		}
		private int pushReserved()
		{
			int sent=0;
			for (Map.Entry<String,List<Object>> entryMsg : reseverdMsg.entrySet()) {
				for(Map.Entry<String,WebSocket> entryMember : members.entrySet())
				{
					if(!entryMember.getKey().equals( entryMsg.getKey()))
					{
						for(Object msg : entryMsg.getValue())
						{
							JSONObject obj=new JSONObject();
							obj.put("msg", msg);
							obj.put("error", "");
							log.info("sock send,room:" + roomId + ",id:" + entryMember.getKey() + ",msg:" + obj.toString());
							entryMember.getValue().send(obj.toString());
							sent++;
						}
					}
				}
			}
			//reseverdMsg.clear();
			return sent;
		}
		private String roomId;
		private Map<String,WebSocket> members;
		private Map<String,List<Object>> reseverdMsg;
		
	}
	
	private Map<String,Room> rooms;
	private Map<WebSocket,String> clients;
	
	public RoomService(){
		rooms = new HashMap<>();
		clients=new HashMap<>();
	}
	public int register(String roomId,String clientId,WebSocket sock)
	{
		int ret=0;
		clients.put(sock, clientId);
		Room room = rooms.get(roomId);
		if(room == null)
		{
		    room = new Room(roomId,clientId,sock);
		    rooms.put(roomId, room);
			ret = 1;
		}
		else
		{
			room.add(clientId, sock);
			
			//不是房间的创建人，一旦注册后就需要推送房间里面保留的消息给他（来自其他用户之前上报的消息，比如sdp信息等）
			room.pushReserved();
		}
		
		return ret;
	}
	
	public void unregister(WebSocket sock)
	{
		String client = clients.get(sock);
		for (Map.Entry<String,Room> entry : rooms.entrySet()) {
			String name=entry.getKey();
			Room room = entry.getValue();
			room.remove(client);
			if(room.isEmpty())
			{
				room.clearMessage();
				rooms.remove(name);
				break;
			}
		}
		clients.remove(sock);
	}
	public int pushMessage(WebSocket sock,Object message)
	{
		int ret=0;
		String client = clients.get(sock);
		for (Room room : rooms.values()) {
			ret = room.pushMessage(client, message);
			if(ret != 0)break;
		}
		return ret;
	}
	public int pushMessage(String toRoom,String fromClient,Object message)
	{
		int ret=0;
		Room room = rooms.get(toRoom);
		if(room != null)
		{
			ret = room.pushMessage(fromClient, message);
			//当前房间就我一个人，先把消息存起来，等其他参与者注册进来的时候，再推送给他们
			if(ret == 0)
			{
				room.saveMessage(fromClient, message);
			}
		}
		else
		{
			log.error("sock save,room not ready:" + toRoom + ",id:" + fromClient + ",msg:" + message);
		}
		return ret;
	}
}
