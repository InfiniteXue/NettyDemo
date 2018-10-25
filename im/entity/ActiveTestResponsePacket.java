package netty.im.entity;

/**
 * 心跳包测试响应实体类
 */
public class ActiveTestResponsePacket extends PacketBase {

    public ActiveTestResponsePacket(Integer seqId) {
        this(Constant.SECRET_DEFAULT, Constant.VERSION_DEFAULT, seqId);
    }

    public ActiveTestResponsePacket(Integer secret, Byte version, Integer seqId) {
        super(secret, version, Constant.ACTIVE_TEST_RESPONSE, seqId);
    }

}
