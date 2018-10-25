package netty.im.server;

import lombok.Getter;

/**
 * 登陆成功后绑定到当前channel的attr上，用于判断是否已登录以及从当前channel获取用户名
 */
public class Session {

    @Getter
    private String username;

    public Session(String username) {
        this.username = username;
    }

}
