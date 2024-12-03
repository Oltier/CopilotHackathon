package com.microsoft.hackathon.copilotdemo;

import com.microsoft.hackathon.copilotdemo.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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

}