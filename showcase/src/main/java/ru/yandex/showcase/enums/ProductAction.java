package ru.yandex.showcase.enums;

public enum ProductAction {
    MINUS,
    PLUS,
    ADD_TO_CART,
    DELETE_FROM_CART;

    public static ProductAction fromString(String value) {
        return switch (value.toLowerCase()) {
            case "minus" -> MINUS;
            case "plus" -> PLUS;
            case "addtocart" -> ADD_TO_CART;
            case "deletefromcart" -> DELETE_FROM_CART;
            default -> throw new IllegalArgumentException("Unknown action: " + value);
        };
    }
}
