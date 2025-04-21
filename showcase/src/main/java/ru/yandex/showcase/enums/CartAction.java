package ru.yandex.showcase.enums;

public enum CartAction {
    MINUS,
    PLUS,
    DELETE;

    public static CartAction fromString(String value) {
        return switch (value.toLowerCase()) {
            case "minus" -> MINUS;
            case "plus" -> PLUS;
            case "delete" -> DELETE;
            default -> throw new IllegalArgumentException("Unknown action: " + value);
        };
    }
}
