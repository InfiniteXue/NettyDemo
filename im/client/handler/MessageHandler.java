package netty.im.client.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.im.entity.MessagePacket;
import netty.im.handler.MessageBusinessHandler;

public class MessageHandler extends MessageBusinessHandler {

    @Override
    public void messageHandle(ChannelHandlerContext ctx, ByteBuf in) {
        byte[] msg = msgAnalysis(ctx.channel(), in);
        MessagePacket packet = JSON.parseObject(msg, MessagePacket.class);
        String connUsername = packet.getConnectionUsername();
        String message = packet.getMessage();
        System.out.println("[" + connUsername + "] " + message);
    }

}