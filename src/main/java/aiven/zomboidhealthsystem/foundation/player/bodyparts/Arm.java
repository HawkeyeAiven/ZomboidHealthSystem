package aiven.zomboidhealthsystem.foundation.player.bodyparts;

import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.EffectAmplifiers;
import net.minecraft.entity.player.PlayerEntity;

public class Arm extends BodyPart {
    public Arm(Health health, float hp, PlayerEntity user, String name) {
        super(health, hp, user, name);
    }

    @Override
    public void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
        effectAmplifiers.fatigueAmplifier += (getMaxHp() - getHp()) / 3.0F;
        effectAmplifiers.weaknessAmplifier += (getMaxHp() - getHp()) / 3.0F;
    }

    @Override
    public float getMaxHp() {
        return Health.MAX_ARM_HP;
    }
}
