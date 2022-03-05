package com.iivanov.cleverdevtestnewsystem.webclients;

import com.iivanov.cleverdevtestnewsystem.dto.ClientNoteRequestDto;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.iivanov.cleverdevtestnewsystem.util.TestObjects.*;
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
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();
        final HttpClient httpClient = HttpClient.create()
            .wiretap("reactor.netty.http.client.HttpClient",
                LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .responseTimeout(Duration.ofMillis(1000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(1000,
                        TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(1000,
                        TimeUnit.MILLISECONDS)));
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders(headers -> {
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            })
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .build();
        this.noteWebClient = new NoteWebClient(webClient);
    }

    @Nested
    @DisplayName("test 'getNotesByClients' method")
    class GetNotesByClientsTest {
        @Test
        @DisplayName("verify request and serialization body")
        void verifyRequestAndSerializationBody() throws InterruptedException {
            ClientNoteRequestDto objectForRequest = createTestClientNoteRequestDto1();
            List<ClientNoteRequestDto> listRequestObjects =
                new ArrayList<>(List.of(objectForRequest));
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
            System.out.println(bodyRequest);
            assertThat(bodyRequest).extractingJsonPathStringValue("$.agency")
                .isEqualTo(AGENCY_FIRST_PATIENT);
            assertThat(bodyRequest).extractingJsonPathStringValue("$.clientGuid")
                .isEqualTo(GUID_FIRST_PATIENT);
        }
    }
}