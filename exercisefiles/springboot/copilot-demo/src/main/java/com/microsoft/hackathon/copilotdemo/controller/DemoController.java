package com.microsoft.hackathon.copilotdemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@RestController
public class DemoController {

    private final RestTemplate restTemplate;

    public DemoController(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
     * Create a GET operation to return the value of a key passed as query parameter.
     * If the key is not passed, return "key not passed".
     * If the key is passed, return "hello <key>".
     *
     */
    @GetMapping("/hello")
    public String greet(@RequestParam(value = "key", required = false) String key) {
        if (key == null || key.isEmpty()) {
            return "key not passed";
        }
        return "hello " + key;
    }

    /*
    * New operation under /diffdates that calculates the difference between two dates.
    * The operation should receive two dates as parameter in format dd-MM-yyyy and return the difference in days.
    * Convert the dates to LocalDate and use the between method to calculate the difference.
    * If the input parameters are not in the correct format, return a 400 status code.
     */
    @GetMapping("/diffdates")
    public String diffDates(@RequestParam(value = "date1") String date1, @RequestParam(value = "date2") String date2) {
        if (!date1.matches("\\d{2}-\\d{2}-\\d{4}") || !date2.matches("\\d{2}-\\d{2}-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }
        LocalDate localDate1 = LocalDate.parse(date1, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate localDate2 = LocalDate.parse(date2, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return String.valueOf(ChronoUnit.DAYS.between(localDate1, localDate2));
    }

    /**
     * New operation under /validate-spanish-phone number :
     * Validate the format of a spanish phone number (+34 prefix, then 9 digits, starting with 6,
     * 7 or 9).
     * The operation should receive a phone number as parameter and return true if the format is correct,
     * false otherwise.
     */
    @GetMapping("/validate-spanish-phone")
    public boolean validateSpanishPhoneNumber(@RequestParam(value = "phone") String phone) {
        return phone.matches("\\+34[6|7|9]\\d{8}");
    }

    /**
     * Validate the format of a spanish DNI (8 digits and 1 letter). The operation should receive a DNI as parameter and return true if the format is correct, false otherwise.
     */
    @GetMapping("/validate-spanish-dni")
    public boolean validateSpanishDni(@RequestParam(value = "dni") String dni) {
        return dni.matches("\\d{8}[A-Z]");
    }

    /**
     * Based on existing colors.json file under resources, given the name of the color as path parameter, return the hexadecimal code.
     * If the color is not found, return 404
     */
    @GetMapping("/color/{name}")
    public String getColor(@PathVariable(value = "name") String name) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/colors.json")) {
            JsonNode colors = objectMapper.readTree(inputStream);
            for (JsonNode color : colors) {
                if (color.get("color").asText().equalsIgnoreCase(name)) {
                    return color.get("code").get("hex").asText();
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading colors file");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Color not found");
    }

    /**
     * Create a new operation that call the API https://api.chucknorris.io/jokes/random and return the joke.
     * The function should call the API and return the joke.
     */
    @GetMapping("/chuck-norris-joke")
    public String getChuckNorrisJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("value").asText();
        } catch (IOException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching joke", e);
        }
    }


}