package netty.im.entity;

/**
 * 心跳包测试请求实体类
 */
public class ActiveTestRequestPacket extends PacketBase {

    public ActiveTestRequestPacket(Integer seqId) {
        this(Constant.SECRET_DEFAULT, Constant.VERSION_DEFAULT, seqId);
    }

    public ActiveTestRequestPacket(Integer secret, Byte version, Integer seqId) {
        super(secret, version, Constant.ACTIVE_TEST_REQUEST, seqId);
    }

}
