package org.example.cli.util;

import java.util.List;

public final class Param {

    private Param() {
    }

    public static String paramValue(List<String> commandLine, String subCommand) {
        String paramKey = "--" + subCommand;
        int paramIndex = commandLine.indexOf(paramKey);

        if (paramIndex == -1 || paramIndex == commandLine.size() - 1) {
            return null;
        }

        return commandLine.get(paramIndex + 1);
    }
}