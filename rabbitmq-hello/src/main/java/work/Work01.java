package work;

import com.rabbitmq.client.*;
import utils.RabbitUtils;
import utils.SleepUtils;

import java.io.IOException;

public class Work01 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        try {
            // 获取到连接
            Connection connection = RabbitUtils.getConnection();
            // 获取通道
            Channel channel = connection.createChannel();
            //推送的消息如何进行消费的接口的回调
            //实现消费方法
            DefaultConsumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    //交换机
                    String exchange = envelope.getExchange();
                    //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    // body 即消息体
                    String msg = new String(body,"utf-8");
                    System.out.println("work01 : " + msg );

                }
            };
            channel.basicConsume(QUEUE_NAME,true,consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
