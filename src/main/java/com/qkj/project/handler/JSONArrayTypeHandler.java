package com.qkj.project.handler;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author KeJiang Qi
 * @date 2025/4/9 - 10:29
 * @description JSONArray 类型的处理器 <br/>
 * 调用方式 <br/>
 * <img src="https://pic1.imgdb.cn/item/68a6c01058cb8da5c840abc0.png" alt="1.png"> <br/>
 * <img src="https://pic1.imgdb.cn/item/68a6c01058cb8da5c840abbf.png" alt="2.png">
 */
@MappedTypes(JSONArray.class)
public class JSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toJSONString());
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJSON(json);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJSON(json);
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJSON(json);
    }

    private JSONArray parseJSON(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSONArray.parseArray(json);
        } catch (Exception e) {
            // 如果解析失败，返回空数组
            return new JSONArray();
        }
    }
}