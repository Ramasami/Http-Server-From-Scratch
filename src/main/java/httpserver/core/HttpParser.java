package httpserver.core;

import httpserver.http.enums.HttpMethod;
import httpserver.http.enums.HttpVersion;
import httpserver.util.HttpParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static httpserver.http.enums.HttpStatusCode.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class HttpParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20;
    private static final int CR = 0x0D;
    private static final int LF = 0x0A;
    private static final int CO = 0x3A;


    public static HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, US_ASCII);

        HttpRequest request = new HttpRequest();
        try {
            parseRequestLine(reader, request);
            parseHeaders(reader, request);
            request.setReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    private static void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        int _byte = 0;
        boolean methodParsed = false;
        boolean requestTargetParsed = false;
        StringBuilder processingDataBuffer = new StringBuilder();
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF && methodParsed && requestTargetParsed) {
                    LOGGER.debug("version to Process: {}", processingDataBuffer.toString());
                    request.setHttpVersion(HttpVersion.getVersion(processingDataBuffer.toString()));
                    return;
                } else {
                    throw new HttpParsingException(BAD_REQUEST);
                }
            } else if (_byte == LF) {
                throw new HttpParsingException(BAD_REQUEST);
            } else if (_byte == SP) {
                if (!methodParsed) {
                    LOGGER.debug("Method to Process: {}", processingDataBuffer.toString());
                    request.setMethod(processingDataBuffer.toString());
                    methodParsed = true;
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Target to Process: {}", processingDataBuffer.toString());
                    request.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed = true;
                } else {
                    throw new HttpParsingException(BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());
            } else {
                processingDataBuffer.append((char) _byte);
                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(NOT_IMPLEMENTED);
                    }
                } else if (!requestTargetParsed) {
                    if (processingDataBuffer.length() > 2048)
                        throw new HttpParsingException(URI_TOO_LONG);
                } else if (processingDataBuffer.length() > HttpVersion.MAX_LENGTH) {
                    throw new HttpParsingException(VERSION_NOT_SUPPORTED);
                }
            }
        }
        throw new HttpParsingException(BAD_REQUEST);
    }

    private static void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
        int _byte = 0;
        boolean parsingHeader = false;
        boolean parsedKey = false;
        boolean exists = false;
        String key = null;
        String value;
        StringBuilder processingDataBuffer = new StringBuilder();
        Map<String, String> headers = new HashMap<>();
        while ((_byte = reader.read()) > 0) {
            exists = true;
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte == LF) {
                    if (!parsingHeader) {
                        request.setHeaders(headers);
                        return;
                    } else {
                        if (!parsedKey)
                            throw new HttpParsingException(BAD_REQUEST);
                        parsingHeader = false;
                        parsedKey = false;
                        LOGGER.debug("Header to Process: {}: {}", key, processingDataBuffer.toString());
                        headers.put(key, processingDataBuffer.toString());
                        processingDataBuffer.delete(0, processingDataBuffer.length());
                    }
                } else {
                    throw new HttpParsingException(BAD_REQUEST);
                }
            } else if (_byte == CO && !parsedKey) {
                _byte = reader.read();
                if (_byte == SP) {
                    parsedKey = true;
                    key = processingDataBuffer.toString();
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                } else {
                    throw new HttpParsingException(BAD_REQUEST);
                }
            } else {
                parsingHeader = true;
                processingDataBuffer.append((char) _byte);
                if (processingDataBuffer.length() > 4096)
                    throw new HttpParsingException(HEADER_TOO_LARGE);
            }
        }
        if(exists)
            throw new HttpParsingException(BAD_REQUEST);
        request.setHeaders(Collections.emptyMap());
    }

    public static void parseBody(InputStreamReader reader, HttpRequest request) throws IOException {
        int _byte;
        StringBuilder processingDataBuffer = new StringBuilder();
        while ((_byte = reader.read()) > 0) {
            processingDataBuffer.append((char) _byte);
        }
        request.setMessage(processingDataBuffer.toString());
    }
}
