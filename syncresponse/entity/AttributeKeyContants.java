package netty.syncresponse.entity;

import io.netty.util.AttributeKey;
import netty.syncresponse.future.AckFuture;

public interface AttributeKeyContants {

    /**
     * io.netty.util.public final class AttributeKey<T> extends AbstractConstant<AttributeKey<T>>
     *      public static <T> AttributeKey<T> valueOf(String name)---name对应的AttributeKey已存在时直接返回之前的
     *      public static <T> AttributeKey<T> newInstance(String name)---name对应的AttributeKey已存在时抛异常
     */
    AttributeKey<AckFuture<Data>> ATTR = AttributeKey.valueOf("ack");

}
