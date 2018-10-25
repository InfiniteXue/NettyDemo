package netty.im.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import netty.im.handler.MessageBusinessHandler;

public class ActiveTestResponseHandler extends MessageBusinessHandler {

    @Override
    public void messageHandle(ChannelHandlerContext ctx, ByteBuf in) {
        byte[] msg = msgAnalysis(ctx.channel(), in);
    }

}
