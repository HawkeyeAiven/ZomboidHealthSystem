package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static final StatusEffect PAIN = register(new Pain(),"pain");
    public static final StatusEffect DROWSINESS = register(new Drowsiness(),"drowsiness");
    public static final StatusEffect THIRST = register(new Thirst(),"thirst");
    public static final StatusEffect EXHAUSTION = register(new Exhaustion(),"exhaustion");
    public static final StatusEffect HYPOTHERMIA = register(new Hypothermia(),"hypothermia");
    public static final StatusEffect HYPERTHERMIA = register(new Hyperthermia(),"hyperthermia");
    public static final StatusEffect WIND = register(new Wind(),"wind");
    public static final StatusEffect WET = register(new Wet(),"wet");
    public static final StatusEffect COLD = register(new Cold(),"cold");

    public static void initialize(){}

    public static StatusEffect register(StatusEffect effect, String name){
        return Registry.register(Registries.STATUS_EFFECT,new Identifier(ZomboidHealthSystem.ID,name),effect);
    }
}
