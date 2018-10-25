package netty.im.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务Handler接口
 */
@Slf4j
public abstract class MessageBusinessHandler {

    public abstract void messageHandle(ChannelHandlerContext ctx, ByteBuf in);

    protected byte[] msgAnalysis(Channel channel, ByteBuf in) {
        log.debug("{}-Packet[length:{} secret:{} version:{} commandId:{} seqId:{}]",
                channel, in.readInt(), in.readInt(), in.readByte(), in.readByte(), in.readInt());
        byte[] msg = new byte[in.readableBytes()];
        in.readBytes(msg);
        return msg;
    }

}
