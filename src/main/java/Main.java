import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static String username;
    public static String qbServer;
    public static String password;
    public static String botToken;

    public static String botName;


    public static void main(String[] args){
        if (args.length != 5) {
            System.out.println("您设置的参数不是五个！请依次写入参数qb地址 qb用户名 qb密码 TelegramBotToken TelegramBotName");
            return;
        }

        try {
            qbServer = args[0];
            username = args[1];
            password = args[2];
            TelegramBot c = new TelegramBot();

            c.botToken = args[3];
            c.botName = args[4];

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(c);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }
}
