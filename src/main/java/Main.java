import com.google.gson.Gson;
import okhttp3.*;
import spark.Spark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String SUPABASE_URL = "https://tmtzwnkcensdrqzwstau.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRtdHp3bmtjZW5zZHJxendzdGF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzgwNTEzMTgsImV4cCI6MjA1MzYyNzMxOH0.gp6A_E6Dn1bU04nA09P-EHRzVAk0GStHVpxsg9iSa1Y";
    private static final String TABLE_NAME = "users";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Spark.port(8080);

        // Убираем CORS ограничения
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
        });

        // Маршруты CRUD
        Spark.post("/users", (req, res) -> {
            res.type("application/json"); // Устанавливаем заголовок Content-Type
            return createUser(req.body());
        });
        Spark.get("/users", (req, res) -> {
            res.type("application/json");
            return getUsers();
        });
        Spark.put("/users/:id", (req, res) -> {
            res.type("application/json");
            return updateUser(req.params(":id"), req.body());
        });
        Spark.delete("/users/:id", (req, res) -> {
            res.type("application/json");
            return deleteUser(req.params(":id"));
        });
    }

    // Создание пользователя
    private static String createUser(String body) throws IOException {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + TABLE_NAME)
                .post(RequestBody.create(body, MediaType.parse("application/json")))
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Возвращаем JSON-ответ
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "User created!");
            return gson.toJson(responseMap);
        }
    }

    // Получение всех пользователей
    private static String getUsers() throws IOException {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + TABLE_NAME + "?select=*")
                .get()
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Возвращаем JSON-ответ напрямую от Supabase
            return response.body().string();
        }
    }

    // Обновление пользователя
    private static String updateUser(String id, String body) throws IOException {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + TABLE_NAME + "?id=eq." + id)
                .patch(RequestBody.create(body, MediaType.parse("application/json")))
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Возвращаем JSON-ответ
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "User updated!");
            return gson.toJson(responseMap);
        }
    }

    // Удаление пользователя
    private static String deleteUser(String id) throws IOException {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/" + TABLE_NAME + "?id=eq." + id)
                .delete()
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Возвращаем JSON-ответ
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "User deleted!");
            return gson.toJson(responseMap);
        }
    }
}