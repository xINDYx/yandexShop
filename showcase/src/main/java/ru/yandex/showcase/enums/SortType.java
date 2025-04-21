package ru.yandex.showcase.enums;

public enum SortType {
    ALPHA,
    PRICE;

    public static SortType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "ALPHA" -> ALPHA;
            case "PRICE" -> PRICE;
            default -> throw new IllegalArgumentException("Unknown sortType: " + value);
        };
    }
}
