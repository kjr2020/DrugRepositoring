package kr.ac.mju.idpl.DrugRepoProducer;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	static final String USER_NAME = "master";
	static final String PASSWORD = "1234";
	static final String VIRTUAL_HOST = "/";
	static final String HOST = "master";
	static final String WORKLOAD_FILE_NAME = "DrugRepoWorkload";
	static final String QUEUE_NAME = "drug-queue";
	static final String RESULT_FILE_NAME = "DrugResult/Producer";
	static final int PORT = 5672;

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(USER_NAME);
		factory.setPassword(PASSWORD);
		factory.setVirtualHost(VIRTUAL_HOST);
		factory.setHost(HOST);
		factory.setPort(PORT);
		//BufferedReader br = new BufferedReader(new FileReader(WORKLOAD_FILE_NAME));
		
		Random rand =new Random();
		int lineCount;
		String line;
		
		Stream<String> stream = Files.lines(Paths.get("DrugRepoWorkload"));
		lineCount = (int) stream.count();
		stream.close();
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel(); 
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		long startTime = System.currentTimeMillis();
		for(int i = 0 ; i < 100 ; i++) {
			Stream<String> streamLine = Files.lines(Paths.get("DrugRepoWorkload"));
			line = streamLine.skip(rand.nextInt(lineCount)).findFirst().get();
			channel.basicPublish("", QUEUE_NAME, null, line.getBytes("UTF-8"));
			System.out.println(i + " " + line);
			streamLine.close();
		}
		PrintWriter pw = new PrintWriter(RESULT_FILE_NAME);
		pw.println("Rabbit Producer : " + (System.currentTimeMillis() - startTime));
		pw.close();
		connection.close();
	} 

}
