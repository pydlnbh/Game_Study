package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户移动指令处理器
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd msg) {
        if (ctx == null || msg == null) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }

        /**
         * 获取移动的用户
         */
        User moveUser = UserManager.getUserById(userId);
        if (moveUser == null) {
            return;
        }

        // 获取自动状态
        MoveState moveState = moveUser.getMoveState();
        // 设置位置和时间
        moveState.setFromPosX(msg.getMoveFromPosX());
        moveState.setFromPosY(msg.getMoveFromPosY());
        moveState.setToPosX(msg.getMoveToPosX());
        moveState.setToPosY(msg.getMoveToPosY());
        moveState.setStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(moveState.getFromPosX());
        resultBuilder.setMoveToPosY(moveState.getFromPosY());
        resultBuilder.setMoveToPosX(moveState.getToPosX());
        resultBuilder.setMoveToPosY(moveState.getToPosY());
        resultBuilder.setMoveStartTime(moveState.getStartTime());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
