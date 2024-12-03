package com.microsoft.hackathon.copilotdemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);
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
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            log.error("Error fetching joke", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching joke", e);
        }
    }

    /**
     * 7. URL parsing
     * Given a url as query parameter, parse it and return the protocol, host, port, path and query parameters.
     * The response should be in Json format.
     */
    @GetMapping("/parse-url")
    public Map<String, Object> parseUrl(@RequestParam(value = "url") String url) {
        try {
            URL parsedUrl = new URL(URLDecoder.decode(url, StandardCharsets.UTF_8));
            Map<String, Object> result = new HashMap<>();
            result.put("protocol", parsedUrl.getProtocol());
            result.put("host", parsedUrl.getHost());
            result.put("port", parsedUrl.getPort());
            result.put("path", parsedUrl.getPath());
            result.put("query", parsedUrl.getQuery());
            return result;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
    }

    /**
     * 8. List files and folders
     * List files and folders under a given path. The path should be a query parameter. The response should be in Json format.
     */

    @GetMapping("/list-files")
    public Map<String, Object> listFilesAndFolders(@RequestParam("path") String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Path does not exist: " + path);
        }

        if (!directory.isDirectory()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The provided path is not a directory: " + path);
        }

        File[] filesAndFolders = directory.listFiles();

        return getFilesAndFoldersOnPath(path, filesAndFolders);
    }

    private static Map<String, Object> getFilesAndFoldersOnPath(final String path, final File[] filesAndFolders) {
        Map<String, Object> response = new HashMap<>();
        List<String> files = new ArrayList<>();
        List<String> folders = new ArrayList<>();

        if (filesAndFolders != null) {
            for (File file : filesAndFolders) {
                if (file.isFile()) {
                    files.add(file.getName());
                } else if (file.isDirectory()) {
                    folders.add(file.getName());
                }
            }
        }

        response.put("path", path);
        response.put("files", files);
        response.put("folders", folders);
        return response;
    }

    /**
     * Given the path of a file and count the number of occurrence of a provided word. The path and the word should be query parameters. The response should be in Json format.
     */
    @GetMapping("/count-word")
    public Map<String, Object> countWordOccurrences(@RequestParam("path") String path,
                                                    @RequestParam("word") String word) {
        if (word.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Word cannot be empty");
        }

        File file = new File(path);

        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File does not exist: " + path);
        }

        if (!file.isFile()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The provided path is not a file: " + path);
        }

        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCount += countOccurrences(line, word);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading the file: " + path, e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("path", path);
        response.put("word", word);
        response.put("occurrences", wordCount);

        return response;
    }

    private int countOccurrences(String line, String word) {
        int count = 0;
        int index = 0;

        while ((index = line.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }

        return count;
    }

    @GetMapping("/zip-folder")
    public ResponseEntity<InputStreamResource> zipFolder(@RequestParam("path") String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Folder does not exist: " + path);
        }

        if (!folder.isDirectory()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The provided path is not a folder: " + path);
        }

        String zipFilePath = folder.getAbsolutePath() + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            zipFolderContents(folder, folder.getName(), zipOut);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating ZIP file", e);
        }

        try {
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(Paths.get(zipFilePath)));

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folder.getName() + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading ZIP file", e);
        }
    }

    private void zipFolderContents(File folder, String parentFolder, ZipOutputStream zipOut) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String zipEntryName = parentFolder + "/" + file.getName();
            if (file.isDirectory()) {
                zipFolderContents(file, zipEntryName, zipOut);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    zipOut.putNextEntry(new ZipEntry(zipEntryName));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zipOut.write(buffer, 0, length); // Pass offset (0) and length
                    }
                    zipOut.closeEntry();
                }
            }
        }
    }


}