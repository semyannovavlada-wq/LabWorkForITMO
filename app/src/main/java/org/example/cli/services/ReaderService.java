package org.example.cli.services;

import java.util.Arrays;
import java.util.List;

public class ReaderService {

    public List<String> readCommand() {
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                return List.of();
            }
            return Arrays.asList(input.trim().split("\\s+"));
        }
        return List.of();
    }
}
