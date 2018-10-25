package netty.im.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * username与channel映射关系缓存
 */
public class ChannelCache {

    private Map<String, Channel> channels = new ConcurrentHashMap<>();

    private ChannelCache() {
    }

    private static class SingletonHolder {
        private static final ChannelCache INSTANCE = new ChannelCache();
    }

    public static ChannelCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void add(String username, Channel channel) {
        channels.put(username, channel);
    }

    public void remove(String username) {
        channels.remove(username);
    }

    public Channel get(String username) {
        return channels.get(username);
    }

}
