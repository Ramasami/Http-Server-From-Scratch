package httpserver.http;


import httpserver.http.message.HttpParser;
import httpserver.http.message.HttpRequest;
import httpserver.util.HttpParsingException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static httpserver.http.enums.HttpMethod.GET;
import static httpserver.http.enums.HttpStatusCode.*;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpParserTest {

    private HttpParser httpParser;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParserTest.class);

    @Before
    public void before() {
        httpParser = new HttpParser();
    }

    private InputStream getInputStream(String request) {
        return new ByteArrayInputStream(request.getBytes(US_ASCII));
    }

    @Test
    public void parseHttpRequestTestValidGet() throws HttpParsingException {
        HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                "GET / HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "\r\n" +
                        "hello"
        ));
        LOGGER.debug("Request: {}", request);
        assertEquals(GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getHttpVersion().getVersion());
        assertEquals(2, request.getHeaders().size());
        assertEquals("localhost:8080", request.getHeaders().get("Host"));
        assertEquals("keep-alive", request.getHeaders().get("Connection"));
        assertEquals("hello", request.getMessage());
    }

    @Test
    public void parseHttpRequestTestValidGetWithoutHeader() throws HttpParsingException {
        HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                "GET / HTTP/1.1\r\n" +
                        "\r\n" +
                        "hello"
        ));
        LOGGER.debug("Request: {}", request);
        assertEquals(GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getHttpVersion().getVersion());
        assertEquals(0, request.getHeaders().size());
        assertEquals("hello", request.getMessage());
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithoutHeader() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "hello"
            ));
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    public void parseHttpRequestTestValidGetWithoutMessage() throws HttpParsingException {
        HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                "GET / HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "\r\n"
        ));
        LOGGER.debug("Request: {}", request);
        assertEquals(GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getHttpVersion().getVersion());
        assertEquals(2, request.getHeaders().size());
        assertEquals("localhost:8080", request.getHeaders().get("Host"));
        assertEquals("keep-alive", request.getHeaders().get("Connection"));
        assertEquals("", request.getMessage());
    }

    @Test
    public void parseHttpRequestTestValidGetWithoutHeaderAndMessage() throws HttpParsingException {
        HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                "GET / HTTP/1.1\r\n"
        ));
        LOGGER.debug("Request: {}", request);
        assertEquals(GET, request.getMethod());
        assertEquals("/", request.getRequestTarget());
        assertEquals("HTTP/1.1", request.getHttpVersion().getVersion());
        assertEquals(0, request.getHeaders().size());
        assertEquals("", request.getMessage());
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithoutRequestLine() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "\r\n" +
                            "Host: localhost:8080\r\n" +
                            "Connection: keep-alive\r\n" +
                            "\r\n" +
                            "hello"
            ));
            LOGGER.debug("Request: {}", request);
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    public void parseHttpRequestTestValidGetWithoutRequestLine2() throws HttpParsingException {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "Host: localhost:8080\r\n" +
                            "Connection: keep-alive\r\n" +
                            "\r\n" +
                            "hello"
            ));
            LOGGER.debug("Request: {}", request);
        } catch (HttpParsingException e) {
            assertEquals(NOT_IMPLEMENTED, e.getErrorCode());
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithWrongMethod() {
        try {
            httpParser.parseHttpRequest(getInputStream("Get / HTTP/1.1\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(NOT_IMPLEMENTED, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithLongMethod() {
        try {
            httpParser.parseHttpRequest(getInputStream("VERY_LONG_METHOD / HTTP/1.1\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(NOT_IMPLEMENTED, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithBadRequest() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET A BAD REQUEST HTTP/1.1\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithEmptyRequestLine() {
        try {
            httpParser.parseHttpRequest(getInputStream("\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithOutLF() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET / HTTP/1.1\r"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidGetWithOutCR() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET / HTTP/1.1\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHTTPVersion() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET / HTTP/1.2\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(VERSION_NOT_SUPPORTED, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidLongHTTPVersion() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET / HTTP/1.0000000\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(VERSION_NOT_SUPPORTED, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidLongURI() {
        try {
            StringBuilder uri = new StringBuilder();
            for (int i = 0; i < 2049; i++)
                uri.append("a");
            httpParser.parseHttpRequest(getInputStream("GET " + uri.toString() + " HTTP/1.0\r\n"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(URI_TOO_LONG, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidWithoutCRLFForRequestLine() {
        try {
            httpParser.parseHttpRequest(getInputStream("GET / HTTP/1.0"));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderWithoutCO() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "Host localhost:8080\r\n" +
                            "\r\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderTooLong() {
        try {
            StringBuilder key = new StringBuilder();
            for (int i = 0; i < 4097; i++)
                key.append("a");
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            key.toString() + ": localhost:8080\r\n" +
                            "\r\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(HEADER_TOO_LARGE, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderWithoutEnding() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "host: localhost:8080\r\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderEndingWithoutCR() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "host: localhost:8080\r\n" +
                            "\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderEndingWithoutLF() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "host: localhost:8080\r\n" +
                            "\r"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderWithoutCR() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "host: localhost:8080\n" +
                            "\r\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }

    @Test
    public void parseHttpRequestTestInvalidHeaderWithoutLF() {
        try {
            HttpRequest request = httpParser.parseHttpRequest(getInputStream(
                    "GET / HTTP/1.1\r\n" +
                            "host: localhost:8080\r" +
                            "\r\n"
            ));
            fail();
        } catch (HttpParsingException e) {
            assertEquals(BAD_REQUEST, e.getErrorCode());
            LOGGER.debug("Test Passed", e);
        }
    }
}
