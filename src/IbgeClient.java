import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class IbgeClient {

    private static final String BASE_URL = "https://servicodados.ibge.gov.br/api/v3/noticias";
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    public List<Noticia> searchNews(String term) {
        try {
            String encoded = java.net.URLEncoder.encode(term, "UTF-8");
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?busca=" + encoded))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            IbgeResponse ibgeResponse = gson.fromJson(response.body(), IbgeResponse.class);
            return ibgeResponse != null && ibgeResponse.items != null ? ibgeResponse.items : List.of();
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar notícias: " + e.getMessage());
        }
        return List.of();
    }

    private static class IbgeResponse {
        List<Noticia> items;
    }
}
