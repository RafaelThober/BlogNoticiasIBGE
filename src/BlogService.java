import java.util.*;

public class BlogService {
    private final Map<String, Map<String, Set<Integer>>> dadosUsuarios;
    private final JsonStore store;

    public BlogService() {
        this.store = new JsonStore();
        this.dadosUsuarios = store.carregar();
    }

    public void adicionar(String nickname, int noticiaId, ListName lista) {
        Map<String, Set<Integer>> dadosUsuario = dadosUsuarios.computeIfAbsent(nickname, k -> new HashMap<>());
        Set<Integer> listaNoticias = dadosUsuario.computeIfAbsent(lista.name(), k -> new HashSet<>());
        listaNoticias.add(noticiaId);
        store.salvar(dadosUsuarios);
    }

    public void remover(String nickname, int noticiaId, ListName lista) {
        Map<String, Set<Integer>> dadosUsuario = dadosUsuarios.get(nickname);
        if (dadosUsuario != null) {
            Set<Integer> listaNoticias = dadosUsuario.get(lista.name());
            if (listaNoticias != null) {
                listaNoticias.remove(noticiaId);
                store.salvar(dadosUsuarios);
            }
        }
    }

    public boolean contem(String nickname, int noticiaId, ListName lista) {
        Map<String, Set<Integer>> dadosUsuario = dadosUsuarios.get(nickname);
        if (dadosUsuario == null) return false;
        Set<Integer> listaNoticias = dadosUsuario.get(lista.name());
        return listaNoticias != null && listaNoticias.contains(noticiaId);
    }

    public Set<Integer> getLista(String nickname, ListName lista) {
        Map<String, Set<Integer>> dadosUsuario = dadosUsuarios.get(nickname);
        if (dadosUsuario == null) return Collections.emptySet();
        return dadosUsuario.getOrDefault(lista.name(), Collections.emptySet());
    }
}
