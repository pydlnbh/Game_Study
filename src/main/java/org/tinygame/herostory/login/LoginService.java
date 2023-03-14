package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDAO;
import org.tinygame.herostory.login.db.UserEntity;

/**
 * 登录服务
 */
public final class LoginService {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单例对象
     */
    private static final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return org.tinygame.herostory.login.LoginService 登录服务对象
     */
    public static LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登录服务
     *
     * @param userName 用户名
     * @param password 密码
     * @return org.tinygame.herostory.login.db.UserEntity 用户对象
     */
    public UserEntity userLogin(String userName, String password) {
        if (userName == null ||
            password == null) {
            return null;
        }

        // 使用 try() 语法, 代码块执行完后自动调用 sqlSession 的 close 方法
        try (SqlSession sqlSession = MySqlSessionFactory.openSession()) {
            // 获取 DAO
            IUserDAO mapper = sqlSession.getMapper(IUserDAO.class);
            // 获取用户实体
            UserEntity userEntity = mapper.getUserByName(userName);

            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            if (userEntity != null) {
                if (!userEntity.getPassword().equals(password)) {
                    LOGGER.error("用户密码错误, userName = {}", userName);
                    throw new RuntimeException("用户密码错误");
                }
            } else {
                // 新建用户实体
                userEntity = new UserEntity();
                userEntity.setUserName(userName);
                userEntity.setPassword(password);

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

            return userEntity;
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return null;
        }
    }
}
