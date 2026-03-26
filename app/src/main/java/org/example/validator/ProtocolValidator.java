package org.example.validator;

import org.example.domain.MeasurementParam;

import java.util.Set;

public class ProtocolValidator {
    private static final int MAX_NAME_LENGTH = 128;

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: название протокола не может быть пустым");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Ошибка: название протокола должно быть до " + MAX_NAME_LENGTH + " символов");
        }
    }

    public static void validateRequiredParams(Set<MeasurementParam> requiredParams) {
        if (requiredParams == null || requiredParams.isEmpty()) {
            throw new IllegalArgumentException("Ошибка: список обязательных параметров не может быть пустым");
        }
    }

    public static void validateOwnerUsername(String ownerUsername) {
        if (ownerUsername == null || ownerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец не может быть пустым");
        }
    }
}