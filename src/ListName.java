public enum ListName {
    FAVORITES("Favoritas"),
    READ("Lidas"),
    TO_READ("Para ler depois");

    private final String label;

    ListName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
