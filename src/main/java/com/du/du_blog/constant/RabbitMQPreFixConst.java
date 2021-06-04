package com.du.du_blog.constant;

/**
 * mq常量
 */
public class RabbitMQPreFixConst {
    /**
     * maxwell交换机
     */
    public static final String MAXWELL_EXCHANGE = "maxwell";

    /**
     * maxwell队列
     */
    public static final String MAXWELL_QUEUE = "article";

    /**
     * email交换机
     */
    public static final String EMAIL_EXCHANGE = "fanout_email_exchange";

    /**
     * 邮件队列
     */
    public static final String EMAIL_QUEUE = "email_queue";

}
