package netty.syncresponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.CharsetUtil;
import netty.syncresponse.entity.Data;

import java.util.List;

/**
 * ByteToMessageCodec = MessageToByteEncoder + ByteToMessageDecoder
 *
 * 数据包格式
 * ---------------------------
 * | 4Bytes | 4Bytes |   ?   |
 * ---------------------------
 * | length | seqId  |  msg  |
 * ---------------------------
 */
public class Codec extends ByteToMessageCodec<Data> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Data data, ByteBuf out) throws Exception {
        byte[] msg = data.getMsg().getBytes(CharsetUtil.UTF_8);
        // 写入length（数据包总长度）
        out.writeInt(4 + 4 + msg.length);
        // 写入数据
        out.writeInt(data.getSeqId());
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // in默认会release
        if (in.readableBytes() >= (4 + 4)) {
            int length = in.readInt();
            int id = in.readInt();
            byte[] msg = new byte[length - 4 - 4];
            in.readBytes(msg);
            // out中的元素依次通过fireChannelRead()向后传递
            out.add(new Data(id, new String(msg, CharsetUtil.UTF_8)));
        }
    }

}
