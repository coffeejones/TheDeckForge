package org.example.thedeckforge.entity;

import org.example.thedeckforge.entity.enums.TradeDirection;

public class TradeCard {

    private Long tradeCardId;
    private Card card;
    private TradeDirection tradeDirection;


    public TradeCard(){}
    public TradeCard(Long tradeCardId, Card card, TradeDirection tradeDirection) {
        this.tradeCardId = tradeCardId;
        this.card = card;
        this.tradeDirection = tradeDirection;
    }


    public Long getTradeCardId() {
        return tradeCardId;
    }
    public void setTradeCardId(Long tradeCardId) {
        this.tradeCardId = tradeCardId;
    }
    public Card getCard() {
        return card;
    }
    public void setCard(Card card) {
        this.card = card;
    }
    public TradeDirection getDirection() {
        return tradeDirection;
    }
    public void setDirection(TradeDirection tradeDirection) {
        this.tradeDirection = tradeDirection;
    }
}
