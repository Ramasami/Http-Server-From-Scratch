package httpserver.http.enums;

public enum HttpMethod {
    GET, HEAD;

    public static final int MAX_LENGTH;

    static {
        int tempMaxLength = -1;
        for (HttpMethod httpMethod :
                HttpMethod.values()) {
            tempMaxLength = Math.max(tempMaxLength, httpMethod.name().length());
        }
        MAX_LENGTH = tempMaxLength;
    }
}
