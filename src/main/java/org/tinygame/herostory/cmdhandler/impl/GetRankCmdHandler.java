package org.tinygame.herostory.cmdhandler.impl;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.rank.RankItem;
import org.tinygame.herostory.rank.RankService;

import java.util.Collections;

/**
 * 获取排行榜指令处理器
 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    /**
     * 指令处理方法
     *
     * @param ctx 通道处理程序上下文
     * @param msg 消息
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd msg) {
        if (ctx == null ||
                msg == null) {
            return;
        }

        RankService.getInstance().getRand((rankItemList) -> {
            if (rankItemList == null) {
                rankItemList = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem : rankItemList) {
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();

                rankItemBuilder.setWin(rankItem.getWin());
                rankItemBuilder.setRankId(rankItem.getRankId());
                rankItemBuilder.setUserId(rankItem.getUserId());
                rankItemBuilder.setUserName(rankItem.getUserName());
                rankItemBuilder.setHeroAvatar(rankItem.getHeroAvatar());

                resultBuilder.addRankItem(rankItemBuilder);
            }

            GameMsgProtocol.GetRankResult rankResult = resultBuilder.build();
            ctx.writeAndFlush(rankResult);

            return null;
        });
    }
}
