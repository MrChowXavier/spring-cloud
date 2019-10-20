package com.xq.learn.rabbit.controller;

import com.xq.learn.rabbit.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoqiang
 * @date 2019/10/20 11:43
 */
@RestController
@RequestMapping("send")
public class ProducerController
{
    @Autowired
    private MessageProducer messageProducer;

    @RequestMapping
    public ResponseEntity<String> send(@RequestParam("message") String message)
    {
        messageProducer.send(message);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseEntity<String> entity = new ResponseEntity<>("success", httpHeaders, HttpStatus.OK);
        
        return entity;
    }
}
