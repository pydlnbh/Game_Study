package org.ormtest.step000.entity;

/**
 * 用户实体
 */
public class UserEntity {
    /**
     * 用户Id
     */
    private int _userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String _password;

    public UserEntity() {
    }

    public UserEntity(int _userId, String userName, String _password) {
        this._userId = _userId;
        this.userName = userName;
        this._password = _password;
    }

    public int get_userId() {
        return _userId;
    }

    public void set_userId(int _userId) {
        this._userId = _userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }
}
