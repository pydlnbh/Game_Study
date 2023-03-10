package org.ormtest.step020.entity;

import java.lang.reflect.Field;
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

        // 创建新的实体对象
        UserEntity ue = new UserEntity();

        // 获取类的字段数组
        Field[] fields = ue.getClass().getDeclaredFields();

        for (Field field : fields) {
            // 获取字段上的注解
            Column annoColum = field.getAnnotation(Column.class);

            if (annoColum == null) {
                continue;
            }

            // 获取列名称
            String colName = annoColum.name();
            // 从数据库中获取列值
            Object colVal = rs.getObject(colName);

            if (colVal == null) {
                continue;
            }

            field.set(ue, colVal);
        }

        // 返回
        return ue;
    }
}
