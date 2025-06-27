import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private final String nickname;
    private final IbgeClient client = new IbgeClient();
    private final BlogService service = new BlogService();

    public ConsoleApp(String nickname) {
        this.nickname = nickname;
    }

    public void run() {
        int option;
        do {
            printMenu();
            option = getIntInput("» ");
            switch (option) {
                case 1 -> searchNews();
                case 2 -> showList("Favoritas", ListName.FAVORITES);
                case 3 -> showList("Lidas", ListName.READ);
                case 4 -> showList("Para ler depois", ListName.TO_READ);
                case 5 -> sortListMenu();
                case 0 -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida.");
            }
        } while (option != 0);
    }

    private void printMenu() {
        System.out.println("\n🗞️  Blog de Notícias IBGE");
        System.out.println("1) Buscar notícia");
        System.out.println("2) Ver lista de favoritas");
        System.out.println("3) Ver lista de lidas");
        System.out.println("4) Ver lista para ler depois");
        System.out.println("5) Ordenar listas");
        System.out.println("0) Sair");
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void searchNews() {
        System.out.print("Digite uma palavra-chave, título ou data (AAAA-MM-DD): ");
        String termo = scanner.nextLine().trim();
        var resultados = client.buscarNoticias(termo);

        if (resultados.isEmpty()) {
            System.out.println("Nenhuma notícia encontrada.");
            return;
        }

        for (int i = 0; i < resultados.size(); i++) {
            System.out.println((i + 1) + ") " + resultados.get(i).getTitulo());
        }

        int escolha = getIntInput("Escolha um número para ver detalhes (0 para voltar): ");
        if (escolha > 0 && escolha <= resultados.size()) {
            var noticia = resultados.get(escolha - 1);
            System.out.println(noticia.toString());
            manageNews(noticia);
        }
    }

    private void manageNews(Noticia noticia) {
        System.out.println("\nDeseja adicionar ou remover de alguma lista?");
        System.out.println("1) Favoritos");
        System.out.println("2) Lidas");
        System.out.println("3) Para ler depois");
        System.out.println("0) Voltar");

        System.out.print("Digite os números das listas separados por vírgula (ex: 1,3): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) return;

        String[] parts = input.split(",");
        for (String part : parts) {
            int opt;
            try {
                opt = Integer.parseInt(part.trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: " + part);
                continue;
            }

            ListName listName = switch (opt) {
                case 1 -> ListName.FAVORITES;
                case 2 -> ListName.READ;
                case 3 -> ListName.TO_READ;
                default -> null;
            };

            if (listName == null) {
                System.out.println("Opção inválida: " + part);
                continue;
            }

            boolean exists = service.contem(nickname, noticia.getId(), listName);
            if (exists) {
                service.remover(nickname, noticia.getId(), listName);
                System.out.println("Removido da lista " + listName.getLabel());
            } else {
                service.adicionar(nickname, noticia.getId(), listName);
                System.out.println("Adicionado à lista " + listName.getLabel());
            }
        }
    }

    private void showList(String titulo, ListName tipo) {
        var ids = service.getLista(nickname, tipo);
        if (ids.isEmpty()) {
            System.out.println("\n📂 Lista " + titulo + ": (vazia)");
            return;
        }

        System.out.println("\n📂 Lista " + titulo + ":");
        for (int id : ids) {
            var noticia = client.getNoticiaById(id);
            if (noticia != null) {
                System.out.println("• " + noticia.getTitulo());
            }
        }
    }

    private void sortListMenu() {
        System.out.println("\n📊 Escolha a lista que deseja ordenar:");
        System.out.println("1) Favoritas");
        System.out.println("2) Lidas");
        System.out.println("3) Para ler depois");
        int listaOpt = getIntInput("» ");

        ListName listName = switch (listaOpt) {
            case 1 -> ListName.FAVORITES;
            case 2 -> ListName.READ;
            case 3 -> ListName.TO_READ;
            default -> null;
        };

        if (listName == null) {
            System.out.println("❌ Lista inválida.");
            return;
        }

        System.out.println("\n📐 Escolha o critério de ordenação:");
        for (int i = 0; i < SortOption.values().length; i++) {
            System.out.printf("%d) %s%n", i + 1, SortOption.values()[i].getLabel());
        }

        int sortOpt = getIntInput("» ");
        if (sortOpt < 1 || sortOpt > SortOption.values().length) {
            System.out.println("❌ Opção de ordenação inválida.");
            return;
        }

        SortOption sortOption = SortOption.values()[sortOpt - 1];
        var ids = service.getLista(nickname, listName);
        var noticias = ids.stream()
                .map(client::getNoticiaById)
                .filter(n -> n != null)
                .toList();

        var sorted = noticias.stream()
                .sorted((n1, n2) -> switch (sortOption) {
                    case TITLE -> n1.getTitulo().compareToIgnoreCase(n2.getTitulo());
                    case DATE -> n2.getDataPublicacao().compareTo(n1.getDataPublicacao());
                    case TYPE -> n1.getTipo().compareToIgnoreCase(n2.getTipo());
                })
                .toList();

        System.out.println("\n📂 Lista " + listName.getLabel() + " ordenada por " + sortOption.getLabel() + ":");
        for (var noticia : sorted) {
            System.out.println("• " + noticia.getTitulo());
        }
    }
}
