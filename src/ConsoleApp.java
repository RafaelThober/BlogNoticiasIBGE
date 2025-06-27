import java.util.*;

public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static String nickname;
    private static final IbgeClient client = new IbgeClient();
    private static final BlogService service = new BlogService();

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println(" Bem-vindo ao Gerenciador de Notícias IBGE");
        System.out.println("=======================================");
        System.out.print("Por favor, digite seu apelido: ");
        nickname = scanner.nextLine().trim();

        int option;
        do {
            printMenu();
            option = getIntInput("Selecione uma opção do menu: ");
            switch (option) {
                case 1 -> searchNews();
                case 2 -> showList("Favoritas", service.getList(nickname, ListName.FAVORITES), ListName.FAVORITES);
                case 3 -> showList("Lidas", service.getList(nickname, ListName.READ), ListName.READ);
                case 4 -> showList("Para ler depois", service.getList(nickname, ListName.TO_READ), ListName.TO_READ);
                case 5 -> sortListMenu();
                case 0 -> System.out.println("\nObrigado por utilizar o Gerenciador de Notícias IBGE. Até logo!");
                default -> System.out.println("\n[Erro] Opção inválida. Por favor, selecione uma opção existente no menu.");
            }
        } while (option != 0);
    }

    private static void printMenu() {
        System.out.println("\n---------------------------------------");
        System.out.println("MENU PRINCIPAL - Usuário: " + nickname);
        System.out.println("---------------------------------------");
        System.out.println("1) Buscar notícia por palavra-chave, título ou data");
        System.out.println("2) Ver lista de notícias favoritas");
        System.out.println("3) Ver lista de notícias lidas");
        System.out.println("4) Ver lista de notícias para ler depois");
        System.out.println("5) Ordenar suas listas");
        System.out.println("0) Sair do programa");
    }

    private static void searchNews() {
        System.out.println("\n---------------------------------------");
        System.out.println("BUSCA DE NOTÍCIAS");
        System.out.println("---------------------------------------");
        System.out.print("Digite palavra-chave, título ou data (AAAA-MM-DD): ");
        String term = scanner.nextLine().trim();

        if (term.isEmpty()) {
            System.out.println("[Aviso] Nenhum termo foi digitado. Retornando ao menu principal.");
            return;
        }

        List<Noticia> results = client.searchNews(term);
        if (results.isEmpty()) {
            System.out.println("[Aviso] Nenhuma notícia encontrada com o termo fornecido.");
            return;
        }

        System.out.println("\nForam encontradas as seguintes notícias:");
        for (int i = 0; i < results.size(); i++) {
            Noticia n = results.get(i);
            System.out.printf("%d) %s\n", i + 1, n.toStringCompact());
        }

        while (true) {
            int choice = getIntInput("Digite o número da notícia para ver detalhes (ou 0 para voltar ao menu): ");
            if (choice == 0) {
                System.out.println("Retornando ao menu principal.");
                return;
            } else if (choice > 0 && choice <= results.size()) {
                Noticia selected = results.get(choice - 1);
                System.out.println("\n=======================================");
                System.out.println("DETALHES DA NOTÍCIA SELECIONADA");
                System.out.println("=======================================");
                System.out.println(selected.toStringDetail());
                manageNews(selected);
                return;
            } else {
                System.out.println("[Erro] Opção inválida. Digite um número correspondente a uma das notícias listadas ou 0 para voltar.");
            }
        }
    }

    private static void manageNews(Noticia noticia) {
        while (true) {
            System.out.println("\nGerenciamento de Listas para a notícia: " + noticia.getTitulo());
            System.out.println("---------------------------------------");
            System.out.println("1) Adicionar/remover de Favoritas");
            System.out.println("2) Adicionar/remover de Lidas");
            System.out.println("3) Adicionar/remover de Para ler depois");
            System.out.println("0) Voltar ao menu anterior");
            System.out.print("Digite o(s) número(s) da(s) lista(s) separados por vírgula (exemplo: 1,3): ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Retornando ao menu anterior.");
                return;
            }

            boolean anyValid = false;
            String[] parts = input.split(",");
            for (String part : parts) {
                int opt;
                try {
                    opt = Integer.parseInt(part.trim());
                } catch (NumberFormatException e) {
                    System.out.println("[Erro] Valor inválido: " + part.trim());
                    continue;
                }

                ListName list = switch (opt) {
                    case 1 -> ListName.FAVORITES;
                    case 2 -> ListName.READ;
                    case 3 -> ListName.TO_READ;
                    default -> null;
                };

                if (list == null) {
                    System.out.println("[Erro] Opção inválida: " + part.trim());
                    continue;
                }

                anyValid = true;
                if (service.isInList(nickname, noticia, list)) {
                    service.removeFromList(nickname, noticia, list);
                    System.out.println("Removido de '" + list.getLabel() + "' com sucesso.");
                } else {
                    service.addToList(nickname, noticia, list);
                    System.out.println("Adicionado a '" + list.getLabel() + "' com sucesso.");
                }
            }
            if (anyValid) {
                return;
            }
        }
    }

    private static void showList(String title, Set<Noticia> noticias, ListName listName) {
        System.out.println("\n=======================================");
        System.out.println("LISTA DE NOTÍCIAS - " + title.toUpperCase());
        System.out.println("=======================================");

        if (noticias.isEmpty()) {
            System.out.println("[Aviso] Sua lista de '" + title + "' está vazia no momento.");
            return;
        }

        List<Noticia> noticiaList = new ArrayList<>(noticias);
        for (int i = 0; i < noticiaList.size(); i++) {
            Noticia noticia = noticiaList.get(i);
            System.out.printf("%d) %s\n", i + 1, noticia.getTitulo());
        }

        while (true) {
            int choice = getIntInput("Digite o número da notícia para remover da lista (ou 0 para voltar ao menu): ");
            if (choice == 0) {
                System.out.println("Retornando ao menu principal.");
                return;
            } else if (choice > 0 && choice <= noticiaList.size()) {
                Noticia selected = noticiaList.get(choice - 1);
                service.removeFromList(nickname, selected, listName);
                System.out.println("A notícia '" + selected.getTitulo() + "' foi removida da lista '" + title + "'.");

                Set<Noticia> updatedNoticias = service.getList(nickname, listName);
                if (updatedNoticias.isEmpty()) {
                    System.out.println("[Aviso] Sua lista de '" + title + "' agora está vazia.");
                    return;
                }
                noticiaList = new ArrayList<>(updatedNoticias);
                System.out.println("\nLista atualizada de '" + title + "':");
                for (int i = 0; i < noticiaList.size(); i++) {
                    Noticia noticia = noticiaList.get(i);
                    System.out.printf("%d) %s\n", i + 1, noticia.getTitulo());
                }
            } else {
                System.out.println("[Erro] Opção inválida. Digite o número de uma notícia da lista ou 0 para voltar.");
            }
        }
    }

    private static void sortListMenu() {
        System.out.println("\n---------------------------------------");
        System.out.println("ORDENAÇÃO DE LISTAS");
        System.out.println("---------------------------------------");
        System.out.println("Qual lista deseja ordenar?");
        System.out.println("1) Favoritas");
        System.out.println("2) Lidas");
        System.out.println("3) Para ler depois");
        int opt = getIntInput("Digite o número da lista desejada: ");

        Set<Noticia> noticias;
        ListName listName;

        switch (opt) {
            case 1 -> {
                listName = ListName.FAVORITES;
                noticias = service.getList(nickname, listName);
            }
            case 2 -> {
                listName = ListName.READ;
                noticias = service.getList(nickname, listName);
            }
            case 3 -> {
                listName = ListName.TO_READ;
                noticias = service.getList(nickname, listName);
            }
            default -> {
                System.out.println("[Erro] Opção inválida. Retornando ao menu principal.");
                return;
            }
        }

        if (noticias.isEmpty()) {
            System.out.println("[Aviso] A lista selecionada está vazia. Não é possível ordenar uma lista vazia.");
            return;
        }

        List<Noticia> noticiaList = new ArrayList<>(noticias);

        System.out.println("\nEscolha o critério de ordenação:");
        System.out.println("1) Título (ordem alfabética)");
        System.out.println("2) Data de publicação (mais recente primeiro)");
        System.out.println("3) Tipo/categoria da notícia");
        int sortOpt = getIntInput("Digite o número do critério desejado: ");

        switch (sortOpt) {
            case 1 -> service.sortByTitle(noticiaList);
            case 2 -> service.sortByDate(noticiaList);
            case 3 -> service.sortByType(noticiaList);
            default -> {
                System.out.println("[Erro] Critério inválido. Retornando ao menu principal.");
                return;
            }
        }

        System.out.println("\nResultado da lista '" + listName.getLabel() + "' ordenada:");

        for (Noticia noticia : noticiaList) {
            switch (sortOpt) {
                case 1 -> // Título
                    System.out.println("- " + noticia.getTitulo() + " (" + noticia.getTitulo() + ")");
                case 2 -> // Data
                    System.out.println("- " + noticia.getTitulo() + " (" + noticia.getDataPublicacaoPadronizada() + ")");
                case 3 -> // Tipo/categoria
                    System.out.println("- " + noticia.getTitulo() + " (" + (noticia.getTipo() != null ? noticia.getTipo() : "Sem categoria") + ")");
            }
        }
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
