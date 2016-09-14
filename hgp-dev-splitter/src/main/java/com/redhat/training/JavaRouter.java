package com.redhat.training;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class JavaRouter {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		context.addRoutes(new JavaRouteBuilder());
		context.start();
		Thread.sleep(5000);
		
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination queue = session.createQueue("orders");
		MessageConsumer consumer = session.createConsumer(queue);
		Message message = consumer.receive(10);
		while (message != null) {
			if (message instanceof TextMessage) {
				String text = ((TextMessage) message).getText();
				System.out.println("**************** Got Message: " + text);
			} 
			message = consumer.receive(10);
		}
		
		consumer.close();
		session.close();
		connection.stop();
		context.stop();
		
		
	}

}
