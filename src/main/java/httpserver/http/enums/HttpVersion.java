package httpserver.http.enums;

import httpserver.util.HttpParsingException;

import static httpserver.http.enums.HttpStatusCode.VERSION_NOT_SUPPORTED;

public enum HttpVersion {

    HTTP_0_9("HTTP/0.9"),
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0");

    public final String VERSION;
    public static final int MAX_LENGTH;

    static {
        int tempMaxLength = -1;
        for (HttpVersion httpVersion : HttpVersion.values()) {
            tempMaxLength = Math.max(tempMaxLength,httpVersion.getVersion().length());
        }
        MAX_LENGTH = tempMaxLength;
    }

    HttpVersion(String VERSION) {
        this.VERSION = VERSION;
    }

    public String getVersion() {
        return VERSION;
    }

    public static HttpVersion getVersion(String version) throws HttpParsingException {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if(httpVersion.getVersion().equals(version))
                return httpVersion;
        }
        throw new HttpParsingException(VERSION_NOT_SUPPORTED);
    }
}
