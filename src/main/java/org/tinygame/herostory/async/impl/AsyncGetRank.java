package org.tinygame.herostory.async.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.rank.RankItem;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AsyncGetRank implements IAsyncOperation {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGetRank.class);

    /**
     * 排名列表
     */
    private List<RankItem> rankItemList = new ArrayList<>();

    /**
     * 获取排名列表
     *
     * @return 排名列表
     */
    public List<RankItem> getRankItemList() {
        return rankItemList;
    }

    /**
     * 异步方式获取排名
     */
    @Override
    public void doSync() {
        try (Jedis redis = RedisUtil.getRedis()) {

            Set<String> rank = redis.zrevrange("Rank", 0, 9);
            LOGGER.info("redis test: {}", rank);

            // 获取字符串集合
            Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

            List<RankItem> rankItemList = new ArrayList<>();
            int rankId = 0;

            for (Tuple t : valSet) {
                // 获取用户 Id
                int userId = Integer.parseInt(t.getElement());

                // 获取用户的基本信息
                String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                if (jsonStr == null ||
                    jsonStr.isEmpty()) {
                    continue;
                }

                JSONObject jsonObj = JSONObject.parseObject(jsonStr);

                RankItem rankItem = new RankItem();
                rankItem.setUserId(userId);
                rankItem.setRankId(++rankId);
                rankItem.setUserName(jsonObj.getString("userName"));
                rankItem.setHeroAvatar(jsonObj.getString("heroAvatar"));
                rankItem.setWin((int) t.getScore());

                rankItemList.add(rankItem);
            }

            this.rankItemList = rankItemList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
