package com.chabuss.lambda;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendGridEvent {

	public static final String DROPPED   = "dropped";
	public static final String DELIVERED = "delivered";
	public static final String PROCESSED = "processed";
	public static final String DEFERRED  = "deferred";
	public static final String BOUNCE    = "bounce";
	
	private String email;
	private long   timestamp;
	private String event;
	private String category;
	private String sg_event_id;
	private String sg_message_id;
	private String reason;
	private String status;
	private String response;
	
}