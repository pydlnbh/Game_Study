package org.tinygame.herostory.model;

/**
 * 用户
 */
public class User {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户形象
     */
    private String heroAvatar;

    /**
     * 当前血量
     */
    private int currHp = 100;

    /**
     * 移动状态
     */
    private final MoveState moveState = new MoveState();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getCurrHp() {
        return currHp;
    }

    public void setCurrHp(int currHp) {
        this.currHp = currHp;
    }

    public MoveState getMoveState() {
        return moveState;
    }
}
