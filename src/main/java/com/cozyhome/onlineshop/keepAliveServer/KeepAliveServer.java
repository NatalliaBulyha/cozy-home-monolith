package com.cozyhome.onlineshop.keepAliveServer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Server
public class KeepAliveServer {

	private final String keepAliveEndpoint = "http://localhost:8080/api/v1/ping";
	
	@Scheduled(fixedRate = 60000)
	public void sendKeepAliveRequest() {
		 try {
	            HttpClient client = HttpClients.createDefault();
	            HttpGet request = new HttpGet(keepAliveEndpoint);
	            HttpResponse response = client.execute(request);
	            int statusCode = response.getStatusLine().getStatusCode();
	            log.info("[ON sendKeepAliveRequest] :: Keep-alive response STATUS: {}", statusCode);
	        } catch (Exception e) {
	        	log.error(e.getMessage());
	        }
	}
}
