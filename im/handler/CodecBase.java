package netty.im.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.Constant;
import netty.im.entity.PacketBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据包格式
 * ----------------------------------------------------------
 * | 4Bytes | 4Bytes |  1Byte  |   1Byte   | 4Bytes |   ?   |
 * ----------------------------------------------------------
 * | length | secret | version | commandId | seqId  |  msg  |
 * ----------------------------------------------------------
 */
@Slf4j
public class CodecBase extends ByteToMessageCodec<PacketBase> {

    protected Map<Byte, MessageBusinessHandler> handlerMap = new HashMap<>();

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketBase packet, ByteBuf out) throws Exception {
        byte[] msg = JSON.toJSONBytes(packet);
        int length = Constant.PACKET_FIXED_LENGTH + msg.length;
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer(length);
        byteBuf.writeInt(length);
        byteBuf.writeInt(packet.getSecret());
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(packet.getCommandId());
        byteBuf.writeInt(packet.getSeqId());
        byteBuf.writeBytes(msg);
        out.writeBytes(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        // 跳过length/secret/version
        in.skipBytes(4 + 4 + 1);
        byte commandId = in.readByte();
        MessageBusinessHandler handler = handlerMap.get(commandId);
        if (handler != null) {
            in.resetReaderIndex();
            handler.messageHandle(ctx, in);
        } else {
            log.warn("{} illegal command.", ctx.channel());
            ctx.channel().close();
        }
    }

}
