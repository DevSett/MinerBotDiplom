package app.classes.services;


import app.classes.models.MinerStatApi;
import app.classes.models.PriceApi;
import app.classes.models.RigApi;
import app.classes.models.Property;
import com.google.gson.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Сергей on 05.07.2017.
 */
@Component
public class ServiceMoney {

    @Autowired
    Property property;

    private Logger logger = Logger.getLogger(ServiceMoney.class);
    private String urlPriceApi = "https://api.nexchange.io/en/api/v1/price/&&&&&/latest/?format=json";
    private String urlProxys = "http://www.freeproxy-list.ru/api/proxy?token=demo";

    public static void main(String[] args) {
        ServiceMoney serviceMoney = new ServiceMoney();
        serviceMoney.getProxy();
    }
    public List<String> getProxy(){
        List<String> proxy = new ArrayList<>();
        String page = getPage(urlProxys);
        for (String s : page.split("\n")) {
            proxy.add(s);
        }
        return proxy;
    }
    public String getRate() {

        Map map = getRateApi();
        if (map == null) return "";
        return "\uD83D\uDC8EКурс Эфириума: \n $ - " + map.get("usd") + "\n \u20BD - " + map.get("rub") + "\n\n Цена средняя[(min+max)/2]";
    }

    private Map<String, Double> getRateApi() {
        PriceApi priceEthUsd;
        PriceApi priceEthRub;

        String page = getPage(urlPriceApi.replace("&&&&&", "ETHUSD"));
        if (page.isEmpty()) return null;

        String pageRub = getPage(urlPriceApi.replace("&&&&&", "ETHRUB"));
        if (pageRub.isEmpty()) return null;

        String kurses = page.replaceAll("]", "") + "," + pageRub.replaceAll("\\[", "");

        JsonElement jelement = new JsonParser().parse(kurses);
        JsonArray array = jelement.getAsJsonArray();

        JsonObject jobjectUsd = array.get(0).getAsJsonObject();
        JsonObject jobjectRub = array.get(1).getAsJsonObject();

        priceEthUsd = new Gson().fromJson(jobjectUsd.get("ticker").getAsJsonObject(), PriceApi.class);
        priceEthRub = new Gson().fromJson(jobjectRub.get("ticker").getAsJsonObject(), PriceApi.class);

        HashMap map = new HashMap();
        map.put("usd", (Double.valueOf(priceEthUsd.getAsk()) + Double.valueOf(priceEthUsd.getBid())) / 2);
        map.put("rub", (Double.valueOf(priceEthRub.getAsk()) + Double.valueOf(priceEthRub.getBid())) / 2);
        return map;
    }

//    public static void main(String[] args) {
//        ServiceMoney serviceMoney = new ServiceMoney();
//        System.out.println(serviceMoney.getBalance("0x1a35478Ec84E49bf4F5A78A470a2740ee7fe042F") + "\n-!-");
//        System.out.println(serviceMoney.getRate() + "\n-!-");
//    }

    public String getInfo(String urlMiner, String account, String type) {
        String page = getPage(
                urlMiner
                        + "/miner/" + account + "/workers"
        );
        if (page.isEmpty()) return "";

        JsonElement jelement = new JsonParser().parse(page);
        JsonObject jobject = jelement.getAsJsonObject();

        JsonArray array = jobject.get("data").getAsJsonArray();

        List<RigApi> rigs = new ArrayList<>();

        for (JsonElement jsonElement : array) {
            JsonObject object = jsonElement.getAsJsonObject();
            RigApi rig = new Gson().fromJson(object, RigApi.class);
            rigs.add(new RigApi(
                    rig.getWorker(),
                    rig.getTime(),
                    rig.getLastSeen(),
                    rig.getReportedHashrate(),
                    rig.getCurrentHashrate(),
                    rig.getValidShares(),
                    rig.getInvalidShares(),
                    rig.getStaleShares(),
                    rig.getAverageHashrate()));
        }


        String result = "";
        for (RigApi rig : rigs) {
            if (!result.isEmpty()) result += "\n\n";
            result += "\uD83D\uDDD3Рига - " + rig.getWorker() +
                    "\n Средний хешрейт - " + rig.getAverageHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                    "\n Текущий хэшрейт - " + rig.getCurrentHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                    "\n Зарегистрированный хэшрейт - " + rig.getReportedHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                    "\n Отгаданные блоки - " + rig.getValidShares() +
                    "\n Старые блоки - " + rig.getStaleShares() +
                    "\n Опасные блоки - " + rig.getInvalidShares() +
                    "\n Последнее обновление - " + new SimpleDateFormat("HH:mm:ss").format(new Date(rig.getLastSeen() * 1000));
        }
        return result;
        //            page = getPage(urlEth);
//            String prefix = "id=\"last_last\" dir=\"ltr\">";
//            startEth = page.indexOf(prefix) + prefix.length();
//            String kursEth = page.substring(startEth, page.indexOf("<", startEth));
//            kursEth = kursEth.replaceAll(",", ".");
//
//            page = getPage(urlUsd);
//            int startUsd = page.indexOf(prefix) + prefix.length();
//            String kursUsd = page.substring(startUsd, page.indexOf("<", startUsd));
//            kursUsd = kursUsd.replaceAll(",", ".");
//
//            Double balanceUsd = Double.valueOf(balance.substring(0, balance.indexOf("ETH"))) * Double.valueOf(kursEth);
//            Double balanceRub = balanceUsd * (Double.valueOf(kursUsd) - 5);
//
//
//            return new String[]{
//                    balance,
//                    hashrate,
//                    balanceUsd.toString().substring(0, balanceUsd.toString().indexOf(".") + 4) + " $",
//                    balanceRub.toString().substring(0, balanceRub.toString().indexOf(".") + 4) + " \u20BD"};

    }

