package org.tinygame.herostory.model;

/**
 * 战斗结果消息
 */
public class VictorMsg {
    /**
     * 赢家 Id
     */
    private int winnerId;

    /**
     * 输家 Id
     */
    private int loserId;

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getLoserId() {
        return loserId;
    }

    public void setLoserId(int loserId) {
        this.loserId = loserId;
    }
}
