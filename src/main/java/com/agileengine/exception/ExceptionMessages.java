package com.agileengine.exception;

public enum ExceptionMessages {
    RESOURCE_NOT_FOUND("Resource not found"),
    INVALID_PRODUCT_DATA("Invalid product data"),
    INVALID_ORDER_DATA("Invalid order data"),
    INVALID_ORDER_ITEM_DATA("Invalid order item data"),
    NO_ORDER_ITEMS("There are no order items"),
    INVALID_ORDER_OPERATION("Could not perform order operation"),
    ORDER_ALREADY_FINISHED("Order already finished");

    private final String message;

    private ExceptionMessages(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
