package httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);


    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
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
//            printRequest(inputStream);


            outputStream = socket.getOutputStream();

            String html = "<html>\n" +
                    "    <head>\n" +
                    "        <body>Simple Http Server</body>\n" +
                    "    </head>\n" +
                    "</html>";

            final String CRLF = "\n\r";
            String response =
                    "HTTP/1.1 200 OK" + CRLF +                                          // Status Line
                            "Content-Length: " + html.getBytes().length + CRLF +        // Headers
                            CRLF +                                                      // To show headers are finished
                            html +
                            CRLF + CRLF;

            outputStream.write(response.getBytes());
            LOGGER.info("Response Sent");

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
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
