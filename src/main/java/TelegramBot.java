import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {


    public String botToken;
    public String botName;


    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String userSend = update.getMessage().getText();
            System.out.println(userSend);
            long userId = update.getMessage().getFrom().getId();
            if (userSend.startsWith("/hi")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("""
                        Hi, am here for your dissertation.
                        """);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (userSend.contains("http") || userSend.contains("magnet")) {//馒头链接
                System.out.println("This task has been added");
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("This task has been added");
                sendDownloadMessage(userSend, message);
                }

            else if (userSend.startsWith("/delete")) {
                if (userSend.equals("/delete_all")) {
                    System.out.println("delete all");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    message.setText("Are you sure to delete all?");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    InlineKeyboardButton s = new InlineKeyboardButton();
                    InlineKeyboardButton r = new InlineKeyboardButton();
                    s.setText("\uD83D\uDEABCancel");
                    r.setText("\u26A0Confirm");
                    s.setCallbackData("delete_all_torrent_cancel");
                    r.setCallbackData("delete_all_torrent_confirm");
                    rowInline.add(s);
                    rowInline.add(r);
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    message.setReplyMarkup(markupInline);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Delete a file");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/delete") + 7);
                        System.out.println(hash);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.delete(hash);
                            Thread.sleep(1000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("no downloads available")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("You do not have permission!");

                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

            else if (userSend.startsWith("/resume")) {
                if (userSend.equals("/resume_all")) {
                    System.out.println("resume all");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        Qbittorrent.resumeAll();
                        message.setText("All download tasks are resuming!");
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        Thread.sleep(2000);
                        String detail = Qbittorrent.printDetail();
                        if (!detail.contains("no downloads available")) {
                            setButtons(message);
                        }
                        message.setText(detail);
                        execute(message);
                    } catch (TelegramApiException | InterruptedException | IOException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                } else {
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/resume") + 7);
                        System.out.println(hash);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.resume(hash);
                            Thread.sleep(2000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("no downloads available")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("You do not have permission!");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                       
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                }
            }



            else if (userSend.startsWith("/pause")) {
                if (userSend.equals("/pause_all")) {
                    System.out.println("pause all");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        Qbittorrent.pauseAll();
                        message.setText("All download tasks are being suspended!");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        Thread.sleep(2000);
                        String detail = Qbittorrent.printDetail();
                        if (!detail.contains("no downloads available")) {
                            setButtons(message);
                        }
                        message.setText(detail);
                        execute(message);
                       
                    } catch (TelegramApiException | IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                } else {
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/pause") + 6);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.pause(hash);
                            Thread.sleep(2000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("no downloads available")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("You do not have permission!");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                       
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }




            else if (userSend.equals("/status")) {
                SendMessage message = new SendMessage();
                try {
                    message.setChatId(update.getMessage().getChatId());
                    String text = Qbittorrent.printDetail();
                    message.setText(text);
                    execute(message);
                } catch (IOException | TelegramApiException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            else if (userSend.equals("/status_downloading")) {
                String status;
                try {
                    status = Qbittorrent.printDownloadingDetail();
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
                SendMessage message = new SendMessage();
                long chatId = update.getMessage().getChatId();
                message.setChatId(chatId);
                message.setText(status);
                if(status.contains("Name")){
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    InlineKeyboardButton k = new InlineKeyboardButton();
                    k.setText("Refresh");
                    k.setCallbackData("refreshDownloadingStatus");
                    rowInline.add(k);
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    message.setReplyMarkup(markupInline);
                }


                int messageId;
                try {
                    Message response = execute(message);
                    messageId = response.getMessageId();//messageID
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }

            }




        }

        if (update.hasCallbackQuery()) {
            long userId = update.getCallbackQuery().getFrom().getId();
            System.out.println("userId: " + userId);
            String call_data = update.getCallbackQuery().getData();
            update.getCallbackQuery().getMessage();
            int message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();


            if(call_data.contains("edit_torrent")){
                EditMessageText new_message = new EditMessageText();
                new_message.setChatId(chat_id);
                new_message.setMessageId(message_id);
                setButtons(new_message);
                String answer;
                try {
                    answer = Qbittorrent.printDetail();
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
                String option = "";
                switch (call_data) {
                    case "edit_torrent_pause" -> {
                        System.out.println(512);
//                        System.out.println("更改之前的\n" + answer);
                        answer = answer.replace("\uD83D\uDE4ADelete：/delete", "\uD83D\uDE48Pause：/pause");
                        answer = answer.replace("\uD83D\uDE49Resume：/resume", "\uD83D\uDE48Pause：/pause");
                        new_message.setText(answer);
                        option = "Pause";
                    }
                    case "edit_torrent_resume" -> {
                        answer = answer.replace("\uD83D\uDE4ADelete：/delete", "\uD83D\uDE49Resume：/resume");
                        answer = answer.replace("\uD83D\uDE48Pause：/pause", "\uD83D\uDE49Resume：/resume");
                        new_message.setText(answer);
                        option = "Resume";
                    }
                    case "edit_torrent_delete" -> {
                        answer = answer.replace("\uD83D\uDE48Delete：/pause", "\uD83D\uDE4ADelete：/delete");
                        answer = answer.replace("\uD83D\uDE49Resume：/resume", "\uD83D\uDE4ADelete：/delete");
                        new_message.setText(answer);
                        option = "Delete";
                    }
                }
                try {
                    execute(new_message);
                    sendAnswerCallbackQuery(option + " done",false, update.getCallbackQuery());
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    try {
                        sendAnswerCallbackQuery("Failed!",false, update.getCallbackQuery());
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }}



            if (call_data.equals("refreshDownloadingStatus")){
                System.out.println(165);
                String answer;
                try {
                    answer = Qbittorrent.printDownloadingDetail();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                EditMessageText new_message = new EditMessageText();
                new_message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                new_message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                new_message.setText(answer);
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                InlineKeyboardButton k = new InlineKeyboardButton();
                k.setText("Refresh");
                k.setCallbackData("refreshDownloadingStatus");
                rowInline.add(k);
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                if(!answer.contains("No tasks are being downloaded")){//If there is a task being downloaded, set a refresh button
                    new_message.setReplyMarkup(markupInline);
                }
                try {
                    execute(new_message);
                    sendAnswerCallbackQuery("Refreshed",false, update.getCallbackQuery());
                } catch (TelegramApiException e) {
                    try {
                        sendAnswerCallbackQuery("Failed!",false, update.getCallbackQuery());
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.out.println(166);
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

            if (call_data.contains("delete_all_torrent_")){
                if (call_data.equals("delete_all_torrent_cancel")){
                    EditMessageText new_message = new EditMessageText();
                    new_message.setChatId(chat_id);
                    new_message.setMessageId(message_id);
                    new_message.setText("Canceled");
                    try {
                        execute(new_message);
                        sendAnswerCallbackQuery("Canceled", false, update.getCallbackQuery());
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                } else if (call_data.equals("delete_all_torrent_confirm")) {
                    try {
                        Qbittorrent.deleteAll();
                        EditMessageText new_message = new EditMessageText();
                        new_message.setChatId(chat_id);
                        new_message.setMessageId(message_id);
                        Thread.sleep(2000);
                        new_message.setText(Qbittorrent.printDetail());
                        execute(new_message);
                        sendAnswerCallbackQuery("All file have been deleted",true, update.getCallbackQuery());
                       
                    } catch (IOException | InterruptedException | TelegramApiException e) {
                        try {
                            sendAnswerCallbackQuery("Failed!",false, update.getCallbackQuery());
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }


                }
            }


        }
    }

    private void setButtons(SendMessage message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton s = new InlineKeyboardButton();
        InlineKeyboardButton r = new InlineKeyboardButton();
        InlineKeyboardButton d = new InlineKeyboardButton();
        s.setText("\uD83D\uDE48Pause");
        r.setText("\uD83D\uDE49Resume");
        d.setText("\uD83D\uDE4ADelete");
        s.setCallbackData("edit_torrent_pause");
        r.setCallbackData("edit_torrent_resume");
        d.setCallbackData("edit_torrent_delete");
        rowInline.add(s);
        rowInline.add(r);
        rowInline.add(d);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }

    private void setButtons(EditMessageText message) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton s = new InlineKeyboardButton();
        InlineKeyboardButton r = new InlineKeyboardButton();
        InlineKeyboardButton d = new InlineKeyboardButton();
        s.setText("\uD83D\uDE48Pause");
        r.setText("\uD83D\uDE49Resume");
        d.setText("\uD83D\uDE4ADelete");
        s.setCallbackData("edit_torrent_pause");
        r.setCallbackData("edit_torrent_resume");
        d.setCallbackData("edit_torrent_delete");
        rowInline.add(s);
        rowInline.add(r);
        rowInline.add(d);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
    }

    private void sendDownloadMessage(String userSend, SendMessage message) {
        try {
            Message m = execute(message);
            Qbittorrent.downloadTorrent(userSend);
            Thread.sleep(5000);
//            message = setButtons(message);
            EditMessageText message1 = new EditMessageText();
            message1.setMessageId(m.getMessageId());
            message1.setChatId(m.getChatId());
            String a = Qbittorrent.printDownloadingDetail();
            System.out.println(a);//Send a message about the list being downloaded after five seconds.
            message1.setText(a);//Execute download_status after each download task
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton k = new InlineKeyboardButton();
            k.setText("Refresh");
            k.setCallbackData("refreshDownloadingStatus");
            rowInline.add(k);
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            message1.setReplyMarkup(markupInline);
            execute(message1);
        } catch (IOException | InterruptedException | TelegramApiException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }






    private void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) throws TelegramApiException{
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        execute(answerCallbackQuery);
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

