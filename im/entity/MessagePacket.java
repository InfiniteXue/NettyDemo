package netty.im.entity;

import lombok.Getter;

/**
 * 消息请求实体类
 */
@Getter
public class MessagePacket extends PacketBase {

    private String connectionUsername;

    private String message;

    public MessagePacket(Integer seqId, String connectionUsername, String message) {
        this(Constant.SECRET_DEFAULT, Constant.VERSION_DEFAULT, seqId, connectionUsername, message);
    }

    public MessagePacket(Integer secret, Byte version, Integer seqId, String connectionUsername, String message) {
        super(secret, version, Constant.MESSAGE, seqId);
        this.connectionUsername = connectionUsername;
        this.message = message;
    }

}
