package com.itmuch.cloud.study.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

/**
 * <p><b>Description:</b> RabbitMQ消息发送者
 * <p><b>Company:</b> 
 *
 * @author created by hongda at 13:03 on 2017-10-24
 * @version V0.1
 */

public class RabbitSender{

    /**
     * 发送MQ消息
     * @param rabbitMetaMessage Rabbit元信息对象，用于存储交换器、队列名、消息体
     * @return 消息ID
     * @throws JsonProcessingException 
     */
    public static String send(RabbitMetaMessage rabbitMetaMessage,RedisTemplate redisTemplate,
    			RabbitTemplate rabbitTemplate, Logger logger) throws JsonProcessingException {
        final String msgId = UUID.randomUUID().toString();
        
        // 放缓存
        redisTemplate.opsForHash().put(MQConstants.MQ_PRODUCER_RETRY_KEY, msgId, rabbitMetaMessage);
        MessagePostProcessor messagePostProcessor = (message) -> {
                message.getMessageProperties().setMessageId(msgId);
                // 设置消息持久化, 防止丢失
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(rabbitMetaMessage.getPayload());

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        try {
            rabbitTemplate.convertAndSend(rabbitMetaMessage.getExchange(), rabbitMetaMessage.getRoutingKey(),
            		message, messagePostProcessor, new CorrelationData(msgId));

            logger.info("发送消息，消息ID:{}", msgId);

            return msgId;
        } catch (AmqpException e) {
            throw new RuntimeException("发送RabbitMQ消息失败！", e);
        }
    }

}
