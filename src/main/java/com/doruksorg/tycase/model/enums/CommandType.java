package com.doruksorg.tycase.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CommandType {

    ADD_ITEM("addItem"),
    ADD_VAS_ITEM_TO_ITEM("addVasItemToItem"),
    REMOVE_ITEM("removeItem"),
    RESET_CART("resetCart"),
    DISPLAY_CART("displayCart"),
    EXIT("exit");

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static CommandType fromCommand(String command) {
        for (CommandType type : CommandType.values()) {
            if (type.getCommand().equals(command)) {
                return type;
            }
        }
        return null;
    }

    public static List<String> getAllCommands() {
        return Arrays.stream(CommandType.values())
                .map(CommandType::getCommand)
                .collect(Collectors.toList());
    }
}