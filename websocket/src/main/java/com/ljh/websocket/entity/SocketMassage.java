package com.ljh.websocket.entity;

/**
 * @author Liujihong
 * @version 1.0
 * @date 2019/9/13 14:58
 */

import lombok.Data;

/**
 * 创建消息对象
 * 这里就不能简单的文本消息进行发送用json格式的方式进行发送
 * 对象的属性包含消息接受者，消息发送者，消息的类型(单聊还是群聊)，还是消息
 */
@Data
public class SocketMassage {
    private int type;//消息发送的类型 1：单聊  0：群聊
    private String fromUser;//发送者
    private String toUser;//接受者
    private String msg;//消息
}
