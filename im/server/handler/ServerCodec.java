package netty.im.server.handler;

import netty.im.entity.Constant;
import netty.im.handler.CodecBase;

public class ServerCodec extends CodecBase {

    public ServerCodec() {
        // 可共享handler使用单例模式，避免每当有新连接ACCEPT时都创建新实例
        this.handlerMap.put(Constant.MESSAGE, MessageHandler.getInstance());
        this.handlerMap.put(Constant.ACTIVE_TEST_REQUEST, ActiveTestRequestHandler.getInstance());
    }

}
