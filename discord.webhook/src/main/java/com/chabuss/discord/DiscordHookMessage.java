package com.chabuss.discord;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.util.StringUtils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DiscordHookMessage {

	private String username;
	private String avatar_url;
	private String content;
	private List<DiscordEmbed> embeds;
	
	public DiscordHookMessage() {
		this.embeds = new ArrayList<>();
	}
	
	public DiscordHookMessage setUserName(String username) {
		if(StringUtils.isNullOrEmpty(username))
			return this;
		this.username = username;
		return this;
	}
	
	public DiscordHookMessage setAvatar(String avatar_url) {
		if(StringUtils.isNullOrEmpty(avatar_url))
			return this;
		this.avatar_url = avatar_url;
		return this;
	}
	
	public DiscordHookMessage setContent(String content) {
		if(StringUtils.isNullOrEmpty(content))
			return this;
		this.content = content;
		return this;
	}
	
	public DiscordHookMessage addEmbed(DiscordEmbed embed) {
		this.embeds.add(embed);
		return this;
	}
	
	public JsonObject toJsonObject() {
		
		JsonObject message = new JsonObject();
		
		if(!StringUtils.isNullOrEmpty(username))
			message.put("username", username);
		
		if(!StringUtils.isNullOrEmpty(avatar_url))
			message.put("avatar_url", avatar_url);
		
		if(!StringUtils.isNullOrEmpty(content))
			message.put("content", content);
		
		if(!embeds.isEmpty()) {
			JsonArray embedArray = new JsonArray();
			for (DiscordEmbed embed : embeds)
				if(embed != null)
					embedArray.add(embed.toJsonObject());
			message.put("embeds", embedArray);			
		}
		
		return message;
	}
	
}
