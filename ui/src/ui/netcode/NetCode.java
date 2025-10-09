package ui.netcode;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class NetCode {
    private static final String URL = "http://localhost:8080/semulator-server";

    public static Response login(String username) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        String url = String.format("%s/login?user=%s", URL, username);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }

    public static Response uploadFile(String user, File file) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file","/D:/test folder/xmls/minus.xml",
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        String url = String.format("%s/upload?user=%s", URL, user);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }

}
