package com.jcondotta.banking.recipients.infrastructure.bankaccount.config.kafka;

import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Bean
  public ConsumerFactory<String, Object> consumerFactory(KafkaProperties properties) {
    return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      ConsumerFactory<String, Object> consumerFactory) {

    var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }
}