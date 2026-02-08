package aiven.zomboidhealthsystem.foundation.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Hypothermia extends StatusEffect {
    public Hypothermia() {
        super(StatusEffectCategory.HARMFUL, 0xe9b8b3);
    }
}
