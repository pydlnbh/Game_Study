package org.ormtest.step010.entity;

import java.sql.ResultSet;

/**
 * 用户实体助手类
 */
public class UserEntity_Helper {
    /**
     * 将数据集封装为实体对象
     *
     * @param rs 数据集
     * @return
     * @throws Exception
     */
    public UserEntity create(ResultSet rs) throws Exception {
        if (rs == null) {
            return null;
        }

        int userId = rs.getInt("user_id");
        String userName = rs.getString("user_name");
        String password = rs.getString("password");

        // 创建新的实体对象并返回
        return new UserEntity(userId, userName, password);
    }
}
