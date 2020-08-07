package ru.ufanet;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    //Токен выданный BotFather
    public final String botToken = "1258217203:AAGK2sImKKCVSs9oWQp8ivoxBvb0LM7J1Hc";

    //Имя бота
    public final String botUsername = "emilNotesBot";

    //Строка приветствия
    public final String greeting = "Hello!\n" +
            "This is a bot that helps you to manage your notes.\n" +
            "Type: '/help' to see all of the available commands.";

    //Список доступных команд
    public final String help = "List of all the available commands:\n" +
            "type '/add note-text' - adds a note where 'note-text' is a text of a note,\n" +
            "type '/all' - type this command to see all notes,\n" +
            "type '/del id' - this command deletes specific note by id,\n" +
            "type '/clear' - this command removes all notes.";

    //Заголовок для списка заметок
    public final String header = "Notes:\n";

    //Если написана команда '/add' без текста заметки
    public final String emptyNote = "Note is empty! Type note text after command '/add'.";

    //Успешное добавление заметки
    public final String addedNote = " - Note added successfully! Type '/all' to see all notes.";

    //На случай, если еще не было добавлено заметок, но введена команда '/all'
    public final String emptyNotes = "Sorry, list is empty. To add a note type: '/add' and add a note text after.";

    //На случай, если еще после команды '/del' ничего не написано
    public final String emptyDel = "Please, add an id of a note after '/del' command";

    //Список для хранения сообщений
    List<String> notes = new ArrayList<String>();

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
    public void sendMessage(Message msg, String text) {
        SendMessage sendMsg = new SendMessage();
        sendMsg.setChatId(msg.getChatId());
        sendMsg.setText(text);
        try {
            execute(sendMsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Listener
    public void onUpdateReceived(Update update) {
        //Получаем сообщение в msg
        Message msg = update.getMessage();

        //Получаем текст из сообщения
        String txt = msg.getText();

        //Строка для ввода текста заметки
        String s = "";

        //Строка для хранения текста заметки
        String noteText = "";

        //Id заметки для удаления
        int note_id = 0;

        if (txt.startsWith("/add")) {
            noteText = txt.replace("/add", "").trim();
            txt = "/add";
        }

        if (txt.startsWith("/del")) {
            noteText = txt.replace("/del", "").trim();
            //Проврка на случай пустого id.
            if (noteText.length() == 0) {
                sendMessage(msg, emptyDel);
                return;
            } else {
                note_id = Integer.parseInt(noteText);
                txt = "/del";
            }
        }

        switch (txt) {
            //Начало
            case "/start":
                sendMessage(msg, greeting);
                break;

            //Получение списка команд
            case "/help": {
                sendMessage(msg, help);
                break;
            }

            //Добавление заметки. Пример: '/add Купить хлеб.'
            case "/add": {
                if (noteText.length() == 0) {
                    sendMessage(msg, emptyNote);
                } else {
                    notes.add(noteText);
                    sendMessage(msg, "'" + noteText + "'" + addedNote);
                }
                break;
            }

            //Вывод всех заметок
            case "/all": {
                if (notes.size() == 0) {
                    sendMessage(msg, emptyNotes);
                } else {
                    for (int i = 0; i < notes.size(); i++) {
                        s += "#" + (i + 1) + " " + notes.get(i) + "\n";
                    }
                    sendMessage(msg, header + s + "Type '/help' for command list.\n");
                }
                break;
            }

            //Удаление заметки из списка. Пример: '/del 2'
            case "/del": {
                if (note_id < 1 || note_id > notes.size()) {
                    sendMessage(msg, "Wrong id. Please try again.");
                } else {
                    String removed = notes.get(note_id - 1);
                    notes.remove(note_id - 1);
                    sendMessage(msg, removed + " - Note removed successfully!");
                }
            }

            //Удалить все заметки
            case "/clear": {
                notes.removeAll(notes);
                sendMessage(msg, "List cleared. Type '/help' for command list.\n");
            }
        }
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

}
