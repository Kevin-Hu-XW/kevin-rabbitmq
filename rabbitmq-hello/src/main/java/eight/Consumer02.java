package eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitUtils;

import java.util.HashMap;

/*
    死信队列
    消费者2
 */
public class Consumer02 {

    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";

    //死信队列名称
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);




        //死信队列
        channel.queueDeclare(DEAD_QUEUE,true,false,false,null);

        //队列绑定

        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");

        System.out.println("等待消息....");

        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {   String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Consumer02接收的消息："+message);
        };
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag -> { });

    }
}
