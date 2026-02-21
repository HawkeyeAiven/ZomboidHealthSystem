package aiven.zomboidhealthsystem.foundation.mixin;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.world.ModServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = ServerPlayerEntity.class, priority = 10000)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

	@Shadow @Final public MinecraftServer server;

	@Shadow private int joinInvulnerabilityTicks;

	@Shadow protected abstract boolean isPvpEnabled();

	@Shadow public abstract boolean shouldDamagePlayer(PlayerEntity player);

	@Unique
	private ServerPlayerEntity toServerPlayerEntity() {
		return (ServerPlayerEntity) ((Object) this);
	}

	/**
	 * @author Yakui the maid
	 * @reason Chikoi the maid
	 */
	@Overwrite
	public boolean damage(DamageSource source, float amount) throws IOException {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			boolean bl = this.server.isDedicated() && this.isPvpEnabled() && source.isIn(DamageTypeTags.IS_FALL);
			if (!bl && this.joinInvulnerabilityTicks > 0 && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return false;
			} else {
				Entity entity = source.getAttacker();
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)entity;
					if (!this.shouldDamagePlayer(playerEntity)) {
						return false;
					}
				}

				if (entity instanceof PersistentProjectileEntity) {
					PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)entity;
					Entity entity2 = persistentProjectileEntity.getOwner();
					if (entity2 instanceof PlayerEntity) {
						PlayerEntity playerEntity2 = (PlayerEntity)entity2;
						if (!this.shouldDamagePlayer(playerEntity2)) {
							return false;
						}
					}
				}

				return super.damage(source, amount);
			}
		}
	}

	@Unique
	int ticks = 0;
	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo ci) {
		if(getModHealth() == null) {
			this.modHealth = new Health(toPlayerEntity());
			ModServer.registerPlayer(toPlayerEntity(), getModHealth());
		}
		if (ticks++ > ZomboidHealthSystem.UPDATE_FREQUENCY - 1) {
			sendPacketHealth();
			ticks = 0;
		}
		Health health = getModHealth();
		if(health != null) {
			health.tick();
		}
	}
}