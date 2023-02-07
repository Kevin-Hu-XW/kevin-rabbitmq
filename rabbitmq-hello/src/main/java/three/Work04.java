package three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitUtils;
import utils.SleepUtils;

/*
    消费者
    消息在手动应答时不丢失，并放回队列重新消费
 */
public class Work04 {

    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        System.out.println("C2等待的时间比较长....");
        //推送的消息如何进行消费的接口的回调
        DeliverCallback deliverCallback =(s, delivery) ->{
            String message = new String(delivery.getBody());
            SleepUtils.sleep(30);
            System.out.println("接收到的消息:"+message);

            //手动应答
            /*
                1、消息的标记tag
                2、是否批量应答   false:不批量应答   true：批量应答
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        //取消消费的回调接口，如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = s -> {
            System.out.println("消息被中断");
        };
        //设置不公平分发
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);

    }
}
