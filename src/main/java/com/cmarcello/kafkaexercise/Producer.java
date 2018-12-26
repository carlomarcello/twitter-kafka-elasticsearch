package com.cmarcello.kafkaexercise;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
	private final String BOOTSTRAP_SERVERS = "127.0.0.1:9092";
	private Properties properties;
	private KafkaProducer<String, String> producer;
	private String topic;

	public Producer(String bootstrapServers, String topic) {
		// create producer properties
		properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers != null ? bootstrapServers : BOOTSTRAP_SERVERS);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		this.topic = topic;
		
		// create the producer
		producer = new KafkaProducer<String, String>(properties);
	}

	public void send(String message) {
		// create a producer record
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);

		producer.send(record);

		// flush data
		producer.flush();		
	}
	
	public void close() {
		// flush and close producer
		producer.close();
	}
}
