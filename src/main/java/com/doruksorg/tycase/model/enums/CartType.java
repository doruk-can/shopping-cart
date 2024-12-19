package com.doruksorg.tycase.model.enums;

public enum CartType {
    DEFAULT,
    DIGITAL;

    public static CartType fromItemType(ItemType itemType) {
        switch (itemType) {
            case DEFAULT:
                return DEFAULT;
            case DIGITAL:
                return DIGITAL;
            default:
                throw new IllegalArgumentException("Invalid item type");
        }
    }
}