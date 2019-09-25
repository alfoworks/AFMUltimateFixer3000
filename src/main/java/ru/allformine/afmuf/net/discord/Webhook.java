package ru.allformine.afmuf.net.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.net.Requests;

import java.util.ArrayList;

public class Webhook {
    private static void sendApiRequest(JsonObject object, String[] extra) {
        object.addProperty("server_id", "lf"); //TODO asap сделать конфиг для айди
        object.addProperty("type", "PACKETHACK_USAGE_DETECTED");
        object.addProperty("group", "secalert");
        object.add("arguments", arrayToJson(extra));
        final String json = object.toString();
        Requests.sendPostJSON(json, "https://allformine.ru/webhook_api/"); //TODO добавить это в конфиг
    }

    private static JsonArray arrayToJson(String[] array) { // TODO: Дикий костыль
        JsonArray jsonArray = new JsonArray();
        for (String s : array) {
            jsonArray.add(s);
        }
        return jsonArray;
    }

    public static void sendSecureAlert(AlertContext context) { //TODO дописать это. Сделаю это сам (не выполнять)
        JsonObject object = new JsonObject();

        ArrayList<String> extra = new ArrayList<>();

        extra.add(context.playerName != null ? context.playerName : "Unknown player");

        sendApiRequest(object, extra.toArray(new String[0]));
    }
}
