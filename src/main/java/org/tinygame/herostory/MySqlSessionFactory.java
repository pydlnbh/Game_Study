package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Mysql 会话工厂
 */
public final class MySqlSessionFactory {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    /**
     * MyBatis Sql 会话工厂
     */
    private static SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化默认构造器
     */
    private MySqlSessionFactory() {
    }

    /**
     * 初始化
     */
    public static void init() {
        try {
            _sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("MyBatisConfig.xml"));

            // 测试数据库连接
            SqlSession tempSession = openSession();

            tempSession.getConnection().createStatement().execute("select  -1");

            tempSession.close();

            LOGGER.info("Mysql 数据库连接成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启 Mysql 会话
     *
     * @return org.apache.ibatis.session.SqlSession 会话对象
     */
    public static SqlSession openSession() {
        if (_sqlSessionFactory == null) {
            throw new RuntimeException("会话工厂尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
