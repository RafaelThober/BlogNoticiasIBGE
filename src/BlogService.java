import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class BlogService {

    private static final String LIBRARY_FILE = "library.json";
    private final Map<String, UserLibrary> userLibraries = new HashMap<>();

    public BlogService() {
        loadLibrary();
    }

    private static class UserLibrary {
        Set<Noticia> FAVORITES = new HashSet<>();
        Set<Noticia> READ = new HashSet<>();
        Set<Noticia> TO_READ = new HashSet<>();
    }

    private UserLibrary getUserLibrary(String nickname) {
        return userLibraries.computeIfAbsent(nickname, k -> new UserLibrary());
    }

    public Set<Noticia> getList(String nickname, ListName listName) {
        UserLibrary ul = getUserLibrary(nickname);
        return switch (listName) {
            case FAVORITES -> ul.FAVORITES;
            case READ -> ul.READ;
            case TO_READ -> ul.TO_READ;
        };
    }

    public void addToList(String nickname, Noticia noticia, ListName listName) {
        getList(nickname, listName).add(noticia);
        saveLibrary();
    }

    public void removeFromList(String nickname, Noticia noticia, ListName listName) {
        getList(nickname, listName).remove(noticia);
        saveLibrary();
    }

    public boolean isInList(String nickname, Noticia noticia, ListName listName) {
        return getList(nickname, listName).contains(noticia);
    }

    public List<Noticia> sortByTitle(List<Noticia> list) {
        list.sort(Comparator.comparing(Noticia::getTitulo, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public List<Noticia> sortByDate(List<Noticia> list) {
        list.sort(Comparator.comparing(
            Noticia::getDataPublicacaoAsDateTime,
            Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());
        return list;
    }

    public List<Noticia> sortByType(List<Noticia> list) {
        list.sort(Comparator.comparing(Noticia::getTipo, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return list;
    }

    private void saveLibrary() {
        try (Writer writer = new FileWriter(LIBRARY_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(userLibraries, writer);
        } catch (IOException e) {
            System.out.println("[Erro] Não foi possível salvar library.json: " + e.getMessage());
        }
    }

    private void loadLibrary() {
        File file = new File(LIBRARY_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(LIBRARY_FILE)) {
            Gson gson = new Gson();
            Map<String, UserLibrary> loaded = gson.fromJson(reader, new TypeToken<Map<String, UserLibrary>>(){}.getType());
            if (loaded != null) {
                userLibraries.putAll(loaded);
            }
        } catch (IOException e) {
            System.out.println("[Erro] Não foi possível carregar library.json: " + e.getMessage());
        }
    }
}
