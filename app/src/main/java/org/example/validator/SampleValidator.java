package org.example.validator;

public class SampleValidator {
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_TYPE_LENGTH = 64;
    private static final int MAX_LOCATION_LENGTH = 64;

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: название не может быть пустым");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Ошибка: название слишком длинное (макс. " + MAX_NAME_LENGTH + " символов)");
        }
    }

    public static void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: тип не может быть пустым");
        }
        if (type.length() > MAX_TYPE_LENGTH) {
            throw new IllegalArgumentException("Ошибка: тип слишком длинный (макс. " + MAX_TYPE_LENGTH + " символов)");
        }
    }

    public static void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: местоположение не может быть пустым");
        }
        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new IllegalArgumentException("Ошибка: местоположение слишком длинное (макс. " + MAX_LOCATION_LENGTH + " символов)");
        }
    }

    public static void validateOwnerUsername(String ownerUsername) {
        if (ownerUsername == null || ownerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Ошибка: владелец не может быть пустым");
        }
    }
}