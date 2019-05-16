package com.chabuss.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CodeCommitEvent;
import com.amazonaws.services.lambda.runtime.events.CodeCommitEvent.Record;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LambdaHandler implements RequestHandler<CodeCommitEvent, Void> {

	@Override public Void handleRequest(CodeCommitEvent input, Context context) {
		
		System.out.println(Json.encodePrettily(input));
		
		return null;
	}

	
	public JsonObject getDiscordMessageGromRecord(Record record) {
		
		String username = "";
		
		JsonArray embeds = new JsonArray();
		JsonObject message = new JsonObject()
				.put("username", "BOTinho")
				.put("avatar_url", "https://s3.us-east-2.amazonaws.com/public.beetools/images/beetools_icon_orange.png")
				.put("embeds", new JsonArray().add(
							new JsonObject()
								.put("author", new JsonObject()
										.put("name", username))
								.put("title", record.getEventName())
								.put("color", 7645483)
								.put("fields", "")
							));	
		
		
		return message;
	}
	
	private JsonObject addField(String name, String value, boolean inline) {
		return new JsonObject()
				.put("name", name)
				.put("value", value)
				.put("inline", inline);
	}
	
}