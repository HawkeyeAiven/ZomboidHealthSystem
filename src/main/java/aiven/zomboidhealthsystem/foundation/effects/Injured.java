package aiven.zomboidhealthsystem.foundation.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Injured extends StatusEffect {
    public Injured() {
        super(StatusEffectCategory.HARMFUL, 0xe9b8b3);
    }
}
