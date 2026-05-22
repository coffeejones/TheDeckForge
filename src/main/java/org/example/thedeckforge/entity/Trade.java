package org.example.thedeckforge.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Trade {

    private Long tradeId;
    private Long proposerId;
    private Long responderId;
    private String proposerName;
    private String responderName;
    private TradeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private List<TradeCard> offered;
    private List<TradeCard> wanted;
    private List<TradeCard> counterOffered;

    public Trade(long tradeId, long proposerId, long responderId, TradeStatus status, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.tradeId = tradeId;
        this.proposerId = proposerId;
        this.responderId = responderId;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public Trade(){}

    public Long getTradeId() {
        return tradeId;
    }
    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }
    public Long getProposerId() {
        return proposerId;
    }
    public void setProposerId(Long proposerId) {
        this.proposerId = proposerId;
    }
    public Long getResponderId() {
        return responderId;
    }
    public void setResponderId(Long responderId) {
        this.responderId = responderId;
    }
    public TradeStatus getStatus() {
        return status;
    }
    public void setStatus(TradeStatus status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    public List<TradeCard> getOffered() {
        return offered;
    }
    public void setOffered(List<TradeCard> offered) {
        this.offered = offered;
    }
    public List<TradeCard> getWanted() {
        return wanted;
    }
    public void setWanted(List<TradeCard> wanted) {
        this.wanted = wanted;
    }
    public List<TradeCard> getCounterOffered() {
        return counterOffered;
    }
    public void setCounterOffered(List<TradeCard> counterOffered) {
        this.counterOffered = counterOffered;
    }
    public String getProposerName() {
        return proposerName;
    }
    public void setProposerName(String name) {
        this.proposerName = name;
    }
    public String getResponderName() {
        return responderName;
    }
    public void setResponderName(String name) {
        this.responderName = name;
    }
}
