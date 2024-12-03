package com.microsoft.hackathon.copilotdemo;

import com.microsoft.hackathon.copilotdemo.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;;import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest()
@AutoConfigureMockMvc
@Import(Config.class)
class CopilotDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void hello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello?key=world"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("hello world"));
    }

    @Test
    void diffDates_withValidDates_returnsCorrectDifference() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/diffdates?date1=01-01-2023&date2=10-01-2023"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("9"));
    }

    @Test
    void diffDates_withSameDates_returnsZeroDifference() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/diffdates?date1=01-01-2023&date2=01-01-2023"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("0"));
    }

    @Test
    void diffDates_withInvalidDateFormat_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/diffdates?date1=2023-01-01&date2=10-01-2023"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void diffDates_withMissingDate1_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/diffdates?date2=10-01-2023"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void diffDates_withMissingDate2_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/diffdates?date1=01-01-2023"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void validateSpanishPhoneNumber_withValidPhoneNumber_returnsTrue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+34612345678"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void validateSpanishPhoneNumber_withInvalidPhoneNumber_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+34123456789"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishPhoneNumber_withMissingPlusSign_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=34612345678"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishPhoneNumber_withInvalidPrefix_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+35123456789"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishPhoneNumber_withInvalidStartingDigit_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+34512345678"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishPhoneNumber_withShortPhoneNumber_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+3461234567"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishPhoneNumber_withLongPhoneNumber_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-phone?phone=+346123456789"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishDni_withValidDni_returnsTrue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-dni?dni=12345678A"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void validateSpanishDni_withInvalidDni_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-dni?dni=1234567A"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishDni_withInvalidLetter_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-dni?dni=12345678a"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishDni_withTooManyDigits_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-dni?dni=123456789A"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void validateSpanishDni_withSpecialCharacters_returnsFalse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/validate-spanish-dni?dni=12345678@"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("false"));
    }

    /**
     * Based on existing colors.json file under resources, given the name of the color as path parameter, return the hexadecimal code.
     * If the color is not found, return 404
     * <p>
     * Hint: Use TDD. Start by creating the unit test and then implement the code.
     */
    @Test
    void getColorHex_withValidColor_returnsHexCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/color/blue"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("#0000FF"));
    }

    @Test
    void getColorHex_withInvalidColor_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/color/unknown"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getColorHex_withEmptyColor_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/color/"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getColorHex_withSpecialCharactersInColor_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/color/bl@e"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getColorHex_withColorInDifferentCase_returnsHexCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/color/BLUE"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("#0000FF"));
    }

    @Test
    void getChuckNorrisJoke_returnsJoke() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://api.chucknorris.io/jokes/random")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("{\"value\": \"Chuck Norris joke\"}", MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/chuck-norris-joke"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().string("Chuck Norris joke"));
    }

    @Test
    void getChuckNorrisJoke_whenApiFails_returnsInternalServerError() throws Exception {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://api.chucknorris.io/jokes/random")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withServerError());

        mockMvc.perform(MockMvcRequestBuilders.get("/chuck-norris-joke"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void parseUrl_withValidUrl_returnsParsedComponents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url?url=http://example.com:8080/path?query=param"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol").value("http"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.host").value("example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.port").value(8080))
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/path"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.query").value("query=param"));
    }

    @Test
    void parseUrl_withInvalidUrl_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url?url=invalid-url"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void parseUrl_withMissingUrlParameter_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void parseUrl_withUrlWithoutPort_returnsParsedComponents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url?url=http://example.com/path?query=param"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol").value("http"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.host").value("example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.port").value(-1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/path"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.query").value("query=param"));
    }

    @Test
    void parseUrl_withUrlWithoutQuery_returnsParsedComponents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url?url=http://example.com:8080/path"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol").value("http"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.host").value("example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.port").value(8080))
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/path"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.query").isEmpty());
    }

    @Test
    void parseUrl_withMultipleQueryParameters_returnsParsedComponents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/parse-url?url=http%3A%2F%2Fexample.com%3A8080%2Fpath%3Fquery1%3Dparam1%26query2%3Dparam2"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol").value("http"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.host").value("example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.port").value(8080))
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/path"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.query").value("query1=param1&query2=param2"));
    }

    @Test
    void listFilesAndFolders_withValidPath_returnsFilesAndFolders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/list-files?path=src/main/resources"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("src/main/resources"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.files").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.folders").isArray());
    }

    @Test
    void listFilesAndFolders_withNonExistentPath_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/list-files?path=/non/existent/path"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void listFilesAndFolders_withFilePath_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/list-files?path=src/main/resources/colors.json"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void listFilesAndFolders_withEmptyPath_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/list-files?path="))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void countWordOccurrences_withValidFileAndWord_returnsCorrectCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/count-word?path=src/main/resources/colors.json&word=hue"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("src/main/resources/colors.json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.word").value("hue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences").value(5));
    }

    @Test
    void countWordOccurrences_withNonExistentFile_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/count-word?path=/non/existent/file.txt&word=test"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void countWordOccurrences_withDirectoryPath_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/count-word?path=src/main/resources&word=test"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void countWordOccurrences_withEmptyWord_returnsZeroOccurrences() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/count-word?path=src/test/resources/testfile.txt&word="))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void countWordOccurrences_withSpecialCharactersInWord_returnsCorrectCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/count-word?path=src/main/resources/colors.json&word=hue\""))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("src/main/resources/colors.json"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.word").value("hue\""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.occurrences").value(5));
    }

    @Test
    void zipFolder_withValidFolder_returnsZippedFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/zip-folder?path=src/main/resources"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resources.zip"))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void zipFolder_withNonExistentFolder_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/zip-folder?path=/non/existent/folder"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void zipFolder_withFilePath_returnsError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/zip-folder?path=src/test/resources/testfile.txt"))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

}