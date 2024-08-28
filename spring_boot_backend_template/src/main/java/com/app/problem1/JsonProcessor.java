package com.app.problem1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class JsonProcessor {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <PRN Number> <path to json file>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s", ""); // Normalize PRN
        String jsonFilePath = args[1];
        String destinationValue = "";

        try {
            destinationValue = findDestinationValue(new File(jsonFilePath));
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the provided JSON file.");
                return;
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
            return;
        }

        String randomString = generateRandomString(8);
        String toHash = prnNumber + destinationValue + randomString;

        try {
            String hashedValue = generateMD5Hash(toHash);
            System.out.println(hashedValue + ";" + randomString);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestinationValue(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonFile);
        return traverseJsonForKey(root, "destination");
    }

    private static String traverseJsonForKey(JsonNode node, String key) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getKey().equals(key)) {
                    return entry.getValue().asText();
                } else {
                    String value = traverseJsonForKey(entry.getValue(), key);
                    if (value != null) return value;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String value = traverseJsonForKey(arrayElement, key);
                if (value != null) return value;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
