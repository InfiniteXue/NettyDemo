package netty.im.entity;

import lombok.Getter;

/**
 * 登录请求实体类
 */
@Getter
public class LoginRequestPacket extends PacketBase {

    private String username;

    private String password;

    public LoginRequestPacket(Integer seqId, String username, String password) {
        this(Constant.SECRET_DEFAULT, Constant.VERSION_DEFAULT, seqId, username, password);
    }

    public LoginRequestPacket(Integer secret, Byte version, Integer seqId, String username, String password) {
        super(secret, version, Constant.LOGIN_REQUEST, seqId);
        this.username = username;
        this.password = password;
    }

}
