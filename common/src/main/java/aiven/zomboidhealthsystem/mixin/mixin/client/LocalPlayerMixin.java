package aiven.zomboidhealthsystem.mixin.mixin.client;

import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {

    /**
     * @author Aiven
     * @reason Ну вот так вот
     */
    @Overwrite
    public void actuallyHurt(DamageSource damageSource, float damage) {

    }

}
