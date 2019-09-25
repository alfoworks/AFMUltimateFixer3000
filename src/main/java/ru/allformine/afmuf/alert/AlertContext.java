package ru.allformine.afmuf.alert;

import com.sun.istack.internal.NotNull;
import net.minecraft.util.math.BlockPos;

public class AlertContext {
    public String playerName;
    public String packetName;

    public BlockPos blockPos;
    public BlockPos playerPos;

    public AlertMod mod;

    public AlertContext(String playerName, String packetName, BlockPos blockPos, BlockPos playerPos, @NotNull AlertMod alertMod) {
        this.playerName = playerName;
        this.packetName = packetName;

        this.blockPos = blockPos;
        this.playerPos = playerPos;

        this.mod = alertMod;
    }
}
