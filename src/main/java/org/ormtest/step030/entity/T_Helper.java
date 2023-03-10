package org.ormtest.step030.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * 用户实体助手类, 这个更通用但是性能太差了
 */
public class T_Helper {
    /**
     * 将数据集封装为实体对象
     *
     * @param entityClazz 实体类
     * @param rs 数据集
     * @return
     * @throws Exception
     */
    public <T> T create(Class<T> entityClazz, ResultSet rs) throws Exception {
        if (rs == null) {
            return null;
        }

        // 创建新的实体对象
        T newInstance = entityClazz.newInstance();

        // 获取类的字段数组
        Field[] fields = entityClazz.getDeclaredFields();

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

            field.set(newInstance, colVal);
        }

        // 返回
        return (T) newInstance;
    }
}
