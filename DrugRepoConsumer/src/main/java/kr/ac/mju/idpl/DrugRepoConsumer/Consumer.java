package kr.ac.mju.idpl.DrugRepoConsumer;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.util.ArrayList;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Consumer {
	
	static final String USER_NAME = "master";
	static final String PASSWORD = "1234";
	static final String VIRTUAL_HOST = "/";
	static final String HOST = "master";
	static final String QUEUE_NAME = "drug-queue";
	static final String RESULT_FILE_NAME = "DrugResult/Consumer-";
	static final int PORT = 5672;

	public static void main(final String[] args) throws Exception {
		// TODO Auto-generated method stub
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(USER_NAME);
		factory.setPassword(PASSWORD);
		factory.setVirtualHost(VIRTUAL_HOST);
		factory.setHost(HOST);
		factory.setPort(PORT);
		
		final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		
		channel.basicQos(1);
		
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		final PrintWriter pw = new PrintWriter(RESULT_FILE_NAME + args[0]);
		pw.println("Consumer-" + args[0] + " Start Time : " + (System.currentTimeMillis()/1000));
		
		channel.basicConsume(QUEUE_NAME, false, "consumerTag", new DefaultConsumer(channel) {
			int taskCount=0;
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				// TODO Auto-generated method stub
				super.handleDelivery(consumerTag, envelope, properties, body);
				long deliveryTag = envelope.getDeliveryTag();
				String temp = new String(body, "UTF-8");
				System.out.println(temp + " Received...");
				
				try {
					String[] message = temp.split(" ");
					String command = "sudo ./autodock-vina.sh ligand/" + message[0] + " pockets/" + message[1] + " scPDB_coordinates.tsv";
					System.out.println(command);
					new ProcessBuilder("/bin/bash", "-c", command).start();
					taskCount++;
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				channel.basicAck(deliveryTag, false);
				
				if(channel.messageCount(QUEUE_NAME) == 0) {
					pw.println("Consumer-" + args[0] + " End Time : " + (System.currentTimeMillis()/1000));
					pw.println("Task Count : " + taskCount);
					pw.close();
					connection.close();
					System.out.println("Connection Closed...");
				}
			}
			
		});
	}

}
