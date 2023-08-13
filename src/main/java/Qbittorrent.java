import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

public class Qbittorrent {



    public static String getCookie(){
        String cookie = "";
        try {
            File input = new File("cookie.txt");
            Scanner s = new Scanner(input);
            while(s.hasNext()) {
                cookie = s.nextLine();
                System.out.println("readCookie():" + cookie);
            }
        } catch (FileNotFoundException var3) {
            System.out.println("readCookie():File not found!");
        }
        System.out.println("getCookie(): " + cookie);
        if (isCookieValid(cookie)) {
            return cookie;
        }
        System.out.println("getCookie():Cookie expired, refreshing cookie now");
        cookie = updateCookie();
        return cookie;
    }

    public static String updateCookie() {
        HttpPost httpPost1 = new HttpPost(Main.qbServer + "auth/login");//https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
        List<NameValuePair> nvps = new ArrayList<>();
        System.out.println(Main.qbServer + 222 + Main.username + Main.password);
        nvps.add(new BasicNameValuePair("username", Main.username));
        nvps.add(new BasicNameValuePair("password", Main.password));
        try {
            httpPost1.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            return "0";
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse response4 = httpclient.execute(httpPost1)) {
            response4.close();
            System.out.println("updateCookie():Trying to log in to refresh cookies");
            String qbResponse = response4.toString();
            System.out.println("updateCookie(): " + qbResponse);
            if (qbResponse.contains("OK")) {
                String cookie = qbResponse.substring(qbResponse.indexOf("SID="), qbResponse.indexOf("; HttpOnly;"));//Intercepting cookie contents after sid
                System.out.println("cookie为:   " + cookie);
                PrintWriter outFile = new PrintWriter("cookie.txt");//Save and write cookies to this directory
                outFile.println(cookie);
                outFile.close();
                System.out.println("updateCookie():Cookie refreshed successfully");
                return cookie;
            } else {
                System.out.println("updateCookie():Login Fail");
                return "0";
            }

        } catch (ClientProtocolException e) {
            return "0";
        } catch (IOException e) {
            return "0";
        }
    }

    public static boolean isCookieValid(String cookie){
        System.out.println("isCookieValid(): start");
        HttpPost httpPost = new HttpPost(Main.qbServer + "transfer/info");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("cookie", cookie);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        System.out.println("isCookieValid()"+ Main.qbServer);
        CloseableHttpResponse response2 = null;
        String body;
        try {
            response2 = httpclient.execute(httpPost);
            body = EntityUtils.toString(response2.getEntity());
            response2.close();
        } catch (IOException e) {
            System.out.println("wrong password.");
            return false;
        }

        if (body.contains("dl_info_speed")) {
            System.out.println("isCookieValid():cookies is valid");
            return true;
        }
        System.out.println("isCookieValid():cookies is not valid!!!!!!!!!!!!!!");
        return false;
    }


    public static void downloadTorrent(String link) throws IOException, InterruptedException {
        String cookie = getCookie();
        if (cookie.contains("SID")) {
            HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/add");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("urls", link));
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("cookie", cookie);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            response2.close();
            System.out.println("downloadTorrent(): Have tried to add to the list, but it always returns FAILS, not sure if it was actually added successfully!");

        }
        System.out.println("downloadTorrent(): " + cookie);
    }




    public static String getDetail() throws IOException {
        String cookie = getCookie();
        HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/info");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("cookie", cookie);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        String body = EntityUtils.toString(response2.getEntity());
        System.out.println(body);
        response2.close();
        return body;
    }

    public static String getTagsByHash(String hash) throws IOException {
        String cookie = getCookie();
        HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/info");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("cookie", cookie);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("hashes", hash));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        String body = EntityUtils.toString(response2.getEntity());
        System.out.println(body);
        response2.close();
        JSONArray array = new JSONArray(body);
        String tags = array.getJSONObject(0).getString("tags");
        return tags;
    }

    public static boolean isAbilityManageTorrent(String hash, long userId) throws IOException {
        return true;
    }

    public static String getDownloadingDetail() throws IOException {
        String cookie = getCookie();
        HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/info");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("cookie", cookie);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("filter", "downloading"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        String body = EntityUtils.toString(response2.getEntity());
        System.out.println(body);
        response2.close();
        return body;
    }

