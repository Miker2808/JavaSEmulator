package ui.netcode;

import com.google.gson.Gson;
import dto.DashboardDTO;
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
                .addFormDataPart("file",file.getAbsolutePath(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        String url = String.format("%s/upload?user=%s", URL, user);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }

    public static Response chargeCredits(String username, int credits) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        String url = String.format("%s/credits?user=%s&charge=%s", URL, username, credits);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        return client.newCall(request).execute();
    }

    public static DashboardDTO getDashboardDTO(String username) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s/dashboard?user=%s", URL, username);
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, DashboardDTO.class);
        }

        else{
            throw new IOException(response.body().string());
        }
    }



}
