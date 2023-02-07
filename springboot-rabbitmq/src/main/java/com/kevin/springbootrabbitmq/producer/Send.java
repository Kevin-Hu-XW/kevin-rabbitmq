package com.kevin.springbootrabbitmq.producer;

import com.kevin.springbootrabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Send {
 
    @Autowired
    RabbitTemplate rabbitTemplate;
    
    @Test
    public void sendMsgByTopics(){
 
        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */
        for (int i=0;i<5;i++){
            String message = "恭喜您，注册成功！userid="+i;
            /**
             * rabbitTemplate.convertAndSend方法是给指定的队列发送消息
             * 第一个是**交换机(exchange)的名字,第二个是路由键(routing-key)的名字，第三个则为消息的内容
             */
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME,"topic.sms.email",message);
            System.out.println("Sent '" + message + "'");
        }
 
    }
}