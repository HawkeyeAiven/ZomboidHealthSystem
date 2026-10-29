package aiven.zomboidhealthsystem.mixin.mixin.client;

import aiven.zomboidhealthsystem.mixin.mixin.PlayerMixin;

import net.minecraft.client.player.AbstractClientPlayer;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public abstract class AbstractClientPlayerMixin extends PlayerMixin {
}
