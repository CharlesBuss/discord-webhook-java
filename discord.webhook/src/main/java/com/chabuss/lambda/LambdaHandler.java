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
	//private String colorNewBranch = "#00c4b3";
	
	@Override public Void handleRequest(CodeCommitEvent input, Context context) {
		
		this.getColors();
		Record record = input.getRecords().get(0);

		// GET REPOSITORY NAME
		String repositoryName = record.getEventSourceArn().substring(record.getEventSourceArn().lastIndexOf(":")+1);
		
		DiscordHookMessage message = new DiscordHookMessage()
				.setUserName(repositoryName);
		
		DiscordEmbed embed = new DiscordEmbed()
				.setColorHexdecimal(colorCommit);
		message.addEmbed(embed);
		
		for(Reference reference : record.getCodeCommit().getReferences()) {
			
			embed.setDescription("[["+repositoryName+":"+reference.getRef()+"]("+this.buildRepositoryUrl(repositoryName, record.getAwsRegion())+")]");
			
			Commit commit = this.getCommit(reference.getCommit(), repositoryName);
			System.out.println("Commit: "+Json.encode(commit));
			
			String commit_url = this.buildCommitUrl(repositoryName, commit.getCommitId(), record.getAwsRegion());
			
			embed.setAuthor(commit.getCommitter().getName(), null, null);
			embed.addField("Commit from "+commit.getCommitter().getName()+" <"+commit.getCommitter().getEmail()+">",
					"(["+commit.getCommitId().substring(0, 6)+"]("+commit_url+")) "+commit.getMessage(), 
					false);
			break;
		}

		embed.setFooter("AWS Code Commit | region "+record.getAwsRegion(), null);
		
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

	private String buildCommitUrl(String repositoryName, String commitId, String region) {
		 return "https://"+region+".console.aws.amazon.com/codesuite/codecommit/repositories/"+
			repositoryName+
			"/commit/"+
			commitId+
			"?region="+
			region;
	}
	
	private String buildRepositoryUrl(String repositoryName, String region) {
		return "https://"+region+".console.aws.amazon.com/codesuite/codecommit/repositories/"+
				repositoryName+
				"/branches?region="+
				region;
	}
}