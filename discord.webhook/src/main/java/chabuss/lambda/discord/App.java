package chabuss.lambda.discord;

import chabuss.discord.DiscordEmbed;
import chabuss.discord.DiscordHookMessage;
import io.vertx.core.json.Json;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        DiscordEmbed embed = new DiscordEmbed()
        		.setAuthor("charlinho", null, null)
        		.setColorHexdecimal("#ff0000")
        		.setDescription("Here goes the description.")
        		.setFooter("Charlinhos Discord Embed", null)
        		.addField("Field Name", "Field Value", true);
        
        System.out.println(embed.toJsonObject().encodePrettily());
        
        DiscordHookMessage message = new DiscordHookMessage()
        		.setContent("Here is the content.")
        		.setUserName("BOT NAME")
        		.addEmbed(embed);
        
        
        System.out.println(message.toJsonObject().encodePrettily());
        
    }
}
