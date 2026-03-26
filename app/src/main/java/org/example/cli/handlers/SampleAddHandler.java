package org.example.cli.handlers;

import org.example.cli.services.ReaderService;
import org.example.domain.Sample;
import org.example.domain.SampleStatus;
import org.example.services.SampleService;

import java.util.Collection;
import java.util.List;

public class SampleAddHandler implements BaseHandler {
    private final ReaderService readerService = new ReaderService();

    @Override
    public boolean handle(List<String> params, SampleService sampleService, Collection<BaseHandler> commandList) {
        try {
            // Ввод названия
            System.out.println("Название:");
            String name = String.join("", readerService.readCommand()).trim();
            if (name.isEmpty()) {
                System.out.println("Ошибка: название не может быть пустым");
                return true;
            }
            if (name.length() > 128) {
                System.out.println("Ошибка: название слишком длинное (макс. 128 символов)");
                return true;
            }

            // Ввод типа
            System.out.println("Тип:");
            String type = String.join("", readerService.readCommand()).trim();
            if (type.isEmpty()) {
                System.out.println("Ошибка: тип не может быть пустым");
                return true;
            }
            if (type.length() > 64) {
                System.out.println("Ошибка: тип слишком длинный (макс. 64 символа)");
                return true;
            }

            // Ввод местоположения
            System.out.println("Место:");
            String location = String.join("", readerService.readCommand()).trim();
            if (location.isEmpty()) {
                System.out.println("Ошибка: местоположение не может быть пустым");
                return true;
            }
            if (location.length() > 64) {
                System.out.println("Ошибка: местоположение слишком длинное (макс. 64 символа)");
                return true;
            }

            // Создание образца
            Sample sample = sampleService.add(name, type, location, "SYSTEM", SampleStatus.ACTIVE);
            System.out.println("OK sample_id=" + sample.getId());

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String help() {
        return "sample_add - создать новый образец (интерактивно)";
    }
}