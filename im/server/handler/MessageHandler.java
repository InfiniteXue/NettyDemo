package netty.im.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.Constant;
import netty.im.entity.MessagePacket;
import netty.im.handler.MessageBusinessHandler;
import netty.im.server.ChannelCache;

/**
 * 服务端消息处理类
 */
@Slf4j
public class MessageHandler extends MessageBusinessHandler {

    private static final MessageHandler INSTANCE = new MessageHandler();

    public static MessageHandler getInstance() {
        return INSTANCE;
    }

    private MessageHandler() {
    }

    @Override
    public void messageHandle(ChannelHandlerContext ctx, ByteBuf in) {
        byte[] msg = msgAnalysis(ctx.channel(), in);
        MessagePacket packet = JSON.parseObject(msg, MessagePacket.class);
        String connUsername = packet.getConnectionUsername();
        Channel connChannel = ChannelCache.getInstance().get(connUsername);
        if (connChannel == null) {
            ctx.writeAndFlush(new MessagePacket(Constant.SEQ.incrementAndGet(), "系统", connUsername + "不在线"));
        } else {
            // 转发至相应的客户端
            connChannel.writeAndFlush(new MessagePacket(Constant.SEQ.incrementAndGet(), ctx.channel().attr(Constant.SESSION_ATTR_KEY).get().getUsername(), packet.getMessage()));
        }
    }

}
