package com.xuecheng.manage_media.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String EX_MEDIA_PROCESSTASK = "ex_media_processor";


    @Bean(EX_MEDIA_PROCESSTASK)
    public Exchange getExchange(){
        return  ExchangeBuilder.directExchange(EX_MEDIA_PROCESSTASK).durable(true).build();
    }


}
