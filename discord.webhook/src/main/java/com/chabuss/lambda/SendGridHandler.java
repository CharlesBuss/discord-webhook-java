package com.chabuss.lambda;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.chabuss.discord.DiscordEmbed;
import com.chabuss.discord.DiscordHookMessage;
import com.chabuss.http.HttpRequest;

public class SendGridHandler implements RequestHandler<List<SendGridEvent>, Void>{
	
	@Override public Void handleRequest(List<SendGridEvent> events, Context context) {
		
		String hook = System.getenv("webhook");			
		
		for(SendGridEvent e : events) {
			try {
				DiscordHookMessage message = new DiscordHookMessage()
						.setUserName("SendGrid");
				DiscordEmbed embed = new DiscordEmbed();
				message.addEmbed(embed);
						
				switch (e.getEvent()) {
					case SendGridEvent.BOUNCE:
					case SendGridEvent.DROPPED:
						embed.setColorHexdecimal("#ffcc00")
							 .addField("Email", e.getEmail(),   true)
							 .addField("Event", e.getEvent(),   true)
							 .addField("Status Code", e.getStatus(), true)
							 .addField("Category", e.getCategory(), true)
							 .addField("Reason", e.getReason(), false);
						break;
					case SendGridEvent.DEFERRED:
						embed.setColorHexdecimal("#ffcc00")
							.addField("Email", e.getEmail(),   true)
							.addField("Event", e.getEvent(),   true)
							.addField("Status Code", e.getStatus(), true)
							.addField("Category", e.getCategory(), true)
							.addField("Response", e.getResponse(), false);
						break;
					default:
						continue;
				}
				
				Thread.sleep(250);
	
				HttpRequest request = HttpRequest.post(hook, message.toJsonObject().encode());
				request.execute();
				System.out.println("Response statusCode="+request.getResponseStatusCode());
				System.out.println("Response body="+request.getResponseBody());
				
			}catch (Exception ex) {
				System.out.println("Exception ocurred handling event="+e.getEvent()+" email="+e.getEmail());
				ex.printStackTrace();
			}
		}
		
		return null;
	}

	
}


