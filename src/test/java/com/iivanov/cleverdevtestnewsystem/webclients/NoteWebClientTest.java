package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.AGENCY_FIRST_PATIENT;
import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.createTestClientNoteRequestDtos;
import static org.assertj.core.api.Assertions.assertThat;

class NoteWebClientTest {

    public static MockWebServer mockServer;

    private NoteWebClient noteWebClient;
    private final BasicJsonTester json = new BasicJsonTester(this.getClass());

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s",
            mockServer.getPort());
        WebClient webClient = WebClient.create(baseUrl);
        this.noteWebClient = new NoteWebClient(webClient);
    }

    @Nested
    @DisplayName("test 'getNotesByClients' method")
    class GetNotesByClientsTest {
        @Test
        @DisplayName("verify request and serialization body")
        void verifyRequestAndSerializationBody() throws InterruptedException {
            List<ClientNoteRequestDto> listRequestObjects =
                createTestClientNoteRequestDtos();
            String bodyResponse = """
                [
                    {
                        "comments": "Patient Care Coordinator sent reminder for daily reading.",
                        "guid": "1F0BDFCB-900D-4205-9347-19BA2CAB6E5D",
                        "modifiedDateTime": "2021-11-12 16:31:53",
                        "clientGuid": "B9CD237D-D266-D861-F880-8BFB5B57F65B",
                        "loggedUser": "jos.moh",
                        "createdDateTime": "2021-11-12 10:11:53"
                    },
                    {
                        "comments": "Health Coach sent daily reminder.",
                        "guid": "CFB93E09-941F-C11F-35B5-ED42868BB106",
                        "modifiedDateTime": "2021-11-15 17:56:27",
                        "clientGuid": "B9CD237D-D266-D861-F880-8BFB5B57F65B",
                        "loggedUser": "mis.moh",
                        "createdDateTime": "2021-11-15 11:11:27"
                    }
                ]
                """;
            mockServer.enqueue(
                new MockResponse().setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(bodyResponse)
            );

            noteWebClient.getNotesByClients(listRequestObjects);

            RecordedRequest request = mockServer.takeRequest();
            JsonContent<Object> bodyRequest = json.from(request.getBody().readUtf8());
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/notes");

            assertThat(bodyRequest).extractingJsonPathStringValue("$.agency")
                .isEqualTo(AGENCY_FIRST_PATIENT);
        }
    }
}