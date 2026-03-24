package org.example.cli.services;

import java.util.Arrays;
import java.util.List;

public class ReaderService {

    public List<String> readCommand() {
        String input = System.console().readLine();
        if (input == null) {
            return List.of();
        }
        return Arrays.asList(input.trim().split(" "));
    }
}
