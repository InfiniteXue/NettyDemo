package netty.im.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.Constant;
import netty.im.server.ChannelCache;

@Slf4j
@ChannelHandler.Sharable
public class BaseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} active.", ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.markReaderIndex();
        // 校验数据包长度
        if (byteBuf.readableBytes() >= Constant.PACKET_FIXED_LENGTH) {
            // 跳过length
            byteBuf.skipBytes(4);
            // 校验密钥
            if (Constant.SECRET_DEFAULT.equals(byteBuf.readInt())) {
                byteBuf.resetReaderIndex();
                ctx.fireChannelRead(msg);
            } else {
                log.warn("{} secret error.", channel);
                channel.close();
            }
        } else {
            log.warn("{} illegal packet.", channel);
            channel.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (channel.hasAttr(Constant.LOGIN_ATTR_KEY)) {
            // 连接断开时，清除客户端登录标识
            channel.attr(Constant.LOGIN_ATTR_KEY).set(null);
        }
        if (channel.hasAttr(Constant.SESSION_ATTR_KEY)) {
            // 连接断开时，清除服务端映射缓存和会话信息
            ChannelCache.getInstance().remove(channel.attr(Constant.SESSION_ATTR_KEY).get().getUsername());
            channel.attr(Constant.SESSION_ATTR_KEY).set(null);
        }
        log.debug("{} inactive.", channel);
        ctx.fireChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 读空闲则close当前channel
            if (state == IdleState.READER_IDLE) {
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} throws an exception.", ctx.channel(), cause);
        ctx.channel().close();
    }

}
