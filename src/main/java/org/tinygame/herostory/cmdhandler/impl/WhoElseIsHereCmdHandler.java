package org.tinygame.herostory.cmdhandler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户位置指令处理器
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd msg) {
        // 还有谁在场
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User curUser : UserManager.listUser()) {
            if (null == curUser) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(curUser.getUserId());
            userInfoBuilder.setHeroAvatar(curUser.getHeroAvatar());

            // 获取移动状态
            MoveState moveState = curUser.getMoveState();

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            moveStateBuilder.setFromPosX(moveState.getFromPosX());
            moveStateBuilder.setFromPosY(moveState.getFromPosY());
            moveStateBuilder.setToPosX(moveState.getToPosX());
            moveStateBuilder.setToPosY(moveState.getToPosY());
            moveStateBuilder.setStartTime(moveState.getStartTime());
            // 将移动状态设置到用户信息
            userInfoBuilder.setMoveState(moveStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
