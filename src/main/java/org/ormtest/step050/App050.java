package org.ormtest.step050;

import org.ormtest.step050.entity.AbstractEntityHelper;
import org.ormtest.step050.entity.EntityHelperFactory;
import org.ormtest.step050.entity.UserEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 主应用程序
 */
public class App050 {
    /**
     * 应用程序主函数
     *
     * @param args 参数数组
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new App050().start();
    }

    /**
     * 测试开始
     *
     * @throws Exception
     */
    private void start() throws Exception {
        // 加载Mysql驱动
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        // 数据库连接地址
        String dbConnStr = "jdbc:mysql://127.0.0.1:3306/hero_story?user=root&password=root";
        // 创建数据库连接
        Connection conn = DriverManager.getConnection(dbConnStr);
        // 建立对象
        Statement stmt = conn.createStatement();

        String sql = "select * from t_user limit 20000";

        // 执行查询
        ResultSet rs = stmt.executeQuery(sql);

        // 创建助手工具类, 这里设计全新设计的工程类, 参考阅读: https://github.com/jhunters/jprotobuf
        AbstractEntityHelper helper = EntityHelperFactory.getEntityHelper(UserEntity.class);

        // 获取开始时间
        long t0 = System.currentTimeMillis();

        while (rs.next()) {
            UserEntity ue = (UserEntity) helper.create(rs);
        }

        // 获取结束时间
        long t1 = System.currentTimeMillis();

        // 关闭数据库链接
        stmt.close();
        conn.close();

        // print
        System.out.println("It cost " + (t1 - t0) + "ms");
    }

    /**
     * 生成20w条数据的sql
     *
     * 这个语句插入mysql会失败, 执行下面语句
     * select @@global.max_allowed_packet;
     * set global max_allowed_packet = 2*1024*1024*100;
     * 设置完需要重启
     *
     * @throws IOException
     */
    private static void generateInsertSql() throws IOException {
        File file = new File(System.getProperty("user.dir") + "\\user.sql");
        FileWriter fileWriter = new FileWriter(file);
        int size = 200001;
        StringBuilder string = new StringBuilder();
        string.append("insert into t_user(user_name, password) values");
        for (int i = 1; i < size; i++) {
            String comma = i == (size - 1) ? ";" : ", ";
            string.append("(\"user" + i + "\", \"password" + i + "\")" + comma);
        }
        fileWriter.write(string.toString());
        fileWriter.close();
    }
}
