package org.example.thedeckforge.infrastructure.sqlquerybuilders;

import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class SQLQueryFilter {
    public void applyNameFilter(String name, String column, List<String> conditions, List<Object> params) {
        if (name != null && !name.isBlank()) {
            conditions.add("LOWER(" + column + ") LIKE ?");
            params.add("%" + name.toLowerCase() + "%");
        }
    }

    public void applyEnumFilter(Enum<?> value, String column, List<String> conditions, List<Object> params) {
        if (value != null) {
            conditions.add(column + " = ?");
            params.add(value.name());
        }
    }

    public void applyIdFilter(Long id, String column, List<String> conditions, List<Object> params) {
        if (id != null) {
            conditions.add(column + " = ?");
            params.add(id);
        }
    }
}