package nl.tudelft.sem.template.example.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "exampleTopic", groupId = "group1")
    void listener(String data){
        System.out.println("Received " + data);
    }
}
