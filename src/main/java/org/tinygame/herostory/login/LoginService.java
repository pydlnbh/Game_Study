package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.async.impl.AsyncGetUserByName;
import org.tinygame.herostory.login.db.IUserDAO;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

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
     * @param callback 回调方法
     * @return 用户实体
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null ||
            password == null) {
            return;
        }

        IAsyncOperation asyncOp = new AsyncGetUserByName(userName, password) {

            /**
             * 执行完成逻辑
             */
            @Override
            public void doFinish() {
                if (callback != null) {
                    callback.apply(getUserEntity());
                }
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);
    }
}
