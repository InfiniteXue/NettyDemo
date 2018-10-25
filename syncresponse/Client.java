package netty.syncresponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.syncresponse.entity.AttributeKeyContants;
import netty.syncresponse.entity.Data;
import netty.syncresponse.future.AckFuture;
import netty.syncresponse.future.FutureWriter;

public class Client {

    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
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
                            pipeline.addLast(new Codec());
                            pipeline.addLast(new ClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            FutureWriter futureWriter = new FutureWriter(channelFuture.channel());
            // 每个请求会阻塞等待对应response指定时间
            System.out.println(futureWriter.writeAndFlush(new Data(1, "syn")).getResult(1000) + " [" + System.currentTimeMillis() + "]");
            System.out.println(futureWriter.writeAndFlush(new Data(2, "established")).getResult(5000) + " [" + System.currentTimeMillis() + "]");
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Client("127.0.0.1", 8989).start();
    }

}

@ChannelHandler.Sharable
class ClientHandler extends SimpleChannelInboundHandler<Data> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data data) throws Exception {
        // 通过数据包seqId判断是否为等待的response
        AckFuture<Data> ackFuture = ctx.channel().attr(AttributeKeyContants.ATTR).get();
        ackFuture.setCompleted(data);
    }

}
