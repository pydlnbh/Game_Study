package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class UserManager {

    /**
     * 用户字典
     */
    private static final Map<Integer, User> userMap = new HashMap<>();

    /**
     * 私有化默认构造方法
     */
    private UserManager() {
    }

    /**
     * 添加用户
     *
     * @param newUser
     */
    public static void addUser(User newUser) {
        if (newUser != null) {
            userMap.put(newUser.userId, newUser);
        }
    }

    /**
     * 根据用户Id移除用户
     * @param userId
     */
    public static void removeUserById(int userId) {
        userMap.remove(userId);
    }

    /**
     * 用户列表
     *
     * @return
     */
    public static Collection<User> listUser() {
        return userMap.values();
    }
}