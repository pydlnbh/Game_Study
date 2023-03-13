package org.ormtest.step050.entity;

import javassist.*;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体助手工厂
 */
public class EntityHelperFactory {

    /**
     * 助手字典
     */
    private static final Map<Class<?>, AbstractEntityHelper> _entityHelperMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private EntityHelperFactory() {
    }

    /**
     * 获取帮助
     *
     * @param entityClazz 实体类
     * @return AbstractEntityHelper 实体助手
     * @throws Exception
     */
    public static AbstractEntityHelper getEntityHelper(Class<?> entityClazz) throws Exception {
        if (entityClazz == null) {
            return null;
        }

        AbstractEntityHelper entityHelper = _entityHelperMap.get(entityClazz);

        if (entityHelper != null) {
            return entityHelper;
        }

        // 使用 Javassist 动态生成 Java 字节码
        // 获取类池
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();

        // 导入相关的类
        // import java.sql.ResultSet
        // import org.ormtest.entity.UserEntity
        // import ...
        pool.importPackage(ResultSet.class.getName());
        pool.importPackage(entityClazz.getName());

        // 抽象的助手类
        CtClass ctClass = pool.getCtClass(AbstractEntityHelper.class.getName());
        // 助手的实现类
        final String helperImplClazzName = entityClazz.getName() + "_Helper";

        // 创建助攻工具类
        // public class UserEntity_Helper extends AbstractEntityHelper { ...
        CtClass helpClazz = pool.makeClass(helperImplClazzName, ctClass);

        // 创建默认构造器, 会生成如下代码: public UserEntity_Helper()
        CtConstructor ctConstructor = new CtConstructor(new CtClass[0], helpClazz);
        // 空函数体
        ctConstructor.setBody("{}");
        // 添加默认构造器
        helpClazz.addConstructor(ctConstructor);

        // 用于创建函数代码字符串
        final StringBuffer sb = new StringBuffer();
        // 添加一个函数, 也就是实现抽象类的 create 函数
        sb.append("public Object create(java.sql.ResultSet rs) throws Exception {\n");
        // 生成如下代码: UserEntity ue = new UserEntity();
        sb.append(entityClazz.getName())
                .append(" ue = new ")
                .append(entityClazz.getName())
                .append("();\n");


        // 通过反射方式获取类的字段数组并生成代码
        // 获取类的字段数组并生成代码
        Field[] fields = entityClazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取字段上的数组
            Column annotation = field.getAnnotation(Column.class);
            if (annotation == null) {
                continue;
            }

            // 获取列名称
            String colName = annotation.name();

            if (field.getType() == Integer.TYPE) {
                // 生成如下代码 ue._userId = rs.getInt("user_id");
                sb.append("ue.")
                        .append(field.getName())
                        .append(" = ")
                        .append("rs.getInt(\"")
                        .append(colName)
                        .append("\");\n");
            } else if (field.getType().equals(String.class)) {
                sb.append("ue.")
                        .append(field.getName())
                        .append(" = ")
                        .append("rs.getString(\"")
                        .append(colName)
                        .append("\");\n");
            } else {
                // 不支持的类型, 如果需要支持 float、long、boolean等类型接着往下写
            }
        }
        sb.append("return ue;\n");
        sb.append("}");

        // 创建解析方法
        CtMethod ctMethod = CtNewMethod.make(sb.toString(), helpClazz);
        // 添加方法
        helpClazz.addMethod(ctMethod);
        // 获取类
        Class<?> javaClazz = helpClazz.toClass();

        // 调试文件
        helpClazz.writeFile("D:\\Program Files\\Code\\Idea-workspace\\demo\\Game_Study");

        // 创建帮助对象实例
        entityHelper = (AbstractEntityHelper) javaClazz.newInstance();
        // 添加到字典
        _entityHelperMap.put(entityClazz, entityHelper);
        return entityHelper;
    }
}
