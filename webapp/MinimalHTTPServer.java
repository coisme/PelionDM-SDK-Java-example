/* Referenced: 
 * http://www.java2s.com/Code/Java/Network-Protocol/MinimalHTTPServerbyusingcomsunnethttpserverHttpServer.htm
 */
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.arm.mbed.cloud.sdk.common.MbedCloudException;
import com.arm.mbed.cloud.sdk.devices.model.DeviceListDao;
import com.arm.mbed.cloud.sdk.devices.model.DeviceListOptions;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MinimalHTTPServer {
  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/devicelist", new Handler());
    server.start();
  }
}

class Handler implements HttpHandler {
  public void handle(HttpExchange xchg) throws IOException {
    StringBuffer response = new StringBuffer();

    response.append("<html>\n<body>\n<h1>Device List</h1>\n<table>");

    try (DeviceListDao dao = new DeviceListDao()) {

      // Listing the first 10 devices on your Pelion Device Management account
      dao.list((new DeviceListOptions()).maxResults(10))
        .forEach(device -> response.append("<tr><td>" + device.getEndpointName() 
          + "</td><td>" + device.getName() + "</td></tr>\n"));
    } catch (MbedCloudException | IOException exception) {
      exception.printStackTrace();
    }

    response.append("</table></body>\n</html>");

    xchg.sendResponseHeaders(200, response.length());
    OutputStream os = xchg.getResponseBody();
    os.write(response.toString().getBytes());
    os.close();
  }
}
