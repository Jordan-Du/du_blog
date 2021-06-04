package com.du.du_blog.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.du.du_blog.dao.ChatRecordMapper;
import com.du.du_blog.dto.ChatRecordDTO;
import com.du.du_blog.dto.RecallMessageDTO;
import com.du.du_blog.dto.WebsocketMessageDTO;
import com.du.du_blog.pojo.ChatRecord;
import com.du.du_blog.utils.DateUtils;
import com.du.du_blog.utils.IpUtils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.du.du_blog.enums.ChatTypeEnum.*;

@NoArgsConstructor
@ServerEndpoint(value = "/websocket",configurator = WebSocketServiceImpl.ChatConfigurator.class)
@Service
public class WebSocketServiceImpl {

    //用户自身Session
    private Session session;

    //在线用户集合
    private static CopyOnWriteArraySet<WebSocketServiceImpl> webSocketServices=new CopyOnWriteArraySet();


    private static ChatRecordMapper chatRecordMapper;

    @Autowired
    public void setChatRecordMapper(ChatRecordMapper chatRecordMapper) {
        this.chatRecordMapper = chatRecordMapper;
    }



    @OnOpen
    public  void onOpen(Session session, EndpointConfig endpointConfig) throws IOException, InterruptedException {
        //加入在线用户集合
        this.session=session;
        webSocketServices.add(this);
        //更新在线人数
        updateOnlineCount();
        //加载聊天记录
        ChatRecordDTO chatRecordDTO = listChatRecords(endpointConfig);
        WebsocketMessageDTO websocketMessageDTO = WebsocketMessageDTO.builder()
                .type(HISTORY_RECORD.getType())
                .data(chatRecordDTO)
                .build();
        synchronized (session){
            session.getBasicRemote().sendText(JSON.toJSONString(websocketMessageDTO));
        }
    }

    @OnMessage
    public void OnMessage(String message,Session session) throws IOException {
        WebsocketMessageDTO websocketMessageDTO = JSON.parseObject(message, WebsocketMessageDTO.class);
        switch (Objects.requireNonNull(getChatType(websocketMessageDTO.getType()))){
            case SEND_MESSAGE://发送消息
                //获取消息
                ChatRecord chatRecord = JSON.parseObject(JSON.toJSONString(websocketMessageDTO.getData()), ChatRecord.class);
                //插入数据库
                chatRecordMapper.insert(chatRecord);
                WebsocketMessageDTO messageDTO = WebsocketMessageDTO.builder()
                        .type(SEND_MESSAGE.getType())
                        .data(chatRecord)
                        .build();
                //群发消息
                for (WebSocketServiceImpl webSocketService : webSocketServices) {
                    synchronized (webSocketService.session) {
                        webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(messageDTO));
                    }
                }
                break;
            case RECALL_MESSAGE:
                //获取撤销消息
                RecallMessageDTO data =JSON.parseObject(JSON.toJSONString(websocketMessageDTO.getData()),RecallMessageDTO.class);
                //判断消息是否属于该用户
                ChatRecord chatRecord1 = chatRecordMapper.selectOne(new LambdaQueryWrapper<ChatRecord>()
                        .eq(ChatRecord::getId, data.getId()));
                if(chatRecord1.getId()!=null&&chatRecord1.getUserId()==data.getUserId()){
                    //删除消息
                    chatRecordMapper.deleteById(data.getId());
                    for (WebSocketServiceImpl webSocketService : webSocketServices) {
                        synchronized (webSocketService.session) {
                            webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(websocketMessageDTO));
                        }
                    }
                }
                break;
            case HEART_BEAT:
                // 心跳消息
                websocketMessageDTO.setData("pong");
                session.getBasicRemote().sendText(JSON.toJSONString(JSON.toJSONString(websocketMessageDTO)));
            default:
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // 更新在线人数
        webSocketServices.remove(this);
        updateOnlineCount();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();

    }



    //握手建立连接时，获取客户端的真实ip
    public static class ChatConfigurator extends ServerEndpointConfig.Configurator{
        public static String HEADER_NAME = "HOST";
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            String ipAddress = null;
            try {
                Map<String, List<String>> headers = request.getHeaders();
                String firstFoundHeader = request.getHeaders().get(HEADER_NAME.toLowerCase()).get(0);
                int index = firstFoundHeader.indexOf(':');
                sec.getUserProperties().put(HEADER_NAME, firstFoundHeader.substring(0, index));
            } catch (Exception e) {
                sec.getUserProperties().put(HEADER_NAME,"未知IP");
            }
        }
    }

    /**
     * 加载聊天记录
     * @param endpointConfig
     * @return
     */
    private ChatRecordDTO listChatRecords(EndpointConfig endpointConfig){
        List<ChatRecord> chatRecords = chatRecordMapper.selectList(new LambdaQueryWrapper<ChatRecord>()
                .ge(ChatRecord::getCreateTime, DateUtils.getBeforeHourTime(12)));
        String ipAddr = endpointConfig.getUserProperties().get(ChatConfigurator.HEADER_NAME).toString();
        return ChatRecordDTO.builder()
                .chatRecordList(chatRecords)
                .ipAddr(ipAddr)
                .ipSource(IpUtils.getIpSource(ipAddr))
                .build();
    }

    /**
     * 更新在线人数
     */
    private void updateOnlineCount(){
        WebsocketMessageDTO websocketMessageDTO = WebsocketMessageDTO.builder()
                .type(ONLINE_COUNT.getType())
                .data(webSocketServices.size())
                .build();
        webSocketServices.forEach(webSocketService->{
            synchronized (webSocketService.session){
                try {
                    webSocketService.session.getBasicRemote().sendText(JSON.toJSONString(websocketMessageDTO));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
