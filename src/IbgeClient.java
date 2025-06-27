import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class IbgeClient {

    private static final String BASE_URL = "https://servicodados.ibge.gov.br/api/v3/noticias";
    private final Gson gson = new Gson();

    public List<Noticia> buscarNoticias(String termo) {
        try {
            String query = URLEncoder.encode(termo, StandardCharsets.UTF_8);
            String urlStr = BASE_URL + "/?busca=" + query + "&qtd=30";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                ApiResponse response = gson.fromJson(reader, ApiResponse.class);
                return response.items != null ? response.items : Collections.emptyList();
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar notícias: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Noticia getNoticiaById(int id) {
        List<Noticia> recentes = buscarNoticias(" ");
        for (Noticia noticia : recentes) {
            if (noticia.getId() == id) {
                return noticia;
            }
        }
        return null;
    }

    private static class ApiResponse {
        List<Noticia> items;
    }
}
