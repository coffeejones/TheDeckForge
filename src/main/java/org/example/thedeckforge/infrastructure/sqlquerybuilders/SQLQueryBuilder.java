package org.example.thedeckforge.infrastructure.sqlquerybuilders;

import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLQueryBuilder {

    public final String buildQuery(ObjectSearchCriteria criteria, List<Object> params) {
        List<String> conditions = new ArrayList<>();
        applyFilters(criteria, conditions, params);
        return buildFinalQuery(getBaseSQLQuery(), conditions);
    }

    protected abstract String getBaseSQLQuery();
    protected abstract void applyFilters(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params);

    private String buildFinalQuery(String baseQuery, List<String> conditions) {
        if (conditions.isEmpty()) {
            return baseQuery;
        }
        return baseQuery + " WHERE " + String.join(" AND ", conditions);
    }
}