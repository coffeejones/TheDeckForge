package org.example.thedeckforge.entity;

import org.example.thedeckforge.entity.enums.CardType;

public class ObjectSearchCriteria {
    private Long objectId;
    private String objectName;
    private CardType cardType;
    private String sqlQuery;
    public ObjectSearchCriteria (){}

    public Long getObjectId() {
        return objectId;
    }
    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }
    public String getObjectName() {
        return objectName;
    }
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public CardType getCardType() {
        return cardType;
    }
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
    public String getSqlQuery() {
        return sqlQuery;
    }
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
}