    private String getPage(String urlPage) {
        try {

            URL url;
            URLConnection uc;
            StringBuilder parsedContentFromUrl = new StringBuilder();
//        System.out.println("Getting content for URl : " + urlPage);
            url = new URL(urlPage);

            uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/4.76");
            uc.connect();
            uc.getInputStream();
            BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
            int ch;
            while ((ch = in.read()) != -1) {
                parsedContentFromUrl.append((char) ch);
            }
            return parsedContentFromUrl.toString();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }


    public String getAverage(String currentLink, String account, String type) {
        String page = getPage(currentLink + "/miner/:miner/currentStats".replaceAll(":miner", account));

        if (page.isEmpty()) return "";
        JsonElement jelement = new JsonParser().parse(page);
        JsonObject jobject = jelement.getAsJsonObject();
        MinerStatApi minerStatistic = new Gson().fromJson(jobject.get("data"), MinerStatApi.class);


        String result = "\uD83D\uDDD2Статистика: " +
                "\n Активные риги - " + minerStatistic.getActiveWorkers() +
                "\n Намайнил - " + Math.round(minerStatistic.getUnpaid() / (type.equals("1") ? 1000d : 1)) / (type.equals("1") ? 100000d : 1) +
                "\n Монет в минуту - " + minerStatistic.getCoinsPerMin() +
                "\n Монет за неделю - " + minerStatistic.getCoinsPerMin().multiply(BigDecimal.valueOf(10080)) +
                "\n Монет за месяц - " + minerStatistic.getCoinsPerMin().multiply(BigDecimal.valueOf(43800)) +
                "\n Биткоинов в месяц - " + minerStatistic.getBtcPerMin().multiply(BigDecimal.valueOf(43800)) +
                "\n Долларов в месяц - " + minerStatistic.getUsdPerMin().multiply(BigDecimal.valueOf(43800)) +
                "\n Зарегистрированный хешрейт - " + minerStatistic.getReportedHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                "\n Текущий хешрейт - " + minerStatistic.getCurrentHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                "\n Средний хэшрейт - " + minerStatistic.getAverageHashrate().doubleValue() / (type.equals("0") ? 1000000 : 1) +
                "\n Отгаданные блоки - " + minerStatistic.getValidShares() +
                "\n Старые блоки - " + minerStatistic.getStaleShares() +
                "\n Опасные блоки - " + minerStatistic.getInvalidShares() +
                "\n Последнее обновление - " + new SimpleDateFormat("HH:mm:ss").format(new Date(1000 * minerStatistic.getLastSeen()));

        return result;

    }

    private int length = "000000000000000000".length();

    public String getBalance(String account) {
//        String page = getPage(property.getLinkBalance() + account);
        String page = getPage("https://etherchain.org/api/account/" + account);
        System.out.println("https://etherchain.org/api/account/" + account);
        if (page.isEmpty()) return "";

        System.out.println(page);
        JsonElement jelement = new JsonParser().parse(page);
        JsonObject jobject = jelement.getAsJsonObject();

        JsonArray array = jobject.get("data").getAsJsonArray();
        Double balance = array.get(0).getAsJsonObject().get("balance").getAsDouble() / 10;


//            Double dol = Double.valueOf(balance) * Double.valueOf(kursEth);
//            return "Баланс: " + balance + " ETH\n" + "По курсу доллара: " + dol + " $\n По курсу рубля: " + dol * Double.valueOf(kursUsd);
        Map<String, Double> map = getRateApi();
        return "\uD83D\uDCB0Баланс:" +
                "\n Эфириум - " + balance.toString().substring(0, 9) +
                "\n $ - " + new BigDecimal(map.get("usd")).multiply(BigDecimal.valueOf(Double.valueOf(balance.toString().substring(0, 9)))).toString().substring(0, 9) +
                "\n \u20BD - " + new BigDecimal(map.get("rub")).multiply(BigDecimal.valueOf(Double.valueOf(balance.toString().substring(0, 9)))).toString().substring(0, 9);

    }
}
