package com.chabuss.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CodeCommitEvent;
import com.amazonaws.services.lambda.runtime.events.CodeCommitEvent.Record;
import com.amazonaws.services.lambda.runtime.events.CodeCommitEvent.Reference;
import com.chabuss.discord.DiscordEmbed;
import com.chabuss.discord.DiscordHookMessage;
import com.chabuss.http.HttpRequest;

import io.vertx.core.json.Json;

public class LambdaHandler implements RequestHandler<CodeCommitEvent, Void> {

	@Override public Void handleRequest(CodeCommitEvent input, Context context) {
		
		
		Record record = input.getRecords().get(0);
		
		DiscordHookMessage message = new DiscordHookMessage();
		
		message.setUserName("AWS Lambda");
		
		DiscordEmbed embed = new DiscordEmbed()
				.addField("Region", record.getAwsRegion(), true)
				.addField("Event name", record.getEventName(), true)
				.addField("User", record.getUserIdentityArn().substring(record.getUserIdentityArn().indexOf("/")), true)
				.setFooter("AWS Code Commit", null);
		
		message.addEmbed(embed);
		
		for(Reference reference : record.getCodeCommit().getReferences())
			message.addEmbed(new DiscordEmbed()
					.addField("Commit", reference.getCommit(), true));
		
		System.out.println(Json.encode(input));
		System.out.println("WebHook "+record.getCustomData());
		
		try {
			HttpRequest request = HttpRequest.post(record.getCustomData(), message.toJsonObject().encode());
			request.execute();
			System.out.println("Response status code: "+request.getResponseStatusCode());
			System.out.println("Response status code: "+request.getResponseBody());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}