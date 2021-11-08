package org.foodmonks.backend.datatypes;

public class CriterioQuery {
    private String key;
    private String operation;
    private Object value;
    private boolean isOrPredicate;

    public CriterioQuery(String key, String operation, Object value, boolean isOrPredicate) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.isOrPredicate = isOrPredicate;
    }

    public boolean isOrPredicate() {
        return isOrPredicate;
    }

    public void setOrPredicate(boolean orPredicate) {
        isOrPredicate = orPredicate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
