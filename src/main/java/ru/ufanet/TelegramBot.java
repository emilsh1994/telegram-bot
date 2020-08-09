package ru.ufanet;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {

    //Мапа для сохранения пользователя и его листа
    public static Map<Long, List<String>> map = new HashMap<>();

    //Имя бота
    public final String botUsername = "emilNotesBot";

    //Токен выданный BotFather
    public final String botToken = "1258217203:AAGK2sImKKCVSs9oWQp8ivoxBvb0LM7J1Hc";

    //************************************************************//
    private static final String greeting = "Hello!\n" +
            "This is a bot that helps you to manage your notes.\n" +
            "Type: '/help' to see all of the available commands.";

    private static final String help = "List of all the available commands:\n" +
            "type '/add note-text' - adds a note where 'note-text' is a text of a note,\n" +
            "type '/all' - type this command to see all notes,\n" +
            "type '/del id' - this command deletes specific note by id,\n" +
            "type '/clear' - this command removes all notes.\n";

    private static final String header = "Notes:\n";

    private static final String viewAll = "Type '/all' to see all the notes.\n";

    private static final String emptyNote = "Note is empty! Type note text after command '/add'.";

    private static final String addedNote = "' - Note added successfully!\n" + viewAll;

    private static final String emptyNotes = "Sorry, list is empty. To add a note type: '/add' and add a note text after.\n";

    private static final String emptyDel = "Please, add an id of a note after '/del' command.\n";

    private static final String commandList = "Type '/help' for command list.\n";

    private static final String listCleared = "List cleared. Type '/help' for command list.\n";

    private static final String wrongId = "Wrong id. Please try again.\n";

    private static final String noteDeleted = " deleted successfully!\n" + viewAll;

    private static final String incorrectInput = "Incorrect input." + commandList;
    //************************************************************//

    //Инициализация
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        try {
            botApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Метод для отправки сообщений
    public void sendMessage(Long id, String text) {
        SendMessage sendMsg = new SendMessage();
        sendMsg.setChatId(id);
        sendMsg.setText(text);
        try {
            execute(sendMsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Listener
    public void onUpdateReceived(Update update) {

        //Список для хранения сообщений
        List<String> notes;

        //Получаем сообщение в msg
        Message msg = update.getMessage();

        //Получаем id чата
        Long user_id = msg.getChatId();

        //Получаем текст из сообщения
        String txt = msg.getText();

        /**
         * Получаем список заметок определенного пользователя,
         * еслт его еще нет, то заносим в мапу и создаем пустой список.
         */
        if (!map.containsKey(user_id)) {
            notes = new ArrayList<>();
            map.put(user_id, notes);
        } else {
            notes = map.get(user_id);
        }

        //Получаем объект note от метода parseMessage
        Note note = parseMessage(txt, user_id);

        String cmd = note.getCmdText();
        String noteText = note.getNoteText();
        int note_id = note.getNoteId();

        //Выбор команды
        switch (cmd) {

            //Начало
            case "/start":
                sendMessage(user_id, greeting);
                break;

            //Получение списка команд
            case "/help": {
                sendMessage(user_id, help);
                break;
            }

            //Вывод всех заметок
            case "/all": {
                String s = "";
                if (notes.size() == 0) {
                    sendMessage(user_id, emptyNotes);
                } else {
                    for (int i = 0; i < notes.size(); i++) {
                        s += "#" + (i + 1) + " " + notes.get(i) + "\n";
                    }
                    sendMessage(user_id, header + s + commandList);
                }
                break;
            }

            //Удалить все заметки
            case "/clear": {
                notes.clear();
                sendMessage(user_id, listCleared);
                break;
            }

            //Добавление заметки. Пример: '/add Купить хлеб.'
            case "/add": {
                //Проверям случай, что строка пустая
                if (note.getNoteText().length() == 0) {
                    sendMessage(user_id, emptyNote);
                    break;
                }
                notes.add(noteText);
                sendMessage(user_id, "'" + noteText + addedNote);
                break;
            }

            //Удаление заметки из списка. Пример: '/del 2'
            case "/del": {
                if (note_id < 1 || note_id > notes.size()) {
                    sendMessage(user_id, wrongId);
                } else {
                    notes.remove(note_id - 1);
                    sendMessage(user_id, "#" + note_id + noteDeleted);
                }
                break;
            }

            //Защита от неправильного ввода
            default:
                sendMessage(user_id, incorrectInput);
                break;
        }
    }

    //Определяем тип команды, текст заметки и номер заметки для удаления
    public Note parseMessage(String text, long user_id) {

        Note note = new Note();

        int noteId = 0;
        String noteFromText = "";
        String cmdFromText = "";

        //Проверка на случай, если текст сообщения начинается с команды '/start'
        if (text.startsWith("/start")) {
            cmdFromText = "/start";
        }

        //Проверка на случай, если текст сообщения начинается с команды '/help'
        if (text.startsWith("/help")) {
            cmdFromText = "/help";
        }

        //Проверка на случай, если текст сообщения начинается с команды '/clear'
        if (text.startsWith("/clear")) {
            cmdFromText = "/clear";
        }

        //Проверка на случай, если текст сообщения начинается с команды '/all'
        if (text.startsWith("/all")) {
            cmdFromText = "/all";
        }

        //Проверка на случай, если текст сообщения начинается с команды '/add'
        if (text.startsWith("/add")) {
            cmdFromText = "/add";
            noteFromText = text.replace("/add", "").trim();
        }

        //Проверка на случай, если текст сообщения начинается с команды '/del'
        if (text.startsWith("/del")) {
            cmdFromText = "/del";
            noteFromText = text.replace("/del", "").trim();

            //Проверка на случай пустого id.
            if (noteFromText.length() == 0) {
                sendMessage(user_id, emptyDel);
                return null;
            } else {
                try {
                    noteId = Integer.parseInt(noteFromText);
                    System.out.println("number :" + noteId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }

        note.setNoteId(noteId);
        note.setCmdText(cmdFromText);
        note.setNoteText(noteFromText);

        return note;
    }


    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

}
