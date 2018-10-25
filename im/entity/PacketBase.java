package netty.im.entity;

import lombok.Getter;

/**
 * 数据包实体基类
 */
@Getter
public abstract class PacketBase {

    /** 密钥 */
    private transient Integer secret;

    /** 版本号 */
    private transient Byte version;

    /** 指令ID */
    private transient Byte commandId;

    /** 流水号 */
    private transient Integer seqId;

    PacketBase(Integer secret, Byte version, Byte commandId, Integer seqId) {
        this.secret = secret;
        this.version = version;
        this.commandId = commandId;
        this.seqId = seqId;
    }

}
