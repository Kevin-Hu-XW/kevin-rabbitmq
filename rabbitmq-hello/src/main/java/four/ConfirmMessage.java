package four;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import utils.RabbitUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/*
    发布确认模式
        1、单个确认                            477ms
        2、批量确认                             45ms
        3、异步批量确认   发布1000个异步确认消息,耗时31ms
 */
public class ConfirmMessage {

    public static final int Message_Count = 1000;
    public static void main(String[] args) throws Exception {
        //publishMessageIndividually();
        //publishMessageBatch();
        publishMessageAsync();
    }
    //单个确认
    public static void publishMessageIndividually() throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        String queue_name = UUID.randomUUID().toString();
        channel.queueDeclare(queue_name,true,false,false,null);
        //开启发确认
        channel.confirmSelect();
        long start = System.currentTimeMillis();
        for (int i = 0; i < Message_Count; i++) {
            String message = i+" ";
            channel.basicPublish("",queue_name,null,message.getBytes());
            //服务端返回false或者超时未返回，生产者可以重发消息
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发送成功......");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + Message_Count + "个单独确认消息,耗时" + (end - start) + "ms");
    }

    //批量确认
    public static void publishMessageBatch() throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        String queue_name = UUID.randomUUID().toString();
        channel.queueDeclare(queue_name,true,false,false,null);
        //开启发确认
        channel.confirmSelect();
        //批量确认消息的大小
        int batch = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < Message_Count; i++) {
            String message = i+" ";
            channel.basicPublish("",queue_name,null,message.getBytes());
            //判断消息达到100条时确认一次
            if (i%100==0){
                channel.waitForConfirms();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + Message_Count + "个批量确认消息,耗时" + (end - start) + "ms");
    }

    //异步确认
    public static void publishMessageAsync() throws Exception {
        // 获取到连接
        Connection connection = RabbitUtils.getConnection();
        // 获取通道
        Channel channel = connection.createChannel();
        String queue_name = UUID.randomUUID().toString();
        channel.queueDeclare(queue_name,true,false,false,null);
        //开启发确认
        channel.confirmSelect();
        //批量确认消息的大小
        int batch = 100;
        long start = System.currentTimeMillis();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        //消息确认成功，回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple)->{
            //2、删除已经确认的消息，就是未确认的
            if (multiple){
                ConcurrentNavigableMap<Long, String> comfirmedMap = outstandingConfirms.headMap(deliveryTag);
                comfirmedMap.clear();
            }
            else {
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息"+deliveryTag);
        };
        //消息确认失败，回调函数
        ConfirmCallback nackCallback = (deliveryTag, multiple)->{
            //3、打印未确认的消
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息"+message);
        };
        //准备消息监听器，那些消息成功了，那些消息失败了
        channel.addConfirmListener(ackCallback, nackCallback);  //异步通知
        for (int i = 0; i < Message_Count; i++) {
            String message = i+" ";
            channel.basicPublish("",queue_name,null,message.getBytes());
            //1、记录所有要发送消息的总和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);

        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + Message_Count + "个异步确认消息,耗时" + (end - start) + "ms");
    }
}
