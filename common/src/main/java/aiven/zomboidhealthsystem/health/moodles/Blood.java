package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;

public final class Blood extends ImportantMoodle {

    public Blood(Health health) {
        super(health);
    }

    @Override
    public float maxValue() {
        return 100.0F;
    }

    @Override
    protected float defaultValue() {
        return maxValue();
    }

    @Override
    public String getId() {
        return "blood";
    }

    @Override
    public boolean canPlayerLive() {
        return getValue() > minValue();
    }

    @Override
    protected void _update(float tickDelta) {
        if(tickDelta != 0) {
            if (this.getPlayer().hasEffect(MobEffects.REGENERATION)) {
                int amplifier = this.getPlayer().getEffect(MobEffects.REGENERATION).getAmplifier();
                this.addValue(((float) amplifier + 1) / 100.0F * tickDelta);
            }
            if (health.getHunger().getValue() < 2) {
                addValue(0.003F * (health.getHunger().getValue() < 0 ? 1.25F : 1.0F) * tickDelta);
            }

            health.getExhaustion().setMultiplier(this, (1.0F + ((100 - value) / 100)));
            if(value <= 25) {
                if(Util.random(tickDelta / (30 * 20))) {
                    health.addMobEffect(MobEffects.CONFUSION, 0, 15 * 20);
                }

                if(Util.random(tickDelta / (30 * 20))) {
                    health.addMobEffect(MobEffects.BLINDNESS, 0, 15 * 20);
                }
            }
        }
    }

    @Override
    public int getAmplifier() {
        return health.hasBleeding() ? 2 : 0;
    }

    @Override
    public void sleep(int sumTicks) {

    }

    @Override
    protected ResourceKey<DamageType> getDamageType() {
        return ModDamageTypes.BLEEDING;
    }

    @Override
    public String getMoodleIconText() {
        return Component.translatable(ZomboidHealthSystem.ID + ".health.hud.bleeding").getString();
    }
}
