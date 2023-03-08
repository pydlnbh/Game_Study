package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

public interface ICmdHandle<T extends GeneratedMessageV3> {
    void handle(ChannelHandlerContext ctx, T msg);
}
