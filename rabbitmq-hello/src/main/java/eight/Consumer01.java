package eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitUtils;

import java.util.HashMap;

/*
    死信队列
    消费者1
 */
public class Consumer01 {
    //普通交换机名称
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //普通队列名称
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信队列名称
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //普通队列的声明
        HashMap<String, Object> arguments = new HashMap<>();
        //正常队列设置死信交换机
        //设置过期时间
        //arguments.put("x-message-ttl",1000000);
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //正常队列设置死信routingKey
        arguments.put("x-dead-letter-routing-key","lisi");
        //设置正常队列长度限制
        //arguments.put("x-max-length",6);
        channel.queueDeclare(NORMAL_QUEUE,true,false,false,arguments);

        //死信队列
        channel.queueDeclare(DEAD_QUEUE,true,false,false,null);

        //队列绑定
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");

        System.out.println("等待消息....");

        //接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {   String message = new String(delivery.getBody(), "UTF-8");
            if (message.equals("info5")){
                System.out.println("Consumer01接收的消息："+message+"是被C1拒绝的");
                //消息拒绝，并且不放回原队列，放入死信队列
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
            }else{
                System.out.println("Consumer01接收的消息："+message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }

        };
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,consumerTag -> { });

    }
}
