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
            if (userSend.startsWith("/about")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("""
                        本程序为开源项目，github地址：
                        """);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (userSend.contains("http") || userSend.contains("magnet")) {//馒头链接
                System.out.println("正在添加种子链接！");
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("正在添加种子链接！");
                sendDownloadMessage(userSend, message);
                }

            else if (userSend.startsWith("/delete")) {
                if (userSend.equals("/delete_all")) {
                    System.out.println("删除所有");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    message.setText("确认删除所有torrent？");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    InlineKeyboardButton s = new InlineKeyboardButton();
                    InlineKeyboardButton r = new InlineKeyboardButton();
                    s.setText("\uD83D\uDEAB取消");
                    r.setText("\u26A0确认");
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
                    System.out.println("删除某个种子");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/delete") + 7);
                        System.out.println(hash);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.delete(hash);
                            Thread.sleep(1000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("无任何下载任务")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("对不起，您无权管理该任务！");

                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        System.out.println("已发送信息");
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }

            else if (userSend.startsWith("/resume")) {
                if (userSend.equals("/resume_all")) {
                    System.out.println("继续所有");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        Qbittorrent.resumeAll();
                        message.setText("正在继续所有下载任务！");
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        Thread.sleep(2000);
                        String detail = Qbittorrent.printDetail();
                        if (!detail.contains("无任何下载任务")) {
                            setButtons(message);
                        }
                        message.setText(detail);
                        execute(message);
                        System.out.println("已发送信息");
                    } catch (TelegramApiException | InterruptedException | IOException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("继续某个任务");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/resume") + 7);
                        System.out.println(hash);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.resume(hash);
                            Thread.sleep(2000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("无任何下载任务")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("对不起，您无权管理该任务！");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        System.out.println("已发送信息");
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                }
            }



            else if (userSend.startsWith("/pause")) {
                if (userSend.equals("/pause_all")) {
                    System.out.println("暂停所有");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        Qbittorrent.pauseAll();
                        message.setText("正在暂停所有下载任务！");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        Thread.sleep(2000);
                        String detail = Qbittorrent.printDetail();
                        if (!detail.contains("无任何下载任务")) {
                            setButtons(message);
                        }
                        message.setText(detail);
                        execute(message);
                        System.out.println("已发送信息");
                    } catch (TelegramApiException | IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("暂停某个任务");
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    try {
                        String hash = userSend.substring(userSend.indexOf("/pause") + 6);
                        System.out.println("tgpause(): " + hash);
                        if (Qbittorrent.isAbilityManageTorrent(hash, userId)) {
                            Qbittorrent.pause(hash);
                            Thread.sleep(2000);
                            String detail = Qbittorrent.printDetail();
                            if (!detail.contains("无任何下载任务")) {
                                setButtons(message);
                            }
                            message.setText(detail);
                        }else message.setText("对不起，您无权管理该任务！");
                    } catch (IOException | InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        execute(message);
                        System.out.println("已发送信息");
                    } catch (TelegramApiException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }




            else if (userSend.equals("/status")) {
                System.out.println("查询状态");
                SendMessage message = new SendMessage();
                try {
                    message.setChatId(update.getMessage().getChatId());
                    String text = Qbittorrent.printDetail();
                    message.setText(text);
                    if (!text.contains("无任何下载任务")) {
                        setButtons(message);
                    }
                    execute(message);
                } catch (IOException | TelegramApiException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            else if (userSend.equals("/status_downloading")) {
                System.out.println("查询正在下载的状态");
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
                if(status.contains("名称")){
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    InlineKeyboardButton k = new InlineKeyboardButton();
                    k.setText("刷新");
                    k.setCallbackData("refreshDownloadingStatus");
                    rowInline.add(k);
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    message.setReplyMarkup(markupInline);
                }


                int messageId;
                try {
                    Message response = execute(message);
                    messageId = response.getMessageId();//这里是messageID
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }

            }




        }

        if (update.hasCallbackQuery()) {
            long userId = update.getCallbackQuery().getFrom().getId();
            System.out.println("消息来源" + userId);
            System.out.println(510);
            String call_data = update.getCallbackQuery().getData();
            update.getCallbackQuery().getMessage();
            int message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();


            if(call_data.contains("edit_torrent")){
                System.out.println(12879);
                EditMessageText new_message = new EditMessageText();
                new_message.setChatId(chat_id);
                new_message.setMessageId(message_id);
                setButtons(new_message);
                String answer;
                try {
                    System.out.println(511);
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
                        answer = answer.replace("\uD83D\uDE4A删除：/delete", "\uD83D\uDE48暂停：/pause");
                        answer = answer.replace("\uD83D\uDE49继续：/resume", "\uD83D\uDE48暂停：/pause");
                        new_message.setText(answer);
                        option = "暂停";
                    }
                    case "edit_torrent_resume" -> {
                        answer = answer.replace("\uD83D\uDE4A删除：/delete", "\uD83D\uDE49继续：/resume");
                        answer = answer.replace("\uD83D\uDE48暂停：/pause", "\uD83D\uDE49继续：/resume");
                        new_message.setText(answer);
                        option = "继续";
                    }
                    case "edit_torrent_delete" -> {
                        answer = answer.replace("\uD83D\uDE48暂停：/pause", "\uD83D\uDE4A删除：/delete");
                        answer = answer.replace("\uD83D\uDE49继续：/resume", "\uD83D\uDE4A删除：/delete");
                        new_message.setText(answer);
                        option = "删除";
                    }
                }
                try {
                    execute(new_message);
                    sendAnswerCallbackQuery(option + "操作",false, update.getCallbackQuery());//增加回响
                } catch (TelegramApiException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    try {
                        sendAnswerCallbackQuery("操作失败",false, update.getCallbackQuery());//增加回响
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
                k.setText("刷新");
                k.setCallbackData("refreshDownloadingStatus");
                rowInline.add(k);
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                if(!answer.contains("无正在下载任务")){//如果有正在下载的任务，就设置刷新按钮
                    new_message.setReplyMarkup(markupInline);
                }
                try {
                    execute(new_message);
                    sendAnswerCallbackQuery("已刷新",false, update.getCallbackQuery());//增加回响
                } catch (TelegramApiException e) {
                    try {
                        sendAnswerCallbackQuery("操作失败",false, update.getCallbackQuery());//增加回响
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
                    new_message.setText("已取消！");
                    try {
                        execute(new_message);
                        sendAnswerCallbackQuery("取消删除", false, update.getCallbackQuery());
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
                        sendAnswerCallbackQuery("种子已全部删除",true, update.getCallbackQuery());//增加回响
                        System.out.println("已发送信息");
                    } catch (IOException | InterruptedException | TelegramApiException e) {
                        try {
                            sendAnswerCallbackQuery("操作失败",false, update.getCallbackQuery());//增加回响
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
        s.setText("\uD83D\uDE48暂停");
        r.setText("\uD83D\uDE49继续");
        d.setText("\uD83D\uDE4A删除");
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
        s.setText("\uD83D\uDE48暂停");
        r.setText("\uD83D\uDE49继续");
        d.setText("\uD83D\uDE4A删除");
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
            System.out.println(a);//过五秒后再发送正在下载的列表消息
            message1.setText(a);//每下载一个任务后都执行downloading_status
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton k = new InlineKeyboardButton();
            k.setText("刷新");
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

