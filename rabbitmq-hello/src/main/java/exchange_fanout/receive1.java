package exchange_fanout;

import com.rabbitmq.client.*;
import utils.RabbitUtils;

import java.io.IOException;

public class receive1 {

    //短信队列
    private final static String QUEUE_NAME = "fanout_exchange_queue_sms";
    private final static String EXCHANGE_NAME = "fanout_exchange";
    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //把队列绑定我们的 exchange
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        // 定义队列的消费者
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息，并且处理，这个方法类似事件监听，如果有消息的时候，会被自动调用
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // body 即消息体
                String msg = new String(body);
                System.out.println(" [短信服务] received1 : " + msg + "!");
            }
        };
        // 监听队列，自动返回完成
        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}
