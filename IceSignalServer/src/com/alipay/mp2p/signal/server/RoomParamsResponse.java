package com.alipay.mp2p.signal.server;

import java.util.List;

public class RoomParamsResponse {
    public  class IceServer {
        public final String uri;
        public final String username;
        public final String password;

        public IceServer(String uri) {
            this(uri, "", "");
        }

        public IceServer(String uri, String username, String password) {
            this.uri = uri;
            this.username = username;
            this.password = password;
        }

        public String toString() {
            return this.uri + "[" + this.username + ":" + this.password + "]";
        }
    }
	public class PcConfig{
		private String rtcpMuxPolicy;
		private String bundlePolicy;
		private List<IceServer> iceServers;
	}
	public class PcConstraints{
		private List<String> optional;
	}
	public class CallStatsParams{
		private String appSecret;
		private String appId;
	}
	public class VersionInfo{
		private String gitHash;
		private String branch;
		private String time;
	}
	public class MediaConstraints{
		private boolean audio;
		private boolean video;
	}
	public class Candidate{
		private String label;
		private String id;
		private String candidate;
	}
	public class MessageItem
	{
		private String type;
		private String sdp;
		private Candidate candiate;
	}
	public class OfferOptions{
		
	}
	
	public class RoomParams{
		private String wssPostUrl;
		private boolean isInitiator;
		private String turnUrl;
		private String roomLink;
		private boolean isLoopBack;
		
		private OfferOptions offerOptions;
		
		private List<MessageItem> messages;
		
		private VersionInfo versionInfo;
		
		private PcConstraints pcConstrains;
		
		private List<String> errorMessages;
		
		private String includeLookBackJs;
		private String iceServerUrl;
		private List<String> warningMessages;
		private String roomId;
		private CallStatsParams callStatParams;
		
		private String clientId;
		private boolean bypassJoinConfirmation;
		private String wssUrl;
		
		private MediaConstraints mediaConstraints;
		private PcConfig pcConfig;
		
		private List<String> iceServers;
	}
	
	private RoomParams params;
	private String result;
}
