package netty.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.im.handler.BaseHandler;
import netty.im.server.handler.LoginRequestHandler;
import netty.im.server.handler.ServerCodec;

public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 每当有新连接ACCEPT时都会调用initChannel(SocketChannel ch)
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 标识@ChannelHandler.Sharable的handler以单例共享于多个channel（public abstract class ChannelHandlerAdapter implements ChannelHandler@public boolean isSharable()）
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0, true));
                            pipeline.addLast(new BaseHandler());
                            pipeline.addLast(new LoginRequestHandler());
                            pipeline.addLast(new ServerCodec());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            // 阻塞等待NioServerSocketChannel被close
            channelFuture.channel().closeFuture().awaitUninterruptibly();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(8989).start();
    }

}
