package com.chabuss.discord;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.util.StringUtils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DiscordEmbed {
	
	private Integer          color;
	private String           description;
	private String           title;
	private String           url;
	private List<JsonObject> fields;
	private JsonObject       author;
	private JsonObject       thumbnail;
	private JsonObject       image;
	private JsonObject       footer;
	
	public DiscordEmbed() {
		fields = new ArrayList<>();
	}
	
	public DiscordEmbed setAuthor(String name, String url, String icon_url) {
		if(StringUtils.isNullOrEmpty(name)) {
			System.out.println("name is null or empty.");
			return this;
		}
		this.author = new JsonObject()
				.put("name", name);
		
		if(!StringUtils.isNullOrEmpty(url))
			this.author.put("url", url);
		
		if(!StringUtils.isNullOrEmpty(icon_url))
			this.author.put("icon_url", icon_url);
		
		return this;
	}
	
	public DiscordEmbed setTitle(String title) {
		if(StringUtils.isNullOrEmpty(title))
			return this;
		this.title = title;
		return this;
	}
	
	public DiscordEmbed setUrl(String url) {
		if(StringUtils.isNullOrEmpty(url))
			return this;
		this.url = url;
		return this;
	}
	
	public DiscordEmbed setDescription(String description) {
		if(StringUtils.isNullOrEmpty(description))
			return this;
		this.description = description;
		return this;
	}
	
	public DiscordEmbed setColorHexdecimal(String hexColor) {
		if(hexColor.contains("#"))
			hexColor = hexColor.replace("#", "");
		Integer decimal = Integer.parseInt(hexColor.trim(), 16);
		this.color = decimal;
		return this;		
	}
	
	public DiscordEmbed setColor(int color) {
		this.color = color;
		return this;
	}
	
	public DiscordEmbed addField(String name, String value, boolean inline) {
		if(StringUtils.isNullOrEmpty(name) || StringUtils.isNullOrEmpty(value)) {
			System.out.println("Name or value is null or empty.");
			return this;
		}
		JsonObject field = new JsonObject()
			.put("name", name)
			.put("value", value)
			.put("inline", inline);
		fields.add(field);
		return this;
	}
	
	public DiscordEmbed setThumbnail(String url) {
		if(StringUtils.isNullOrEmpty(url))
			return this;
		this.thumbnail = new JsonObject();
		image.put("url", url);
		return this;
	}
	
	public DiscordEmbed setImage(String url) {
		if(StringUtils.isNullOrEmpty(url))
			return this;
		this.image = new JsonObject();
		image.put("url", url);
		return this;
	}
	
	public DiscordEmbed setFooter(String text, String icon_url) {
		this.footer = new JsonObject();
		if(!StringUtils.isNullOrEmpty(text))
			footer.put("text", text);
		if(!StringUtils.isNullOrEmpty(icon_url))
			footer.put("icon_url", icon_url);
		return this;
	}
	
	public JsonObject toJsonObject() {
		
		JsonObject embed = new JsonObject();
		
		if(author != null && !author.isEmpty())
			embed.put("author", author);
		
		if(!StringUtils.isNullOrEmpty(title))
			embed.put("title", title);
		
		if(!StringUtils.isNullOrEmpty(url))
			embed.put("url", url);
		
		if(!StringUtils.isNullOrEmpty(description))
			embed.put("description", description);
		
		if(color != null)
			embed.put("color", color);
		
		if(!this.fields.isEmpty()) {
			JsonArray fieldArray = new JsonArray();
			for (JsonObject field : this.fields) 
				if(field != null && !field.isEmpty())
					fieldArray.add(field);
			embed.put("fields", fieldArray);
		}
		
		if(thumbnail != null && !thumbnail.isEmpty())
			embed.put("thumbnail", thumbnail);
		
		if(image != null && !image.isEmpty())
			embed.put("image", image);
		
		if(footer != null && !footer.isEmpty())
			embed.put("footer", footer);
		
		return embed;
	}
	
}