package cs3773.com.eventplanner.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Rodney on 4/4/2015.
 * <p/>
 * This class will be responsible for communication with the server.
 */
public class ServerRequest {

    // the link to send the request to
    private ServerLink link;

    // the response from the server (will be null if no request has been sent)
    private String response;

    // key and value pairs to be sent to the server
    private HashMap<String, String> map;

    // the last error message from the server
    private static String lastErrorMessage;

    /**
     * @param link the link containing the URL to send the request to
     */
    public ServerRequest(ServerLink link) {
        this.link = link;
        map = new HashMap<>();
    }

    /**
     * @param key   variable name in the server
     * @param value data mapped to that variable (key)
     * @return this instance for chaining
     */
    public ServerRequest put(String key, String value) {
        map.put(key, value);
        return this;
    }

    /**
     * send the request to the server
     *
     * @return this instance for chaining
     */
    public ServerRequest send() throws ServerRequestException {
        // get iterator from entry set so we can traverse and fill our request data
        Iterator it = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder(); // string builder to hold request data
        try {
            boolean firstIteration = true; // fence post the '&' character
            while (it.hasNext()) {
                if (firstIteration) firstIteration = false;
                else sb.append('&'); // fence post

                Map.Entry pair = (Map.Entry) it.next();
                sb.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
            }

            URL url = new URL(link.url);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sb.toString());

            wr.flush();
            wr.close();

            readResponse(conn);

        } catch (UnsupportedEncodingException uee) {
            // should never happen since UTF-8 is almost guaranteed
            lastErrorMessage = "ServerRequest.send(): UnsupportedEncodingException";
            throw new ServerRequestException(lastErrorMessage);
        } catch (MalformedURLException mue) {
            lastErrorMessage = "ServerRequest.send(): MalformedURLException";
            throw new ServerRequestException(lastErrorMessage);
        } catch (IOException ioe) {
            lastErrorMessage = "ServerRequest.send(): IOException (error communicating with the server)";
            throw new ServerRequestException(lastErrorMessage);
        }

        return this;
    }

    private void readResponse(URLConnection conn) throws ServerRequestException {
        // Read server response
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            response = sb.toString();
        } catch (IOException ioe) {
            lastErrorMessage = "ServerRequest.readResponse(): IOException (An error occurred while reading from the server)";
            throw new ServerRequestException(lastErrorMessage);
        }
    }

    /**
     * @return response from the server. Will return null if no request has been sent.
     */
    public String getResponse() {
        return response;
    }

    public void reset() {
        response = null;
        map.clear();
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }


}
