import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Noticia {
    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("introducao")
    private String introducao;

    @SerializedName("data_publicacao")
    private String dataPublicacao;

    @SerializedName("link")
    private String link;

    @SerializedName("tipo")
    private String tipo;

    private String fonte = "IBGE";

    // Métodos de utilidade para data
    public LocalDateTime getDataPublicacaoAsDateTime() {
        if (dataPublicacao == null) return null;
        // Tenta parsear nos dois formatos
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return LocalDateTime.parse(dataPublicacao, formatter);
        } catch (DateTimeParseException ex1) {
            try {
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dataPublicacao, formatter2);
                return localDate.atStartOfDay();
            } catch (DateTimeParseException ex2) {
                return null;
            }
        }
    }

    public String getDataPublicacaoPadronizada() {
        LocalDateTime dt = getDataPublicacaoAsDateTime();
        if (dt != null)
            return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return dataPublicacao != null ? dataPublicacao : "Sem data";
    }

    // Getters padrão
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getIntroducao() { return introducao; }
    public String getDataPublicacao() { return dataPublicacao; }
    public String getLink() { return link; }
    public String getTipo() { return tipo; }
    public String getFonte() { return fonte; }

    public String toStringCompact() {
        return titulo + " (" + getDataPublicacaoPadronizada() + ")";
    }

    public String toStringDetail() {
        return "\n📰 Título: " + titulo +
               "\nIntrodução: " + introducao +
               "\nData de publicação: " + getDataPublicacaoPadronizada() +
               "\nTipo: " + (tipo != null ? tipo : "Sem categoria") +
               "\nFonte: " + fonte +
               "\nLink: " + link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Noticia noticia)) return false;
        return id == noticia.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
