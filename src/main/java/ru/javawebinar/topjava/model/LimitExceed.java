package ru.javawebinar.topjava.model;

public class LimitExceed {
    private boolean exces;

    public LimitExceed(boolean exces) {
        this.exces = exces;
    }

    public boolean isExces() {
        return exces;
    }

    public void setExces(boolean exces) {
        this.exces = exces;
    }
}
