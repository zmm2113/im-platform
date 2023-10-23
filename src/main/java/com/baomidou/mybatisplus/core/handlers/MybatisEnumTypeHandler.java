//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baomidou.mybatisplus.core.handlers;

import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.platform.common.core.EnumUtils;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解决mybatis-plus枚举转换问题
 */
public class MybatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private final Class<E> enumClassType;
    private final Class<?> propertyType;
    private final Invoker getInvoker;

    public MybatisEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        } else {
            this.enumClassType = enumClassType;
            MetaClass metaClass = MetaClass.forClass(enumClassType, REFLECTOR_FACTORY);
            String name = "value";
            if (!IEnum.class.isAssignableFrom(enumClassType)) {
                name = (String) findEnumValueFieldName(this.enumClassType).orElseThrow(() -> {
                    return new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", this.enumClassType.getName()));
                });
            }

            this.propertyType = ReflectionKit.resolvePrimitiveIfNecessary(metaClass.getGetterType(name));
            this.getInvoker = metaClass.getGetInvoker(name);
        }
    }

    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(CollectionUtils.computeIfAbsent(TABLE_METHOD_OF_ENUM_TYPES, className, (key) -> {
                Optional<Field> fieldOptional = findEnumValueAnnotationField(clazz);
                return fieldOptional.map(Field::getName).orElse(null);
            }));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Field> findEnumValueAnnotationField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter((field) -> {
            return field.isAnnotationPresent(EnumValue.class);
        }).findFirst();
    }

    public static boolean isMpEnums(Class<?> clazz) {
        return clazz != null && clazz.isEnum() && (IEnum.class.isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }

    }

    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        if (EnumUtil.isEnum(propertyType)) {
            value = EnumUtils.toEnum(this.enumClassType, rs.getString(columnName));
        }
        return null == value && rs.wasNull() ? null : this.valueOf(value);
    }

    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        if (EnumUtil.isEnum(propertyType)) {
            value = EnumUtils.toEnum(this.enumClassType, rs.getString(columnIndex));
        }
        return null == value && rs.wasNull() ? null : this.valueOf(value);
    }

    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        if (EnumUtil.isEnum(propertyType)) {
            value = EnumUtils.toEnum(this.enumClassType, cs.getString(columnIndex));
        }
        return null == value && cs.wasNull() ? null : this.valueOf(value);
    }

    private E valueOf(Object value) {
        E[] es = (E[]) this.enumClassType.getEnumConstants();
        return Arrays.stream(es).filter((e) -> {
            return this.equalsValue(value, this.getValue(e));
        }).findAny().orElse(null);
    }

    protected boolean equalsValue(Object sourceValue, Object targetValue) {
        String sValue = StringUtils.toStringTrim(sourceValue);
        String tValue = StringUtils.toStringTrim(targetValue);
        return sourceValue instanceof Number && targetValue instanceof Number && (new BigDecimal(sValue)).compareTo(new BigDecimal(tValue)) == 0 ? true : Objects.equals(sValue, tValue);
    }

    private Object getValue(Object object) {
        try {
            return this.getInvoker.invoke(object, new Object[0]);
        } catch (ReflectiveOperationException var3) {
            throw ExceptionUtils.mpe(var3);
        }
    }
}
