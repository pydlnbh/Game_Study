package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 这个类当做工具类不需要继承, 使用final修饰
 */
public final class Broadcaster {

    /**
     * 信道组, 注意这里一定要用static, 否则无法实现群发
     */
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化默认构造器
     */
    private Broadcaster() {

    }

    /**
     * 添加信道
     *
     * @param channel
     */
    public static void addChannel(Channel channel) {
        channelGroup.add(channel);
    }

    /**
     * 删除信道
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    /**
     * 广播消息
     *
     * @param msg
     */
    public static void broadcast(Object msg) {
        if (msg == null) {
            return;
        }

        channelGroup.writeAndFlush(msg);
    }
}