    public static String printDownloadingDetail() throws IOException, InterruptedException {
        System.out.println("printDownloadingDetail()start");
        String body1 = getDownloadingDetail();
        JSONArray array = new JSONArray(body1);
        if(array.isNull(0) ){
            System.out.println("printDownloadingDetail():The list is empty");
            return "No tasks are being downloaded!";
        }
        StringBuilder detail = new StringBuilder("\uD83D\uDCBE Free Space：" + diskSpaceRemain() + "\n");//emoji https://dplatz.de/blog/2019/emojis-for-java-commandline.html
        for (int j = 0; j < array.length(); j++) {//Take the seeds from each list
            String name = array.getJSONObject(j).getString("name");
            String size;
            String category = array.getJSONObject(j).getString("category");
            String categoryIcon = "\uD83D\uDCC1";//icon
            if(category.equals("tvseries")) categoryIcon = "\uD83D\uDCFA";
            else if (category.equals("movie")) categoryIcon = "\uD83C\uDFAC";
            else if (category.equals("anime")) categoryIcon = "\uD83C\uDFA8";
            else if (category.equals("bbc")) categoryIcon = "\uD83D\uDD0D";

            if (array.getJSONObject(j).getDouble("total_size")/1024/1024<1024) {
                size = String.format("%.2f", array.getJSONObject(j).getDouble("total_size") / 1024 / 1024) + "M";
            }else{
                size = String.format("%.2f", array.getJSONObject(j).getDouble("total_size") / 1024 / 1024 / 1024) + "G";
            }
            String status = array.getJSONObject(j).getString("state");
            String hash = array.getJSONObject(j).getString("hash");
            double progress = array.getJSONObject(j).getDouble("progress");
            if (progress != 1 && status.contains("downloading")) {//Determine if a download is in progress
                double completed = array.getJSONObject(j).getDouble("completed");
                double total = array.getJSONObject(j).getDouble("size");
                String left;
                if (array.getJSONObject(j).getDouble("amount_left") /1024/1024<1024) {
                    left = String.format("%.2f", array.getJSONObject(j).getDouble("amount_left") / 1024 / 1024) + "M";
                }else{
                    left = String.format("%.2f", array.getJSONObject(j).getDouble("amount_left") / 1024 / 1024 / 1024) + "G";
                }
                System.out.println((int) completed);
                System.out.println((int) total);
                double remain = completed / total;
                System.out.println(remain);
                String percent = String.format("%.2f", completed / total * 100) + "%" + "\n" + progressPercentage((int) Math.round(remain * 100), 100);
                String speed = String.format("%.2f", array.getJSONObject(j).getDouble("dlspeed") / 1024 / 1024) + "M/s";
                int eta = array.getJSONObject(j).getInt("eta");
                String strEta;
                if (eta > 60 && eta < 3600) {
                    long minutes = eta / 60;//Seconds to minutes
                    eta = eta % 60;//remaining seconds
                    strEta = minutes + "m" + eta + "s";
                } else if (eta >= 3600 && eta < 8640000) {
                    long hours = eta / 3600;//Seconds to hours
                    eta = eta % 3600;//remaining seconds
                    long minutes = eta / 60;//seconds to minutes
                    eta = eta % 60;//remaining seconds
                    strEta = hours + "h" + minutes + "m" + eta + "s";
                } else if (eta == 8640000) {
                    strEta = "Cannot be completed";
                } else {
                    strEta = eta + "s";
                }
                detail.append("\n" + categoryIcon + "name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: ").append("\n").append("Left：").append(left).append("     ").append("Speed: ").append(speed).append("\nPercentage completed: ").append(percent).append("\nTime left: ").append(strEta).append("\n\uD83D\uDE48Pause：/pause").append(hash).append("\n----------------------------------------");
            }

        }
        System.out.println(detail);

        return detail.toString();
    }







