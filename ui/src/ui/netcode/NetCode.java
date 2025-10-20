package ui.netcode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dto.*;
import javafx.application.Platform;
import okhttp3.*;
import ui.NetworkException;
import ui.elements.InfoMessage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetCode {
    private static final String URL = "http://localhost:8080/semulator-server";
    private static final Gson gson = new Gson();

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

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        String url = String.format("%s/upload?user=%s", URL, user);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }

    public static void chargeCredits(String username, int credits) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        String url = String.format("%s/credits?user=%s&charge=%s", URL, username, credits);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()) {
            response.close();
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }

    }

    public static DashboardDTO getDashboardDTO(String username, String historyUsername) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url;

        if(historyUsername != null){
            url = String.format("%s/dashboard?user=%s&history=%s", URL, username, historyUsername);
        }
        else{
            url = String.format("%s/dashboard?user=%s", URL, username);
        }

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
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    public static ExecutionDTO getExecutionDTO(String username) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s/execution?user=%s", URL, username);
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            String json = response.body().string();
            return gson.fromJson(json, ExecutionDTO.class);
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    public static SProgramDTO getSProgramDTO(String username, Integer degree) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/execution/get-program?user=%s&degree=%d", URL, username, degree);

        Map<String, String> data = new HashMap<>();
        String json = gson.toJson(data);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            String jsonString = response.body().string();
            return gson.fromJson(jsonString, SProgramDTO.class);
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    public static Response selectProgram(String username, String program, String type) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/dashboard?user=%s", URL, username);

        Map<String, String> data = new HashMap<>();
        data.put("program", program);
        data.put("type", type); // "FUNCTION" or "PROGRAM"
        String json = gson.toJson(data);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }

    public static List<SInstructionDTO> getExpansionHistoryDTO(String username, Integer degree, Integer line) throws IOException{
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/execution/get-expansion-history?user=%s&degree=%d&line=%d", URL, username, degree, line);

        Map<String, String> data = new HashMap<>();
        String json = gson.toJson(data);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            String jsonString = response.body().string();
            Type type = new TypeToken<List<SInstructionDTO>>() {}.getType();
            return gson.fromJson(jsonString, type);
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    public static Response sendExecutionCommand(String username, ExecutionRequestDTO dto) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/execution/execute?user=%s", URL, username);

        Type type = new TypeToken<ExecutionRequestDTO>() {}.getType();
        String json = gson.toJson(dto, type);
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .build();

        return client.newCall(request).execute();
    }


    public static LinkedHashMap<String, Integer> getHistoryStatusVariables(String username, String historyUser, Integer index) throws IOException
    {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = String.format("%s/dashboard/history-status?user=%s&history=%s&index=%d", URL, username, historyUser, index);
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            String jsonString = response.body().string();
            Type type = new TypeToken<LinkedHashMap<String, Integer>>() {}.getType();
            return gson.fromJson(jsonString, type);
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    public static boolean sendMessageToServer(String username, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("message", message);

        String url = URL + "/chat";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        RequestBody body = RequestBody.create(
                gson.toJson(json), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            response.close();
            return true;
        }
        else{
            throw new NetworkException(response.code(), response.body().string());
        }
    }

    // Method to get chat from server as a string (single-call, blocking)
    public static String getChatFromServer() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(URL + "/chat")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                InfoMessage.showInfoMessage("Failure", response.body().string());
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            InfoMessage.showInfoMessage("Failure", e.getMessage());
            return null;
        }
    }



}
