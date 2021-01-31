package httpserver.core;

import httpserver.http.enums.HttpMethod;
import httpserver.http.enums.HttpVersion;
import httpserver.util.HttpParsingException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

import static httpserver.http.enums.HttpStatusCode.NOT_IMPLEMENTED;

public class HttpRequest extends HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private HttpVersion httpVersion;
    private Map<String, String> headers;
    private String message;
    private InputStreamReader reader;

    HttpRequest() {
    }

    public HttpMethod getMethod() {
        return method;
    }

    void setMethod(String method) throws HttpParsingException {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.toString().equals(method)) {
                this.method = HttpMethod.valueOf(method);
                return;
            }
        }
        throw new HttpParsingException(NOT_IMPLEMENTED);
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    void setRequestTarget(String requestTarget) {
        this.requestTarget = requestTarget;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    void setHeaders(Map<String, String> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public String getMessage() throws IOException {
        if(message == null)
            HttpParser.parseBody(reader,this);
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    InputStreamReader getReader() {
        return reader;
    }

    void setReader(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", requestTarget='" + requestTarget + '\'' +
                ", httpVersion=" + httpVersion +
                ", headers=" + headers +
                ", message='" + message + '\'' +
                '}';
    }
}
