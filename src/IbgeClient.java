import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class IbgeClient {

    private static final String BASE_URL = "https://servicodados.ibge.gov.br/api/v3/noticias";
    private final Gson gson = new Gson();

    public List<Noticia> searchNews(String term) {
        try {
            String encoded = java.net.URLEncoder.encode(term, "UTF-8");
            URL url = new URL(BASE_URL + "?busca=" + encoded);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                IbgeResponse response = gson.fromJson(reader, IbgeResponse.class);
                return response != null && response.items != null ? response.items : List.of();
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar notícias: " + e.getMessage());
        }
        return List.of();
    }

    // Wrapper para a resposta da API
    private static class IbgeResponse {
        List<Noticia> items;
    }
}
