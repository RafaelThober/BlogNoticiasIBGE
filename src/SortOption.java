public enum SortOption {
    TITLE("Título (A-Z)"),
    DATE("Data de Publicação"),
    TYPE("Tipo / Categoria");

    private final String label;

    SortOption(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
