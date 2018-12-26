package com.cmarcello.kafkaexercise;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/** 
 * Client for consuming Twitter's standard Streaming API based on the Hosebird Client (hbc)<br>
 * https://github.com/twitter/hbc
 * @author carlo 
 */
public class TwitterFilterStream {

	public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		
		// Add some track terms
		endpoint.trackTerms(Lists.newArrayList("new year"));

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
		// Authentication auth = new BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		Client client = new ClientBuilder()
				.hosts(Constants.STREAM_HOST)
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(queue))
				.build();

		// Establish a connection
		client.connect();

		// Creates a Kafka producer
		Producer producer = new Producer("localhost:9092", "tweets_by_subject");
				
		// TODO do something like this in another thread and delete the "for" code below
		// while (!client.isDone()) {
		//	  String msg = queue.take();
		//	  producer.send(msg);
		// }
		
		// Do whatever needs to be done with messages
		for (int msgRead = 0; msgRead < 1000; msgRead++) {
			String msg = queue.take();
			
			JSONTokener tokener = new JSONTokener(msg);
			JSONObject jsonTemp = new JSONObject(tokener);
			
			String createdAt;
			String userName;
			String text;
			JSONObject json;
			
			try {
				createdAt = jsonTemp.getString("created_at");
				JSONObject user = jsonTemp.getJSONObject("user");
				userName = user.getString("name");
				text = jsonTemp.getString("text");
				
				json = new JSONObject();
				json.put("createdAt", createdAt);
				json.put("user", userName);
				json.put("message", text);
				
				producer.send(json.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.err.println("JSON inválido: " + jsonTemp.toString());
			}
		}
		
		producer.close();
		client.stop();
	}

	public static void main(String[] args) {
		try {
			TwitterFilterStream.run(args[0], args[1], args[2], args[3]);			
			
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
}
