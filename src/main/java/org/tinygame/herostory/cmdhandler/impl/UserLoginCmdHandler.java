package org.tinygame.herostory.cmdhandler.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登录指令处理器
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd msg) {
        if (ctx == null ||
            msg == null) {
            return;
        }

        LOGGER.info(
                "userName = {}, password = {}",
                msg.getUserName(),
                msg.getPassword()
        );


        try {
            LoginService.getInstance().userLogin(msg.getUserName(), msg.getPassword(), (userEntity) -> {
                if (userEntity == null) {
                    LOGGER.error("用户登录失败, userName = {}", msg.getUserName());
                    return null;
                }

                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                // 从指令对象中获取用户 Id 和英雄形象
                int userId = userEntity.getUserId();
                String heroAvatar = userEntity.getHeroAvatar();

                // 新建用户
                User newUser = new User();
                newUser.setUserId(userId);
                newUser.setUserName(userEntity.getUserName());
                newUser.setHeroAvatar(heroAvatar);
                newUser.setCurrHp(100);
                // 将用户加入管理器
                UserManager.addUser(newUser);

                // 将用户 Id 附着Channel
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

                GameMsgProtocol.UserLoginResult.Builder newBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
                newBuilder.setUserId(userId);
                newBuilder.setUserName(userEntity.getUserName());
                newBuilder.setHeroAvatar(userEntity.getHeroAvatar());

                // 构建结果并发送
                GameMsgProtocol.UserLoginResult result = newBuilder.build();
                ctx.writeAndFlush(result);

                return null;
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
