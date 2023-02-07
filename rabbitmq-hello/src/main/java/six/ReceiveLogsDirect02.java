package six;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitUtils;

public class ReceiveLogsDirect02 {
    private static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //声明队列
        channel.queueDeclare("disk",false,false,false,null);
        //把该临时队列绑定我们的 exchange 其中 routingkey(也称之为 binding key)
        channel.queueBind("disk", EXCHANGE_NAME, "error");

        System.out.println("ReceiveLogsDirect02等待接收消息,把接收到的消息打印在屏幕 ........... ");
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("控制台打印接收到的消息"+message);
        };
        channel.basicConsume("disk", true, deliverCallback, consumerTag -> { });
    }
}
