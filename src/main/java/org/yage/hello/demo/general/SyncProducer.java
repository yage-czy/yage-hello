package org.yage.hello.demo.general;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

/**
 *
 */
@Slf4j
public class SyncProducer {

    public static final String DEMO_PREFIX = "demo-general-";

    /**
     * @param args
     * @throws MQClientException
     * @throws UnsupportedEncodingException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQBrokerException
     */
    public static void main(String[] args) throws
            MQClientException,
            UnsupportedEncodingException,
            RemotingException,
            InterruptedException,
            MQBrokerException {

        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(DEMO_PREFIX + "producer-group");
        defaultMQProducer.setNamesrvAddr("localhost:9876");
        defaultMQProducer.start();

        for (int i = 0; i < 10; i++) {

            String text = DEMO_PREFIX + "Hello RocketMQ - " + i;
            byte[] bytes = text.getBytes(RemotingHelper.DEFAULT_CHARSET);
            Message message = new Message(
                    "producer-group-topic",
                    "TagA",
                    bytes);

            SendResult sendResult = defaultMQProducer.send(message);
            log.info("sendResult{}", sendResult);
        }
    }
}
