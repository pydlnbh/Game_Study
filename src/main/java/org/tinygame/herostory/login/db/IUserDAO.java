package org.tinygame.herostory.login.db;

import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper
 */
public interface IUserDAO {
    /**
     * 根据用户名获取用户
     *
     * @param userName 用户名
     * @return 用户信息
     */
    UserEntity getUserByName(@Param("userName") String userName);

    /**
     * 添加用户实体
     *
     * @param record 请求参数
     */
    void insertInfo(UserEntity record);
}
