package aiven.zomboidhealthsystem.foundation.player.bodyparts;

import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.EffectAmplifiers;
import net.minecraft.entity.player.PlayerEntity;

public class Leg extends BodyPart {
    public Leg(Health health, float hp, PlayerEntity user, String name) {
        super(health, hp, user, name);
    }

    @Override
    public void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
        effectAmplifiers.slownessAmplifier += (getMaxHp() - getHp()) / 2.0F;
    }

    @Override
    public float getMaxHp() {
        return Health.MAX_LEG_HP;
    }

}
