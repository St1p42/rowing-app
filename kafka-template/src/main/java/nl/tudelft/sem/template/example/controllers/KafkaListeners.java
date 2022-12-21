package nl.tudelft.sem.template.example.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "notification", groupId = "group1")
    void listener(String data){
        System.out.println("Received " + data + new Date());
    }
}
