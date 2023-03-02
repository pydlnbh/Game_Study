package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class WhoElseIsHereCmdHandler {
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd msg) {
        // 还有谁在场
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuider = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User curUser : UserManager.listUser()) {
            if (null == curUser) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(curUser.userId);
            userInfoBuilder.setHeroAvatar(curUser.heroAvatar);
            resultBuider.addUserInfo(userInfoBuilder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuider.build();
        ctx.writeAndFlush(newResult);
    }
}
