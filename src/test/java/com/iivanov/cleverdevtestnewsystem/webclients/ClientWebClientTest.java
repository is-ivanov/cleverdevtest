package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.*;
import static org.assertj.core.api.Assertions.assertThat;

class ClientWebClientTest {

    public static MockWebServer mockServer;

    private ClientWebClient clientWebClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
            mockServer.getPort());
        WebClient webClient = WebClient.create(baseUrl);
        this.clientWebClient = new ClientWebClient(webClient);
    }

    @Test
    @DisplayName("getClients")
    void getClients() throws InterruptedException {
        String jsonBody = "[{" +
            "\"agency\": \"" + AGENCY_FIRST_PATIENT + "\"," +
            "\"createdDateTime\": \"2021-10-20 03:10:24\"," +
            "\"dob\": \"1960-02-02\"," +
            "\"firstName\": \""  + NAME_FIRST_PATIENT + "\"," +
            "\"guid\": \"" + GUID_FIRST_PATIENT + "\"," +
            "\"lastName\": \"" + LAST_NAME_FIRST_PATIENT + "\"," +
            "\"status\": \"ACTIVE\"" +
            "}]";
        mockServer.enqueue(
            new MockResponse().setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(jsonBody)
        );

        List<ClientResponseDto> clients = clientWebClient.getClients();
        RecordedRequest request = mockServer.takeRequest();

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).contains("/clients");

        assertThat(clients).hasSize(1);
        ClientResponseDto clientResponseDto = clients.get(0);
        assertThat(clientResponseDto.getGuid()).isEqualTo(GUID_FIRST_PATIENT);
        assertThat(clientResponseDto.getAgency()).isEqualTo(AGENCY_FIRST_PATIENT);
        assertThat(clientResponseDto.getFirstName()).isEqualTo(NAME_FIRST_PATIENT);
        assertThat(clientResponseDto.getLastName()).isEqualTo(LAST_NAME_FIRST_PATIENT);
    }


}