package work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitUtils;
import utils.SleepUtils;

/**
    生产者发送大量的消息
 */
public class Task01 {
    private static final String QUEUE_NAME = "hello";

    //发送大量的消息
    public static void main(String[] args) throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        // 循环发布任务
        for (int i = 0; i <30; i++) {
            // 消息内容
            String message = "task .. " + i;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("Sent '" + message + "'");
            SleepUtils.sleep(1);
        }
        channel.close();
        connection.close();
    }
}
