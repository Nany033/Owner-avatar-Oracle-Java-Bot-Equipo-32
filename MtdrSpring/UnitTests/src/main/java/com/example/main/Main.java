import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot("7670377589:AAEG7xLhS1NfsRlZr5rsLC6pPzwYzEzXuMc", new MiBot("7670377589:AAEG7xLhS1NfsRlZr5rsLC6pPzwYzEzXuMc"));
            System.out.println("MiBot successfully started!");
            Thread.currentThread().join();
            System.out.println("Bot iniciado correctamente.");
        } catch (Exception  e) {
            e.printStackTrace();
        }

    }
}