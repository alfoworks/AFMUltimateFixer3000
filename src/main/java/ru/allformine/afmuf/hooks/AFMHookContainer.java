package ru.allformine.afmuf.hooks;

import com.mrcrayfish.furniture.network.message.MessageDoorMat;
import gloomyfolken.hooklib.asm.Hook;

import javax.xml.ws.handler.MessageContext;

public class AFMHookContainer {
    @Hook
    public static void onMessage(MessageDoorMat message, MessageContext ctx) {
        System.out.println("Anal hook test.");
    }
}
