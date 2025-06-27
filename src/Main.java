import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digite seu apelido: ");
            String nickname = scanner.nextLine().trim();

            ConsoleApp app = new ConsoleApp(nickname);
            app.run();
        }
    }
}
