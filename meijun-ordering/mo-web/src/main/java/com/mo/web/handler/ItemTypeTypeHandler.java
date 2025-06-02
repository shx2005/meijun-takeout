package com.mo.web.handler;

import com.mo.common.enumeration.ItemType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemTypeTypeHandler extends BaseTypeHandler<ItemType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ItemType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public ItemType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return ItemType.fromValue(value);
    }

    @Override
    public ItemType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return ItemType.fromValue(value);
    }

    @Override
    public ItemType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return ItemType.fromValue(value);
    }
}
