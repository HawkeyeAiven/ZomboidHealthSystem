package aiven.zomboidhealthsystem.foundation.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Bleeding extends StatusEffect {
    public Bleeding() {
        super(StatusEffectCategory.HARMFUL, 0xe9b8b3);
    }
}
