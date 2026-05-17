package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class SQLQueryBuilder {

    public String sqlQuery(ObjectSearchCriteria criteria, List<Object> params) {
        List<String> conditions = new ArrayList<>();

        applyNameFilter(criteria, conditions, params);
        applyCardTypeFilter(criteria, conditions, params);
        applyIdFilter(criteria, conditions, params);

        String sqlQuery = "SELECT * FROM Cards";

        if (!conditions.isEmpty()) {
            sqlQuery += " WHERE " + String.join(" AND ", conditions);
        }

        return sqlQuery;
    }

    private void applyNameFilter(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params) {
        if (criteria.getObjectName() != null && !criteria.getObjectName().isBlank()) {
            conditions.add("LOWER(CharacterName) LIKE ?");
            params.add(criteria.getObjectName());
        }
    }

    private void applyCardTypeFilter(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params) {
        if (criteria.getCardType() != null) {
            conditions.add("CardType = ?");
            params.add(criteria.getCardType().name());
        }
    }
    private void applyIdFilter(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params) {
        if (criteria.getObjectId() != null) {
            conditions.add("CardId = ?");
            params.add(criteria.getObjectId());
        }
    }
}
