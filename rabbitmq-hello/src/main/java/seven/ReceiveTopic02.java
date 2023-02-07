package seven;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitUtils;

/*
    消费者C2
 */
public class ReceiveTopic02 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //声明队列
        String queueName = "Q2";
        //声明队列
        channel.queueDeclare(queueName,false,false,false,null);
        //把该临时队列绑定我们的 exchange 其中 routingkey(也称之为 binding key)
        channel.queueBind(queueName, EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(queueName, EXCHANGE_NAME, "lazy.#");
        System.out.println("等待接收消息...");
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {   String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接收队列："+queueName+"，绑定键"+delivery.getEnvelope().getRoutingKey());
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
