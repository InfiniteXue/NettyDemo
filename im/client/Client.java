package netty.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import netty.im.client.handler.ClientCodec;
import netty.im.handler.BaseHandler;

@Slf4j
public class Client {

    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 登录请求/响应线程通信对象
        Object obj = new Object();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0, true));
                            pipeline.addLast(new BaseHandler());
                            pipeline.addLast(new ClientCodec(obj));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            ClientConsoleThread clientConsole = new ClientConsoleThread(obj, channelFuture.channel());
            // 启动客户端控制台管理线程
            clientConsole.start();
            // 阻塞等待NioSocketChannel被close
            channelFuture.channel().closeFuture().awaitUninterruptibly();
            // 关闭客户端控制台管理线程
            clientConsole.setFlag(false);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Client("127.0.0.1", 8989).start();
    }

}
