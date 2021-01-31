package httpserver.http.enums;

public enum HttpStatusCode {

    /* --- CLIENT ERRORS --- */
    BAD_REQUEST(400,"Bad Request"),
    METHOD_NOT_ALLOWED(401,"Method Not Allowed"),
    URI_TOO_LONG(400,"URI Too Long"),
    HEADER_TOO_LARGE(431,"Header Too Large"),
    /* --- SERVER ERRORS --- */
    INTERNAL_SERVER_ERROR(500,"Internal Server Error"),
    NOT_IMPLEMENTED(501,"Not Implemented"),
    VERSION_NOT_SUPPORTED(505,"Version Not Supported"),

    /* --- Success --- */
    OK(200,"OK");

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }

    public int getSTATUS_CODE() {
        return STATUS_CODE;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }
}
