package netty.im.client.handler;

import netty.im.entity.Constant;
import netty.im.handler.CodecBase;

public class ClientCodec extends CodecBase {

    public ClientCodec(Object obj) {
        this.handlerMap.put(Constant.LOGIN_RESPONSE, new LoginResponseHandler(obj));
        this.handlerMap.put(Constant.MESSAGE, new MessageHandler());
        this.handlerMap.put(Constant.ACTIVE_TEST_RESPONSE, new ActiveTestResponseHandler());
    }

}
