package httpserver.core;

import httpserver.http.enums.HttpStatusCode;
import httpserver.http.enums.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static httpserver.http.enums.HttpStatusCode.OK;

public class HttpResponse extends HttpMessage {

    private final HttpVersion httpVersion;
    private final Map<String, String> headers;
    private HttpStatusCode statusCode;
    private String message;

    HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.statusCode = OK;
        this.headers = new HashMap<>();
    }

    HttpResponse(HttpVersion httpVersion, HttpStatusCode statusCode, String message) {
        this.httpVersion = httpVersion == null ? HttpVersion.HTTP_1_0 : httpVersion;
        this.headers = new HashMap<>();
        this.statusCode = statusCode;
        this.message = message;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    byte[] getResponseBytes() {
        final String SP = " ";
        final String CRLF = "\n\r";
        final String CO = ":";

        StringBuilder response = new StringBuilder();
        response.append(httpVersion.getVersion());
        response.append(SP);
        response.append(statusCode.getSTATUS_CODE());
        response.append(SP);
        response.append(statusCode.getMESSAGE());
        response.append(CRLF);
        headers.forEach((k, v) -> {
            response.append(k);
            response.append(CO);
            response.append(SP);
            response.append(v);
            response.append(CRLF);
        });
        response.append(CRLF);
        if (getMessage() != null)
            response.append(getMessage());
        response.append(CRLF);
        response.append(CRLF);
        return new String(response).getBytes(StandardCharsets.US_ASCII);
    }
}
