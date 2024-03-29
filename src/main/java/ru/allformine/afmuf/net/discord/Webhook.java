package ru.allformine.afmuf.net.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.allformine.afmuf.References;
import ru.allformine.afmuf.alert.AlertContext;
import ru.allformine.afmuf.net.Requests;

public class Webhook {
    private static void sendApiRequest(JsonObject object, String[] extra) {
        object.addProperty("server_id", References.server_id);
        object.addProperty("type", "PACKETHACK_USAGE_DETECTED");
        object.addProperty("group", "secalert");
        object.add("arguments", arrayToJson(extra));
        final String json = object.toString();
        Requests.sendPostJSON(json, "https://hooks.alfo.ws/servers");
    }

    private static JsonArray arrayToJson(String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (String s : array) {
            jsonArray.add(s);
        }
        return jsonArray;
    }

    public static void sendSecureAlert(AlertContext context) {
        JsonObject object = new JsonObject();

        sendApiRequest(object, new String[]{context.playerName, context.mod.getModName(), context.packetName, context.getExtraInfo()});
    }
}
