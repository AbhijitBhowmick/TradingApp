package com.trading.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.zerodhatech.models.Instrument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.yaml.snakeyaml.nodes.Tag.PREFIX;


public class TradeUtility {

    private static final Path TOKEN_FILE_PATH = Path.of("token.txt");
    private static final String REQUEST_TOKEN_PREFIX = "Request_Token=";
    private static final Duration MAX_TOKEN_AGE = Duration.ofHours(24);


    public static void convertJsonToCsv(List<Instrument> instruments, String outputFilePath) {
        if (instruments == null || instruments.isEmpty()) {
            System.out.println("No data to write.");
            return;
        }

        // Use reflection on the first Instrument to get headers
        Instrument first = instruments.getFirst(); // Java 21
        Field[] fields = first.getClass().getFields();

        // Extract header names
        List<String> headers = Arrays.stream(fields)
                .map(Field::getName)
                .toList();

        try (CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath))) {
            // Write header
            writer.writeNext(headers.toArray(new String[0]));

            // Write rows
            for (Instrument instrument : instruments) {
                List<String> row = new ArrayList<>();
                for (Field field : fields) {
                    field.setAccessible(true); // Required for reading public fields
                    Object value = field.get(instrument);
                    row.add(value != null ? value.toString() : ""); // null-safe
                }
                writer.writeNext(row.toArray(new String[0]));
            }

            System.out.println("CSV written to: " + outputFilePath);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the current timestamp and token to the file in the format:
     * request_token=Timestamp|requesttoken
     */
    public static void writeToken(String token) throws IOException {
        // Ensure parent directories exist (if needed)
        Path parent = TOKEN_FILE_PATH.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String line = REQUEST_TOKEN_PREFIX + timestamp + "|" + token;
        Files.writeString(TOKEN_FILE_PATH, line);
        System.out.println("Token written to file:");
        System.out.println(line);
    }

    /**
     * Reads the file and returns the token if still fresh (within 24h).
     * Otherwise returns Optional.empty()
     */
    public static Optional<String> readToken() throws IOException {
        if (!Files.exists(TOKEN_FILE_PATH)) {
            return Optional.empty();
        }

        String line = Files.readString(TOKEN_FILE_PATH).trim();
        if (!line.startsWith(PREFIX)) {
            return Optional.empty();
        }

        String[] parts = line.substring(PREFIX.length()).split("\\|", 2);
        if (parts.length != 2) {
            return Optional.empty();
        }

        try {
            Instant tokenTimestamp = Instant.parse(parts[0]);
            if (Duration.between(tokenTimestamp, Instant.now()).compareTo(MAX_TOKEN_AGE) <= 0) {
                return Optional.of(parts[1]);
            } else {
                System.out.println("Token is expired (older than 24 hours).");
                return Optional.empty();
            }
        } catch (DateTimeParseException e) {
            System.err.println("Invalid timestamp format.");
            return Optional.empty();
        }
    }
}
