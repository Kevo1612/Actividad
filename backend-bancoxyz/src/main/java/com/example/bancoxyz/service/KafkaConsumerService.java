package com.example.bancoxyz.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = "${spring.kafka.topic.transacciones}",
            groupId = "bank-service-group"
    )
    public void consumirEvento(String mensaje) {

        System.out.println("========================================");
        System.out.println("EVENTO KAFKA RECIBIDO");
        System.out.println("Topic: bank-transacciones");
        System.out.println("Mensaje: " + mensaje);
        System.out.println("========================================");
    }
}