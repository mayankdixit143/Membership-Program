package com.firstclub.membership.enums;

public enum TierType {
    SILVER(1),
    GOLD(2),
    PLATINUM(3);

    private final int level;

    TierType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(TierType other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(TierType other) {
        return this.level < other.level;
    }
}