    public static String progressPercentage(int done, int total) {//Generate progress bar, referenced in printDownloadingDetail()
        int size = 26;
        String iconLeftBoundary = "";
        String iconDone = "⣿";
        String iconRemain = "⣀";
        String iconRightBoundary = "";

        if (done > total) {
            throw new IllegalArgumentException();
        }
        int donePercents = (100 * done) / total;
        int doneLength = size * donePercents / 100;

        StringBuilder bar = new StringBuilder(iconLeftBoundary);
        for (int i = 0; i < size; i++) {
            if (i < doneLength) {
                bar.append(iconDone);
            } else {
                bar.append(iconRemain);
            }
        }
        bar.append(iconRightBoundary);
        return "\r" + bar ;
    }


    public static String printDetail() throws IOException, InterruptedException {
        String body1 = getDetail();
        JSONArray array = new JSONArray(body1);
        if(array.isNull(0)){
            return "\uD83D\uDCBE Free Space："  + diskSpaceRemain() + "\n\nThere are currently no downloads available";
        }
        System.out.println("printDetail(): " + array.getJSONObject(0));
        StringBuffer detail = new StringBuffer("\uD83D\uDCBE Free Space：" + diskSpaceRemain() + "\n");
        for (int j = 0; j < array.length(); j++) {
            String name = array.getJSONObject(j).getString("name");
            String size;
            if (array.getJSONObject(j).getDouble("total_size")/1024/1024<1024) {
                size = String.format("%.2f", array.getJSONObject(j).getDouble("total_size") / 1024 / 1024) + "M";
            }else{
                size = String.format("%.2f", array.getJSONObject(j).getDouble("total_size") / 1024 / 1024 / 1024) + "G";
            }

            String status = array.getJSONObject(j).getString("state");
            String hash = array.getJSONObject(j).getString("hash");
            String category = array.getJSONObject(j).getString("category");

            String categoryIcon = "\uD83D\uDCC1";//icon
            if(category.equals("tvseries")) categoryIcon = "\uD83D\uDCFA";
            else if (category.equals("movie")) categoryIcon = "\uD83C\uDFAC";
            else if (category.equals("anime")) categoryIcon = "\uD83C\uDFA8";
            else if (category.equals("bbc")) categoryIcon = "\uD83D\uDD0D";

            double progress = array.getJSONObject(j).getDouble("progress");
            if (progress != 1) {
                double completed = array.getJSONObject(j).getDouble("completed");
                double total = array.getJSONObject(j).getDouble("size");

                String left;
                if (array.getJSONObject(j).getDouble("amount_left") /1024/1024<1024) {
                    left = String.format("%.2f", array.getJSONObject(j).getDouble("amount_left") / 1024 / 1024) + "M";
                }else{
                    left = String.format("%.2f", array.getJSONObject(j).getDouble("amount_left") / 1024 / 1024 / 1024) + "G";
                }
                String percent = String.format("%.2f", completed / total * 100) + "%";
                String speed = String.format("%.2f", array.getJSONObject(j).getDouble("dlspeed") / 1024 / 1024) + "M/s";
                int eta = array.getJSONObject(j).getInt("eta") ;
                String strEta;
                if (eta > 60 && eta <3600) {
                    long minutes = eta / 60;//Seconds to minutes
                    eta = eta % 60;//remaining seconds
                    strEta = minutes + "m" + eta + "s";
                } else if (eta >= 3600 && eta < 8640000) {
                    long hours = eta / 3600;//Seconds to hours
                    eta = eta % 3600;//remaining seconds
                    long minutes = eta / 60;//seconds to minutes
                    eta = eta % 60;//remaining seconds
                    strEta = hours + "h" + minutes + "m" + eta + "s" ;
                } else if (eta == 8640000) {
                    strEta = "Cannot be completed" ;
                } else strEta = eta + "s" ;




                if(status.equals("pausedDL")){
                    detail.append("\n" + categoryIcon + "Name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: Paused").append("\n").append("Left：").append(left).append("     ").append("\nPercentage completed: ").append(percent).append("\n\uD83D\uDE49Resume：/resume").append(hash).append("\n----------------------------------------");
                } else if (status.equals("downloading")) {
                    double remain = completed / total;
                    percent = String.format("%.2f", completed / total * 100) + "%" + "\n" + progressPercentage((int) Math.round(remain * 100), 100);
                    detail.append("\n" + categoryIcon + "Name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: ").append("\n").append("Left：").append(left).append("     ").append("Speed: ").append(speed).append("\nPercentage completed: ").append(percent).append("\nTime left: ").append(strEta).append("\n\uD83D\uDE48Pause：/pause").append(hash).append("\n----------------------------------------");
                } else {
                    detail.append("\n" + categoryIcon + "Name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: ").append("\n").append("Left：").append(left).append("     ").append("Speed: ").append(speed).append("\nPercentage completed: ").append(percent).append("\nTime left: ").append(strEta).append("\n\uD83D\uDE4ADelete：/delete").append(hash).append("\n----------------------------------------");
                }
            } else if (status.equals("uploading")) {
                String upspeed = String.format("%.2f", array.getJSONObject(j).getDouble("upspeed") / 1024 / 1024) + "M/s";
                detail.append("\n" + categoryIcon + "Name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: Completed").append("     ").append("Speed: ").append(upspeed).append("\n\n\uD83D\uDE4ADelete：/delete").append(hash).append("\n----------------------------------------");
            }else{
                detail.append("\n" + categoryIcon + "Name: ").append(name).append("\n").append("Size: ").append(size).append("     ").append("State: Completed").append("\n\n\uD83D\uDE4ADelete：/delete").append(hash).append("\n----------------------------------------");
            }
        }
        System.out.println(detail);
        return detail.toString();
    }

