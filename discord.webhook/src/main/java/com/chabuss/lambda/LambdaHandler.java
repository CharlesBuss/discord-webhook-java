package com.chabuss.lambda;

import com.amazonaws.services.codecommit.AWSCodeCommit;
import com.amazonaws.services.codecommit.AWSCodeCommitClientBuilder;
import com.amazonaws.services.codecommit.model.Commit;
import com.amazonaws.services.codecommit.model.GetCommitRequest;
import com.amazonaws.services.codecommit.model.GetCommitResult;
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

	AWSCodeCommit codeCommitClient = AWSCodeCommitClientBuilder.defaultClient();
	
	private String colorCommit    = "#24db00";
	private String colorNewBranch = "#00c4b3";
	
	@Override public Void handleRequest(CodeCommitEvent input, Context context) {
		
		this.getColors();
		Record record = input.getRecords().get(0);
		
		DiscordHookMessage message = new DiscordHookMessage();
		
		DiscordEmbed embed = new DiscordEmbed()
				.setColorHexdecimal(colorCommit);
		
		message.addEmbed(embed);
		
		// GET REPOSITORY NAME
		String repositoryName = record.getEventSourceArn().substring(record.getEventSourceArn().lastIndexOf(":")+1);
		
		for(Reference reference : record.getCodeCommit().getReferences()) {
			Commit commit = this.getCommit(reference.getCommit(), repositoryName);
			System.out.println("Commit: "+Json.encode(commit));
			embed.addField("User", commit.getCommitter().getName(), true)
				 .addField("Commit", commit.getCommitId(), true)
				 .addField("Comment", commit.getMessage(), false);
		}
		

		embed.addField("Region", record.getAwsRegion(), true)
			 .setFooter("AWS Code Commit", null);
		
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

	private Commit getCommit(String commit, String repositoryName) {
		GetCommitRequest request = new GetCommitRequest();
		request.setCommitId(commit);
		request.setRepositoryName(repositoryName);
		GetCommitResult result = codeCommitClient.getCommit(request);
		return result.getCommit();
	}
	
	private void getColors() {
		System.out.println("Getting colors.");
	}
}