package ru.allformine.afmuf.ic2;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

class AFMIC2FakePlayer {
    static FakePlayer getFakePlayer(WorldServer world) {
        String name = "9995e7e8-1090-11ea-8d71-362b9e155667";
        return FakePlayerFactory.get(world, new GameProfile(UUID.fromString(name), "AFMIC2"));
    }
}
