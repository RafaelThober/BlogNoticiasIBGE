import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonStore {
    private static final String FILE_NAME = "blog_data.json";
    private final Gson gson = new Gson();
    private Map<String, Map<String, Set<Integer>>> data;

    public JsonStore() {
        this.data = loadFromFile();
    }

    private Map<String, Map<String, Set<Integer>>> loadFromFile() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type type = new TypeToken<Map<String, Map<String, Set<Integer>>>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, Set<Integer>>> carregar() {
        return this.data;
    }

    public void salvar(Map<String, Map<String, Set<Integer>>> novaData) {
        this.data = novaData;
        saveToFile();
    }
}
