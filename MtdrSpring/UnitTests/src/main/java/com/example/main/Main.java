import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot("7075157328:AAHgeGeAA7oSzSrntasoQLMhDQht25SOwEg", new MiBot("7075157328:AAHgeGeAA7oSzSrntasoQLMhDQht25SOwEg"));
            System.out.println("MiBot successfully started!");
            Thread.currentThread().join();
            System.out.println("Bot iniciado correctamente.");
        } catch (Exception  e) {
            e.printStackTrace();
        }

    }
}