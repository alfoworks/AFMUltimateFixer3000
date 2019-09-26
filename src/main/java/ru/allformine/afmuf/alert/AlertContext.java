package ru.allformine.afmuf.alert;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.StringJoiner;

public class AlertContext {
    public String playerName;
    public String packetName;

    public TileEntity tile;
    
    public BlockPos playerPos;

    public AlertMod mod;

    public AlertContext(String playerName, String packetName, BlockPos playerPos, TileEntity tile, AlertMod alertMod) {
        this.playerName = playerName;
        this.packetName = packetName;

        this.tile = tile;

        this.playerPos = playerPos;

        this.mod = alertMod;
    }

    public String getExtraInfo() {
        StringJoiner info = new StringJoiner("\n");

        if (playerPos != null) {
            info.add(String.format("Player XYZ: %s %s %s", playerPos.getX(), playerPos.getY(), playerPos.getZ()));
        }

        if (tile != null) {
            info.add(String.format("Tile info: %s", tile.toString()));
        }

        return info.toString();
    }
}
