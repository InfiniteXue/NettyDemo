package netty.syncresponse.future;

import io.netty.channel.Channel;
import netty.syncresponse.entity.AttributeKeyContants;
import netty.syncresponse.entity.Data;

public class FutureWriter {

    private final Channel channel;

    public FutureWriter(Channel channel) {
        this.channel = channel;
    }

    public AckFuture writeAndFlush(Data data) {
        AckFuture<Data> ackFuture = new AckFuture<>();
        ackFuture.setSeqId(data.getSeqId());
        // channel的Attribute只能添加不能移除
        channel.attr(AttributeKeyContants.ATTR).set(ackFuture);
        channel.writeAndFlush(data).syncUninterruptibly();
        return ackFuture;
    }

}
