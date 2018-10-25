package netty.im.entity;

import io.netty.util.AttributeKey;
import netty.im.server.Session;

import java.util.concurrent.atomic.AtomicInteger;

public interface Constant {

    /** 默认密钥 */
    Integer SECRET_DEFAULT = 123456;

    /** 默认版本号 */
    Byte VERSION_DEFAULT = 1;

    /** seqId生成器 */
    AtomicInteger SEQ = new AtomicInteger();

    /** 登录请求 */
    Byte LOGIN_REQUEST = (byte) 0x01;

    /** 登录响应 */
    Byte LOGIN_RESPONSE = (byte) 0x81;

    /** 消息 */
    Byte MESSAGE = (byte) 0x02;

    /** 心跳包测试请求 */
    Byte ACTIVE_TEST_REQUEST = (byte) 0x03;

    /** 心跳包测试响应 */
    Byte ACTIVE_TEST_RESPONSE = (byte) 0x83;

    int ACTIVE_TEST_PERIOD = 5;

    /** 登陆成功会话信息(服务端) */
    AttributeKey<Session> SESSION_ATTR_KEY = AttributeKey.valueOf("session");

    /** 登陆成功标识(客户端) */
    AttributeKey<Boolean> LOGIN_ATTR_KEY = AttributeKey.valueOf("login");

    /** 数据包格式固定字节数 */
    int PACKET_FIXED_LENGTH = 4 + 4 + 1 + 1 + 4;

}
