package netty.im.client;

import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import netty.im.entity.Constant;
import netty.im.entity.LoginRequestPacket;
import netty.im.entity.MessagePacket;

import java.util.Scanner;

/**
 * 客户端控制台管理线程
 */
@Slf4j
public class ClientConsoleThread extends Thread {

    @Setter
    private volatile boolean flag;

    private Object obj;
    private Channel channel;

    public ClientConsoleThread(Object obj, Channel channel) {
        this.obj = obj;
        this.channel = channel;
    }

    @Override
    public void run() {
        log.info("Client console start.");
        flag = true;
        Scanner sc = new Scanner(System.in);
        while (flag) {
            try {
                // public interface AttributeMap@<T> boolean hasAttr(AttributeKey<T> key)---指定key不存在或值为null时返回false
                if (channel.hasAttr(Constant.LOGIN_ATTR_KEY)) {
                    String connUsername = sc.next();
                    String message = sc.next();
                    channel.writeAndFlush(new MessagePacket(Constant.SEQ.incrementAndGet(), connUsername, message));
                } else {
                    // 登录请求
                    System.out.println("======登录======");
                    System.out.println("用户名");
                    String username = sc.nextLine();
                    System.out.println("密码");
                    String password = sc.nextLine();
                    channel.writeAndFlush(new LoginRequestPacket(Constant.SEQ.incrementAndGet(), username, password));
                    synchronized (obj) {
                        // 阻塞等待服务器登录响应
                        obj.wait(5000);
                    }
                }
            } catch (Exception e) {
                log.error("An exception occurred.", e);
            }
        }
        log.info("Client console end.");
    }

}
