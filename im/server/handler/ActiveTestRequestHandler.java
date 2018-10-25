package netty.im.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.im.entity.ActiveTestResponsePacket;
import netty.im.entity.Constant;
import netty.im.handler.MessageBusinessHandler;

public class ActiveTestRequestHandler extends MessageBusinessHandler {

    private static final ActiveTestRequestHandler INSTANCE = new ActiveTestRequestHandler();

    public static ActiveTestRequestHandler getInstance() {
        return INSTANCE;
    }

    private ActiveTestRequestHandler() {
    }

    @Override
    public void messageHandle(ChannelHandlerContext ctx, ByteBuf in) {
        byte[] msg = msgAnalysis(ctx.channel(), in);
        ctx.channel().writeAndFlush(new ActiveTestResponsePacket(Constant.SEQ.incrementAndGet()));
    }

}
