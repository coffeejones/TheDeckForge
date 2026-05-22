package org.example.thedeckforge.infrastructure.sqlquerybuilders;

import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.infrastructure.sqlquerybuilders.SQLQueryBuilder;
import org.example.thedeckforge.infrastructure.sqlquerybuilders.SQLQueryFilter;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class CardSQLQueryBuilder extends SQLQueryBuilder {
    private final SQLQueryFilter filter;

    public CardSQLQueryBuilder(SQLQueryFilter filter) {
        this.filter = filter;
    }


    protected String getBaseSQLQuery() {
        return "SELECT * FROM Cards";

    }


    public void applyFilters(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params) {
        filter.applyNameFilter(criteria.getObjectName(), "CharacterName", conditions, params);
        filter.applyEnumFilter(criteria.getCardType(), "CardType", conditions, params);
        filter.applyIdFilter(criteria.getObjectId(), "CardId", conditions, params);
    }

}