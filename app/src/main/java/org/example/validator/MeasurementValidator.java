package org.example.validator;

import org.example.domain.MeasurementParam;

public class MeasurementValidator {
    private static final int MAX_UNIT_LENGTH = 16;
    private static final int MAX_METHOD_LENGTH = 64;

    public static void validateParam(MeasurementParam param) {
        if (param == null) {
            throw new IllegalArgumentException("Ошибка: параметр измерения не может быть пустым");
        }
    }

    public static void validateValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Ошибка: значение измерения не может быть NaN или Infinity");
        }
    }

    public static void validateUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: единицы измерения не могут быть пустыми");
        }
        if (unit.length() > MAX_UNIT_LENGTH) {
            throw new IllegalArgumentException("Ошибка: единицы измерения должны быть до " + MAX_UNIT_LENGTH + " символов");
        }
    }

    public static void validateMethod(String method) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: метод измерения не может быть пустым");
        }
        if (method.length() > MAX_METHOD_LENGTH) {
            throw new IllegalArgumentException("Ошибка: метод измерения должен быть до " + MAX_METHOD_LENGTH + " символов");
        }
    }

    public static void validateOwnerUsername(String ownerUsername) {
        if (ownerUsername == null || ownerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец не может быть пустым");
        }
    }
}