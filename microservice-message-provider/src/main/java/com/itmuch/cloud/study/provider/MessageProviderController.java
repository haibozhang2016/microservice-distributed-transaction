package com.itmuch.cloud.study.provider;

import com.itmuch.cloud.study.utils.MQConstants;
import com.itmuch.cloud.study.utils.RabbitMetaMessage;
import com.itmuch.cloud.study.utils.RabbitSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageProviderController {

  /** test if can get value from config center **/
  @Value("${rabbitmq.address}")
  private String rabbitmqAddress;
  
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RedisTemplate redisTemplate;


  Logger logger = LoggerFactory.getLogger(getClass());
  
  
  
  @GetMapping("testconfig")
  public String testconfig() throws Exception {
	  return rabbitmqAddress;
  }
  
  @GetMapping("testmq")
  public String testMessage() throws Exception {
	  /** 生成一个发送对象 */
		RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
		/**设置交换机 */
		rabbitMetaMessage.setExchange(MQConstants.BUSINESS_EXCHANGE);
		/**指定routing key */
		rabbitMetaMessage.setRoutingKey(MQConstants.BUSINESS_KEY);
		/** 设置需要传递的消息体,可以是任意对象 */
		rabbitMetaMessage.setPayload("the message you want to send");

		/** 发送消息 */
		RabbitSender.send(rabbitMetaMessage, redisTemplate, rabbitTemplate, logger);
		
		return "success";
  }

}
