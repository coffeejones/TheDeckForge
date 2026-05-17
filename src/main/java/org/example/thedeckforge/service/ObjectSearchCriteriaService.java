package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.enums.CardType;
import org.springframework.stereotype.Service;

@Service
public class ObjectSearchCriteriaService {

    public ObjectSearchCriteria createSearchCriteria(Long Id) {
        ObjectSearchCriteria criteria = new ObjectSearchCriteria();
        criteria.setObjectId(Id);
        return criteria;
    }

    public ObjectSearchCriteria createSearchCriteria(String searchTerm, CardType cardType) {
        ObjectSearchCriteria criteria = new ObjectSearchCriteria();

        if (searchTerm != null && !searchTerm.isBlank()) {
            criteria.setObjectName(returnStringWithLikeOperator(searchTerm));
        }

        if (cardType != null) {
            criteria.setCardType(cardType);
        }

        return criteria;
    }

    private String returnStringWithLikeOperator(String searchTerm){
        return "%"+searchTerm.toLowerCase()+"%";
    }
}
