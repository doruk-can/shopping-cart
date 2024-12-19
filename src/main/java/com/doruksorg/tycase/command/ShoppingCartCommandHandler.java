package com.doruksorg.tycase.command;

import com.doruksorg.tycase.exception.CommandValidationException;
import com.doruksorg.tycase.model.enums.CommandType;
import com.doruksorg.tycase.model.mockapi.request.AddItemRequest;
import com.doruksorg.tycase.model.mockapi.request.AddVasItemRequest;
import com.doruksorg.tycase.model.mockapi.request.RemoveItemRequest;
import com.doruksorg.tycase.service.cart.service.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static com.doruksorg.tycase.util.JsonUtil.jsonResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("local")
public class ShoppingCartCommandHandler {

    private static final String defaultUserId = "62e0e44c553403f7a3d70715"; // For the demo case
    private final ObjectMapper objectMapper;
    private final CartService cartService;
    private final Validator validator;

    @Value("${app.runPostConstruct}")
    private boolean runPostConstruct;

    @EventListener(ApplicationReadyEvent.class)
    public void processConsoleInput() {
        if (!runPostConstruct) {
            return;
        }

        final String outputFilePath = "output/results.txt";
        List<String> outputResults = new ArrayList<>();
        System.out.println("You can start entering commands. Type '" + CommandType.EXIT.getCommand() + "' to finish." + " Possible commands: " + CommandType.getAllCommands());

        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter command:");
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase(CommandType.EXIT.getCommand())) {
                    break;
                }
                String payloadString = handlePayload(scanner, command);
                String result = processCommand(command, payloadString);
                outputResults.add(result);
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        writeResultsToFile(outputFilePath, outputResults);
    }

    private String handlePayload(Scanner scanner, String command) {
        String payloadString = "";
        if (!command.equals(CommandType.DISPLAY_CART.getCommand()) && !command.equals(CommandType.RESET_CART.getCommand())) {
            System.out.println("Enter payload in JSON format:");
            payloadString = scanner.nextLine();
        }
        return payloadString;
    }

    private void writeResultsToFile(String outputFilePath, List<String> outputResults) {
        try {
            Path outputPath = Paths.get(outputFilePath);
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, outputResults);
            System.out.println("File saved to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private String processCommand(String command, String payloadString) {
        try {
            CommandType commandType = CommandType.fromCommand(command);
            if (commandType == null) {
                System.out.println(String.format("Invalid command %s", command));
                return jsonResponse(false, "Invalid command");
            }
            switch (commandType) {
                case ADD_ITEM:
                    AddItemRequest addItemRequest = objectMapper.readValue(payloadString, AddItemRequest.class);
                    validateRequest(addItemRequest);
                    return cartService.addItem(defaultUserId, addItemRequest);
                case ADD_VAS_ITEM_TO_ITEM:
                    AddVasItemRequest addVasItemRequest = objectMapper.readValue(payloadString, AddVasItemRequest.class);
                    validateRequest(addVasItemRequest);
                    return cartService.addVasItem(defaultUserId, addVasItemRequest);
                case REMOVE_ITEM:
                    RemoveItemRequest removeItemRequest = objectMapper.readValue(payloadString, RemoveItemRequest.class);
                    validateRequest(removeItemRequest);
                    return cartService.removeItem(defaultUserId, removeItemRequest);
                case RESET_CART:
                    return cartService.resetCart(defaultUserId);
                case DISPLAY_CART:
                    return cartService.displayCart(defaultUserId);
                default:
                    System.out.println(String.format("Invalid command %s", command));
                    return jsonResponse(false, "Invalid command");
            }
        } catch (CommandValidationException e) {
            System.out.println(String.format("Validation failed for %s command: %s", command, e.getMessage()));
            return jsonResponse(false, e.getMessage());
        } catch (JsonProcessingException e) {
            System.out.println(String.format("Invalid payload for %s command", command));
            return jsonResponse(false, String.format("Invalid payload for %s command", command));
        } catch (Exception e) {
            System.out.println(String.format("Error processing command %s", command));
            return jsonResponse(false, e.getMessage());
        }
    }

    private <T> void validateRequest(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new CommandValidationException("Validation failed: " + violations);
        }
    }

}
