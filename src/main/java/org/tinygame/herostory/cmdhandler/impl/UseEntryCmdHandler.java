package org.tinygame.herostory.cmdhandler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户进场指令处理器
 */
public class UseEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        // 将用户id附着Channel
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        User userInfo = UserManager.getUserById(userId);
        if (userInfo == null) {
            return;
        }

        String userName = userInfo.getUserName();
        String heroAvatar = userInfo.getHeroAvatar();

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setUserName(userName);
        resultBuilder.setHeroAvatar(heroAvatar);

        // 构建结果并广播
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
