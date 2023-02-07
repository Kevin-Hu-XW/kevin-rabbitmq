package work.prefetchCount;

import com.rabbitmq.client.*;
import utils.RabbitUtils;
import utils.SleepUtils;

import java.io.IOException;

public class Work02 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        try {
            // 获取到连接
            Connection connection = RabbitUtils.getConnection();
            // 获取通道
            Channel channel = connection.createChannel();
            //设置每个消费者同时只能处理一条消息，在手动ack下才生效
            channel.basicQos(1);
            //实现消费方法
            DefaultConsumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    SleepUtils.sleep(3);
                    //交换机
                    String exchange = envelope.getExchange();
                    //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    // body 即消息体
                    String msg = new String(body,"utf-8");
                    System.out.println("work02 : " + msg );
                    channel.basicAck(deliveryTag,false);
                }
            };
            channel.basicConsume(QUEUE_NAME,false,consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
