package org.tinygame.herostory.cmdhandler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.model.VictorMsg;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户攻击指令处理器
 */
public class UserAttackCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {

    /**
     * 日志对象
     */
    private static Logger LOGGER = LoggerFactory.getLogger(UserAttackCmdHandler.class);

    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd msg) {
        if (msg == null) {
            return;
        }

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }

        // 获取被攻击者Id
        int targetUserId = msg.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(userId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult attResult = resultBuilder.build();
        Broadcaster.broadcast(attResult);

        // 获取被攻击用户
        User targetUser = UserManager.getUserById(targetUserId);
        if (targetUser == null) {
            return;
        }

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        int subtractHP = 10;
        int currHp = targetUser.getCurrHp() - subtractHP;
        targetUser.setCurrHp(currHp);

        // 广播减血消息
        broadcastSubtractHp(targetUserId, subtractHP);

        // 广播死亡消息
        broadcastDie(targetUser);

        // 发送 MQ 消息
        sendMqMessage(userId, targetUser);
    }

    /**
     * 广播减血消息
     *
     * @param targetUserId 被攻击用户 Id
     * @param subtractHP 减血数量
     */
    private static void broadcastSubtractHp(int targetUserId, int subtractHP) {
        if (targetUserId <= 0 ||
            subtractHP <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder hpResultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        hpResultBuilder.setTargetUserId(targetUserId);
        hpResultBuilder.setSubtractHp(subtractHP);

        GameMsgProtocol.UserSubtractHpResult hpResult = hpResultBuilder.build();
        Broadcaster.broadcast(hpResult);
    }

    /**
     * 广播死亡消息
     *
     * @param targetUser 被攻击用户对象
     */
    private static void broadcastDie(User targetUser) {
        if (targetUser.getUserId() <= 0 ||
            targetUser.getCurrHp() > 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder dieResult = GameMsgProtocol.UserDieResult.newBuilder();
        dieResult.setTargetUserId(targetUser.getUserId());
        GameMsgProtocol.UserDieResult userDieResult = dieResult.build();
        Broadcaster.broadcast(userDieResult);
    }

    /**
     * 发送 MQ 消息
     *
     * @param userId 攻击用户 Id
     * @param targetUser 被攻击用户
     */
    private static void sendMqMessage(Integer userId, User targetUser) {
        if (targetUser.getUserId() <= 0 ||
            targetUser.getCurrHp() > 0 ||
            targetUser.isDied()) {
            return;
        }

        targetUser.setDied(true);
        VictorMsg mqMsg = new VictorMsg();
        mqMsg.setWinnerId(userId);
        mqMsg.setLoserId(targetUser.getUserId());
        MQProducer.sendMsg("Victor", mqMsg);
    }
}
