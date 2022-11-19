package com.canela.service.commchannels.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canela.service.commchannels.service.EmailSenderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



//communicationChannelsService
//uri: http://10.0.0.0:9009/

@RestController
@RequestMapping(value = "/api/communication-channels")
public class SendMovementsController {
	
	@Autowired
    private EmailSenderService service;
	
	 @PostMapping(value = "/sendMovements")
	    public String servicePaymentSavings (@RequestBody Request request){
		 
		 String url = "http://localhost:3001/graphql";
		 String operation = "getMovementsByOriginAccount";
		 String query = "query{getMovementsByOriginAccount(account_id:\""+request.accountId+"\"){\n"
		 		+ "  origin_account\n"
		 		+ "  destination_account\n"
		 		+ "  amount\n"
		 		+ "  movement_date\n"
		 		+ "	}  \n"
		 		+ "}";
		 
		 try {
			 CloseableHttpClient client = HttpClientBuilder.create().build();
		        HttpGet requestGraphQL = new HttpGet(url);
		        URI uri = new URIBuilder(requestGraphQL.getURI())
		                .addParameter("query", query)
		                .build();
		        requestGraphQL.setURI(uri);
		        HttpResponse response =  client.execute(requestGraphQL);
		        InputStream inputResponse = response.getEntity().getContent();
		        String actualResponse = new BufferedReader(
		                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
		                .lines()
		                .collect(Collectors.joining("\n"));

		        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
		        
		        JsonNode Movements = node.get("data");
		        if(Movements == null) {
		        	return Movements.toString();
		        }

		        	JsonNode MovementsAccount = Movements.get(operation);
		        	StringBuilder sb = new StringBuilder(MovementsAccount.toString());      
		        	
		        	 service.sendSimpleEmail(request.userEmail, " Movimientos: " + sb , "Extracto Canela Bank");
		        	return  MovementsAccount.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		 
		 
	 }
	
	
	 static class Request {
	        private String userEmail;
	        private String accountId;
	        
			public String getUserEmail() {
				return userEmail;
			}
			public void setUserEmail(String userEmail) {
				this.userEmail = userEmail;
			}
			public String getAccountId() {
				return accountId;
			}
			public void setAccountId(String accountId) {
				this.accountId = accountId;
			}

	    }

}
