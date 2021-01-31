package httpserver.core;

import httpserver.http.enums.HttpVersion;
import httpserver.util.HttpParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.function.BiConsumer;

public class HttpConnectionWorkerThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);


    private final Socket socket;
    private final BiConsumer<HttpRequest, HttpResponse> process;

    public HttpConnectionWorkerThread(Socket socket, BiConsumer<HttpRequest, HttpResponse> process) {
        this.socket = socket;
        this.process = process;
    }

    private void printRequest(InputStream inputStream) throws IOException {
        byte[] input = new byte[inputStream.available()];
        inputStream.read(input);
        LOGGER.info("request: " + new String(input));
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            HttpResponse response;
            HttpRequest request = null;
            try {
                request = HttpParser.parseHttpRequest(inputStream);
                response = new HttpResponse(request.getHttpVersion());
                process.accept(request, response);
            } catch (HttpParsingException e) {
                response = new HttpResponse(HttpVersion.HTTP_1_0, e.getErrorCode(), e.getErrorCode().MESSAGE);
            }
            outputStream = socket.getOutputStream();
            outputStream.write(response.getResponseBytes());
            LOGGER.info("Response Sent");

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            LOGGER.error("Problem with communication", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
