package com.example.bot;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;


public class MiBot implements LongPollingSingleThreadUpdateConsumer {

    private static final String BOT_TOKEN = "7670377589:AAEG7xLhS1NfsRlZr5rsLC6pPzwYzEzXuMc"; // Reemplaza con tu token
    private static final String BOT_USERNAME = "test3ng_java_bot"; // Reemplaza con el nombre de tu bot
    private final TelegramClient telegramClient;
    private SilentSender silentSender;

    public MiBot() {
        this(BOT_TOKEN); // Llamar al constructor correcto con solo el botToken
    }

    public MiBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    public TelegramClient getTelegramClient(){
        return telegramClient;
    }

    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    // Setter para SilentSender
    public void setSilentSender(SilentSender silentSender) {
        this.silentSender = silentSender;
    }

    // Definir la habilidad "Hello World"
    public Ability saysHelloWorld() {
        return Ability.builder()
                .name("hello")
                .locality(Locality.valueOf("ALL"))
                .privacy(Privacy.valueOf("PUBLIC"))
                .info("Responde con 'Hello World!'")
                .input(0)
                .action(ctx -> silentSender.send("Hello World!", ctx.chatId()))
                .build();
    }

    // Definir la habilidad para crear tareas
    public Ability createTask() {
        return Ability.builder()
                .name("crear_tarea")
                .locality(Locality.valueOf("ALL"))
                .privacy(Privacy.valueOf("PUBLIC"))
                .info("Crea una nueva tarea")
                .input(4)
                .action(ctx -> {
                    String[] args = ctx.arguments();
                    if (args.length < 4) {
                        silentSender.send("Faltan argumentos. Uso: /crear_tarea <descripción> <horas> <fecha>", ctx.chatId());
                        return;
                    }
                    try {
                        int hours = Integer.parseInt(args[2]);
                        if (hours > 4) {
                            silentSender.send("Las horas estimadas exceden el límite de 4 horas", ctx.chatId());
                            return;
                        }
                        silentSender.send("Tarea creada exitosamente", ctx.chatId());
                    } catch (NumberFormatException e) {
                        silentSender.send("Las horas deben ser un número válido", ctx.chatId());
                    }
                })
                .build();
    }

    // Definir la habilidad para ver tareas completadas de un sprint
    public Ability viewCompletedSprintTasks() {
        return Ability.builder()
                .name("ver_tareas_sprint")
                .locality(Locality.valueOf("ALL"))
                .privacy(Privacy.valueOf("PUBLIC"))
                .info("Muestra las tareas completadas de un sprint")
                .input(1)
                .action(ctx -> {
                    String[] args = ctx.arguments();
                    if (args.length < 1) {
                        silentSender.send("Falta el ID del sprint", ctx.chatId());
                        return;
                    }
                    try {
                        int sprintId = Integer.parseInt(args[0]);
                        if (sprintId < 0) {
                            silentSender.send("Sprint no encontrado", ctx.chatId());
                            return;
                        }
                        silentSender.send("Tareas completadas del sprint " + sprintId, ctx.chatId());
                    } catch (NumberFormatException e) {
                        silentSender.send("El ID del sprint debe ser un número válido", ctx.chatId());
                    }
                })
                .build();
    }

    // Definir la habilidad para ver tareas completadas de un usuario en un sprint
    public Ability viewCompletedUserSprintTasks() {
        return Ability.builder()
                .name("ver_tareas_usuario_sprint")
                .locality(Locality.valueOf("ALL"))
                .privacy(Privacy.valueOf("PUBLIC"))
                .info("Muestra las tareas completadas de un usuario en un sprint")
                .input(2)
                .action(ctx -> {
                    String[] args = ctx.arguments();
                    if (args.length < 2) {
                        silentSender.send("Faltan argumentos. Uso: /ver_tareas_usuario_sprint <user_id> <sprint_id>", ctx.chatId());
                        return;
                    }
                    try {
                        int userId = Integer.parseInt(args[0]);
                        int sprintId = Integer.parseInt(args[1]);
                        silentSender.send("Tareas completadas del usuario " + userId + " en el sprint " + sprintId, ctx.chatId());
                    } catch (NumberFormatException e) {
                        silentSender.send("Los IDs deben ser números válidos", ctx.chatId());
                    }
                })
                .build();
    }

    @Override
    public void consume(Update update) {
        // Verificar si hay un mensaje de texto recibido
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            
            long chatId = update.getMessage().getChatId();
            
            // Crear el mensaje de respuesta
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(messageText)
                    .build();

            try {
                
                if (silentSender != null) {
                    // Usar SilentSender en pruebas para evitar comunicación real
                    silentSender.send(message.getText(), chatId);
                } else {
                    telegramClient.execute(message); // Enviar mensaje si no es un test
                }
                
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}

