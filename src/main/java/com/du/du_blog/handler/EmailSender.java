package com.du.du_blog.handler;

import com.alibaba.fastjson.JSON;
import com.du.du_blog.constant.RabbitMQPreFixConst;
import com.du.du_blog.dto.EmailDTO;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * 获取消息队列的消息，并发送到指定邮箱
 */
@Component
@RabbitListener(queues = RabbitMQPreFixConst.EMAIL_QUEUE)
public class EmailSender {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String username;

    @RabbitHandler
    public void sendEmail(byte[] data){
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(emailDTO.getEmail());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        javaMailSender.send(message);
    }
}
