public class Noticia {
    private String titulo;
    private String introducao;
    private String data_publicacao;
    private String link;
    private String tipo;

    public String getTitulo() {
        return titulo;
    }

    public String getIntroducao() {
        return introducao;
    }

    public String getDataPublicacao() {
        return data_publicacao;
    }

    public String getLink() {
        return link;
    }

    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return this.hashCode();
    }

    @Override
    public int hashCode() {
        return (titulo + data_publicacao).hashCode();
    }

    @Override
    public String toString() {
        return """
            📰 %s
            📅 Publicado em: %s
            🔗 Link: %s
            🏷️ Tipo: %s
            ➤ %s
            """.formatted(titulo, data_publicacao, link, tipo, introducao);
    }

    public String toStringDetail() {
        return """
            ----------------------------
            📰 Título: %s
            🗓️ Data: %s
            📄 Tipo: %s
            🔗 Link: %s
            ----------------------------""".formatted(titulo, data_publicacao, tipo, link);
    }
}
