package aiven.zomboidhealthsystem.foundation.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public abstract class ModStatusEffect extends StatusEffect {
    protected ModStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xe9b8b3);
    }
}
