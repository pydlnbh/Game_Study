package org.tinygame.herostory.rank;

/**
 * 排名项
 */
public class RankItem {
    /**
     * 用户 Id
     */
    private int userId;

    /**
     * 排名 Id
     */
    private int rankId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 英雄人物
     */
    private String heroAvatar;

    /**
     * 获胜次数
     */
    private int win;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }
}
