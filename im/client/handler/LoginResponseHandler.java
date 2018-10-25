package netty.im.client.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.ActiveTestRequestPacket;
import netty.im.entity.Constant;
import netty.im.entity.LoginResponsePacket;
import netty.im.handler.MessageBusinessHandler;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LoginResponseHandler extends MessageBusinessHandler {

    private Object obj;

    public LoginResponseHandler(Object obj) {
        this.obj = obj;
    }

    @Override
    public void messageHandle(ChannelHandlerContext ctx, ByteBuf in) {
        byte[] msg = msgAnalysis(ctx.channel(), in);
        LoginResponsePacket packet = JSON.parseObject(msg, LoginResponsePacket.class);
        if (packet.getSuccess()) {
            ctx.channel().attr(Constant.LOGIN_ATTR_KEY).set(true);
            System.out.println("登陆成功");
            // 添加空闲检测handler
            ctx.pipeline().addLast(new IdleStateHandler(Constant.ACTIVE_TEST_PERIOD * 2, 0, 0, TimeUnit.SECONDS));
            // 定时心跳包测试请求
            ctx.executor().scheduleAtFixedRate(() -> {
                // 可参考syncresponse改为阻塞式
                ctx.channel().writeAndFlush(new ActiveTestRequestPacket(Constant.SEQ.incrementAndGet()));
            }, Constant.ACTIVE_TEST_PERIOD, Constant.ACTIVE_TEST_PERIOD, TimeUnit.SECONDS);
        } else {
            System.out.println("登录失败");
        }
        synchronized (obj) {
            obj.notifyAll();
        }
    }

}