    public static String diskSpaceRemain() throws IOException {
        String cookie = getCookie();
        HttpPost httpGet = new HttpPost(Main.qbServer + "sync/maindata");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("deleteFiles", "true"));//delete file
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.addHeader("cookie", cookie);
        httpGet.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response2 = httpclient.execute(httpGet);
        String body1 =  EntityUtils.toString(response2.getEntity());
        JSONObject b = new JSONObject(body1);
        Double c = b.getJSONObject("server_state").getDouble("free_space_on_disk");
        String freeSpace;

        if (c/1024/1024<1024) {
            freeSpace = String.format("%.2f", c / 1024 / 1024) + "M";
        }else{
            freeSpace = String.format("%.2f", c / 1024 / 1024 / 1024) + "G";
        }

//        System.out.println(freeSpace);
        response2.close();
        return freeSpace;


    }

    public static void deleteAll() throws IOException, InterruptedException {//The hashes of the torrents you want to delete. hashes can contain multiple hashes separated by |, to delete multiple torrents, or set to all, to delete all torrents.
        delete("all");
        System.out.println("deleteAll(): Deleting all files");

    }

    public static void pauseAll() throws IOException, InterruptedException {//The hashes of the torrents you want to delete. hashes can contain multiple hashes separated by |, to delete multiple torrents, or set to all, to delete all torrents.
        pause("all");
        System.out.println("pauseAll(): Pausing all files");
    }

    public static void resumeAll() throws IOException, InterruptedException {//The hashes of the torrents you want to delete. hashes can contain multiple hashes separated by |, to delete multiple torrents, or set to all, to delete all torrents.
        resume("all");
        System.out.println("resumeAll(): Resuming all files");
    }

    public static void delete(String hash) throws IOException, InterruptedException {
        String cookie = getCookie();
        if (cookie.contains("SID")) {
            HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/delete");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("hashes", hash));
            nvps.add(new BasicNameValuePair("deleteFiles", "true"));//delete file
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("cookie", cookie);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("delete(): " + response2);
            response2.close();
            System.out.println("delete(): Delete" + hash);
        }

    }

    public static void pause(String hash) throws IOException, InterruptedException {
        String cookie = getCookie();
        if (cookie.contains("SID")) {
            HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/pause");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("hashes", hash));
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("cookie", cookie);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("pause(): " + response2);
            response2.close();
            System.out.println("pause(): Pause" + hash);
        }

    }


    public static void resume(String hash) throws IOException, InterruptedException {
        String cookie = getCookie();
        if (cookie.contains("SID")) {
            HttpPost httpPost = new HttpPost(Main.qbServer + "torrents/resume");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("hashes", hash));
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("cookie", cookie);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("resume(): " + response2);
            response2.close();
            System.out.println("resume(): Resume" + hash);
        }
    }

}
