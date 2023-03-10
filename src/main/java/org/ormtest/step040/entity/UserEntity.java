package org.ormtest.step040.entity;

/**
 * 用户实体
 */
public class UserEntity {
    /**
     * 用户Id
     */
    @Column(name = "user_id")
    public int _userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    public String userName;

    /**
     * 密码
     */
    @Column(name = "password")
    public String _password;
}
