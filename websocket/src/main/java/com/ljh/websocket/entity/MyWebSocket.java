package com.ljh.websocket.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.xml.ws.WebEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Liu, jihong
 * @version 1.0
 * @date 2019-09-12 上午 11:01
 */

/**
 * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，
    所以可以用一个静态set保存起来。
 */
@Component
@ServerEndpoint(value = "/websocket/{name}")
public class MyWebSocket {
    //昵称
    private String name;
    //与某个客户端的连接会话，需要通过session来给客户端发送数据
    private Session session;
    //用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet=new CopyOnWriteArraySet<>();
    //用来记录sessionId与session绑定
    private static Map<String,Session> map=new HashMap<>();

    /**
     * 连接成功调用此方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name){
          this.session=session;
          this.name=name;
          webSocketSet.add(this);//加入set中
          map.put(session.getId(),session);//绑定
        System.out.println("有新连接加入！当前在线人数是："+webSocketSet.size());
        //创建的一个session对象，服务端在像客户端传递信息
        this.session.getAsyncRemote().sendText("恭喜"+name+"成功连接上WebSocket(其频道号："+session.getId()+")-->当前在线人数为："+webSocketSet.size());
    }


    /**
     * 连接关闭调用此方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);//将用户关闭连接过后就从mywebsocket对象中删除
        System.out.println(this.name+"有一连接关闭，当前在线人数为："+webSocketSet.size());
    }

    /**
     * 接收客户端发送过来的消息     客户端发过来的json数据有消息，还有接受者信息sessionid
     * @param message 用户的消息内容
     * @param session 用户的session
     */
    @OnMessage
    public void onMessage(String message,Session session){
        ObjectMapper objectMapper=new ObjectMapper();
        SocketMassage socketMassage;
        try {
            //message是前端传过来的消息所有数据，类型是json，转为socketMessage对象
           socketMassage= objectMapper.readValue(message,SocketMassage.class);
            if(socketMassage.getType()==1){
                socketMassage.setFromUser(session.getId());//存入发送者的sessionid
                Session fromSession = map.get(socketMassage.getFromUser());//发送者的session
                Session toSession = map.get(socketMassage.getToUser());//接受者的session
                if(toSession!=null){
                    fromSession.getAsyncRemote().sendText(this.name+": "+socketMassage.getMsg());
                    toSession.getAsyncRemote().sendText(this.name+": "+socketMassage.getMsg());
                }else{
                    fromSession.getAsyncRemote().sendText("对方不在线，请稍后联系他");
                }
            }else{
                broadcast(socketMassage.getMsg());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发生错误是调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session,Throwable error){
        System.out.println("发生了错误");
        error.printStackTrace();
    }

    /**
     * 自定义群发消息
     * @param message
     */
    public void broadcast(String message){
        for (MyWebSocket socket:webSocketSet){
            socket.session.getAsyncRemote().sendText(this.name+":"+message);////异步发送消息
        }
    }

}
