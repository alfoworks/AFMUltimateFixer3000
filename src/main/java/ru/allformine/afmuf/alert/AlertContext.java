package ru.allformine.afmuf.alert;

import net.minecraft.util.math.BlockPos;

import java.util.StringJoiner;

public class AlertContext {
    public String playerName;
    public String packetName;

    public BlockPos blockPos;
    public String tileName;
    
    public BlockPos playerPos;

    public AlertMod mod;

    public AlertContext(String playerName, String packetName, BlockPos blockPos, BlockPos playerPos, String tileName, AlertMod alertMod) {
        this.playerName = playerName;
        this.packetName = packetName;

        this.blockPos = blockPos;
        this.tileName = tileName;
        
        this.playerPos = playerPos;

        this.mod = alertMod;
    }

    public String getExtraInfo() {
        StringJoiner info = new StringJoiner(", ");

        if (playerPos != null) {
            info.add(String.format("Player XYZ: %s %s %s", playerPos.getX(), playerPos.getY(), playerPos.getZ()));
        }

        if (tileName != null) {
            info.add(String.format("Tile name: %s", tileName));
        }

        if (blockPos != null) {
            info.add(String.format("Block XYZ: %s, %s, %s", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }

        return info.toString();
    }
}
