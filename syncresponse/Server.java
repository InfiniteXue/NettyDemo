package netty.syncresponse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.syncresponse.entity.Data;

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
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new Codec());
                            pipeline.addLast(new ServerHandler());
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

@ChannelHandler.Sharable
class ServerHandler extends SimpleChannelInboundHandler<Data> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data data) throws Exception {
        // 重写SimpleChannelInboundHandler的channelRead0()时可不关注msg的release（channelRead()中默认会release）
        System.out.println("Server:" + data.getSeqId() + "-" + data.getMsg() + " [" + System.currentTimeMillis() + "]");
        // 模拟耗时操作
        Thread.sleep(2000);
        ctx.writeAndFlush(new Data(data.getSeqId(), "ack"));
    }

}
