package org.example.thedeckforge.infrastructure.sqlquerybuilders;

import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class CardSQLQueryBuilder extends SQLQueryBuilder {

    private final SQLQueryFilterHelper filter;

    public CardSQLQueryBuilder(SQLQueryFilterHelper filter) {
        this.filter = filter;
    }

    @Override
    protected String getBaseSQLQuery() {
        return "SELECT * FROM Cards";
    }

    @Override
    public void applyFilters(ObjectSearchCriteria criteria, List<String> conditions, List<Object> params) {
        filter.applyNameFilter(criteria.getObjectName(), "CharacterName", conditions, params);
        filter.applyCardTypeFilter(criteria.getCardType(), "CardType", conditions, params);
        filter.applyIdFilter(criteria.getObjectId(), "CardId", conditions, params);
    }

}
