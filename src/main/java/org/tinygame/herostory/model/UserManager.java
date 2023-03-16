package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserManager {

    /**
     * 用户字典
     */
    private static final Map<Integer, User> userMap = new ConcurrentHashMap<>();

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
            userMap.put(newUser.getUserId(), newUser);
        }
    }

    /**
     * 根据用户 Id移除用户
     * @param userId
     */
    public static void removeByUserId(int userId) {
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

    /**
     * 根据用户 Id获取用户
     *
     * @param userId 用户 Id
     * @return 用户对象
     */
    public static User getUserById(Integer userId) {
        return userMap.get(userId);
    }
}
