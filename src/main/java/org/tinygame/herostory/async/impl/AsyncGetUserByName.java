package org.tinygame.herostory.async.impl;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDAO;
import org.tinygame.herostory.login.db.UserEntity;

public class AsyncGetUserByName implements IAsyncOperation {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGetUserByName.class);

    /**
     * 用户名
     */
    private final String _userName;

    /**
     * 密码
     */
    private final String _password;

    /**
     * 用户实体
     */
    private UserEntity _userEntity = null;

    /**
     * 类参数构造器
     *
     * @param userName 用户名
     * @param password 密码
     */
    public AsyncGetUserByName(String userName, String password) {
        _userName = userName;
        _password = password;
    }

    /**
     * 获取用户实体
     *
     * @return 用户实体
     */
    public UserEntity getUserEntity() {
        return _userEntity;
    }

    @Override
    public int getBindId() {
        return _userName.charAt(_userName.length() - 1);
    }

    /**
     * 执行异步操作
     */
    @Override
    public void doSync() {
        // 使用 try() 语法, 代码块执行完后自动调用 sqlSession 的 close 方法
        try (SqlSession sqlSession = MySqlSessionFactory.openSession()) {
            // 获取 DAO
            IUserDAO mapper = sqlSession.getMapper(IUserDAO.class);
            // 获取用户实体
            UserEntity userEntity = mapper.getUserByName(_userName);

            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            if (userEntity != null) {
                if (!userEntity.getPassword().equals(_password)) {
                    LOGGER.error("用户密码错误, userName = {}", _userName);
                    throw new RuntimeException("用户密码错误");
                }
            } else {
                // 新建用户实体
                userEntity = new UserEntity();
                userEntity.setUserName(_userName);
                userEntity.setPassword(_password);

                // 随机赋值角色
                double random = Math.random();
                String heroAvatar = "";
                if (random < 0.3) {
                    heroAvatar = "Hero_Hammer";
                } else if (random < 0.6) {
                    heroAvatar = "Hero_Shaman";
                } else {
                    heroAvatar = "Hero_Skeleton";
                }
                userEntity.setHeroAvatar(heroAvatar);

                // 将用户实体插入数据库
                mapper.insertInfo(userEntity);
            }

            _userEntity = userEntity;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
    }
}
