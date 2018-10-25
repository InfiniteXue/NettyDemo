package netty.im.entity;

import lombok.Getter;

/**
 * 登录响应实体类
 */
@Getter
public class LoginResponsePacket extends PacketBase {

    private Boolean success;

    public LoginResponsePacket(Integer seqId, Boolean isSuccess) {
        this(Constant.SECRET_DEFAULT, Constant.VERSION_DEFAULT, seqId, isSuccess);
    }

    public LoginResponsePacket(Integer secret, Byte version, Integer seqId, Boolean success) {
        super(secret, version, Constant.LOGIN_RESPONSE, seqId);
        this.success = success;
    }

}
