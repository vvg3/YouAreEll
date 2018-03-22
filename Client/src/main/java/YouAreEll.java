import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class YouAreEll {

    YouAreEll() {
    }

    public static void main(String[] args) {
        YouAreEll urlhandler = new YouAreEll();
        System.out.println(urlhandler.MakeURLCall("/ids", "GET", ""));
        System.out.println(urlhandler.MakeURLCall("/messages", "GET", ""));
    }

    public String get_ids() {
        return MakeURLCall("/ids", "GET", "");
    }

    public String get_messages() {
        return MakeURLCall("/messages", "GET", "");
    }

    public String MakeURLCall(String mainurl, String method, String jpayload) {

        String url = "http://zipcode.rocks:8085" + mainurl;

        switch (method) {
            case "GET":
                return getMethod(url);
            case "POST":
                return postMethod(url, jpayload);
            default:
                return null;
        }
    }

    private String getMethod(String url) {
        try {
            return Request.Get(url).execute().returnContent().toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private String postMethod(String url, String jpayload) {
        try {
            return Request.Post(url).bodyString(jpayload, ContentType.APPLICATION_JSON).execute().returnContent().toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
