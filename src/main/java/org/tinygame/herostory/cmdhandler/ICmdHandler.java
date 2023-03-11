package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 用户指令接口
 */
public interface ICmdHandler<T extends GeneratedMessageV3> {
    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    void handle(ChannelHandlerContext ctx, T msg);
}
