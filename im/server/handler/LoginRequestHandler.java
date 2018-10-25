package netty.im.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.Constant;
import netty.im.entity.LoginRequestPacket;
import netty.im.entity.LoginResponsePacket;
import netty.im.server.ChannelCache;
import netty.im.server.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

    /**
     * 模拟用户信息表
     */
    private Map<String, String> users = new HashMap<>();

    {
        users.put("user_1", "user_1");
        users.put("user_2", "user_2");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        Channel channel = ctx.channel();
        // 跳过length/secret/version
        in.skipBytes(4 + 4 + 1);
        byte commandId = in.readByte();
        if (commandId != Constant.LOGIN_REQUEST) {
            log.warn("{} has not login.", channel);
            channel.close();
            return;
        }
        // 跳过seqId
        in.skipBytes(4);
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        LoginRequestPacket packet = JSON.parseObject(bytes, LoginRequestPacket.class);
        String username = packet.getUsername();
        String password = packet.getPassword();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && password.equals(users.get(username))) {
            // 登陆成功后，绑定会话信息并添加至映射缓存中
            channel.attr(Constant.SESSION_ATTR_KEY).set(new Session(username));
            ChannelCache.getInstance().add(username, channel);
            channel.writeAndFlush(new LoginResponsePacket(Constant.SEQ.incrementAndGet(), true));
            // 添加空闲检测handler
            ctx.pipeline().addLast(new IdleStateHandler(Constant.ACTIVE_TEST_PERIOD * 2, 0, 0, TimeUnit.SECONDS));
            // 移除当前handler
            ctx.pipeline().remove(this);
            log.info("{} login success. [username:{} password:{}]", channel, packet.getUsername(), packet.getPassword());
        } else {
            channel.writeAndFlush(new LoginResponsePacket(Constant.SEQ.incrementAndGet(), false));
            log.info("{} login failure. [username:{} password:{}]", channel, packet.getUsername(), packet.getPassword());
        }
    }

}
