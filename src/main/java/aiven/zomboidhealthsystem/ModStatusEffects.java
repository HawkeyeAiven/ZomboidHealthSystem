package aiven.zomboidhealthsystem;

import aiven.zomboidhealthsystem.foundation.effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static final StatusEffect PAIN = register(new Pain(),"moodle_icon_pain");
    public static final StatusEffect DROWSINESS = register(new Drowsiness(),"moodle_icon_drowsiness");
    public static final StatusEffect THIRST = register(new Thirst(),"moodle_icon_thirst");
    public static final StatusEffect EXHAUSTION = register(new Exhaustion(),"moodle_icon_exhaustion");
    public static final StatusEffect HYPOTHERMIA = register(new Hypothermia(),"moodle_icon_hypothermia");
    public static final StatusEffect HYPERTHERMIA = register(new Hyperthermia(),"moodle_icon_hyperthermia");
    public static final StatusEffect WIND = register(new Wind(),"moodle_icon_wind");
    public static final StatusEffect WET = register(new Wet(),"moodle_icon_wet");
    public static final StatusEffect COLD = register(new Cold(),"moodle_icon_cold");
    public static final StatusEffect COLD_WEATHER = register(new ColdWeather(), "moodle_icon_cold_weather");
    public static final StatusEffect HOT_WEATHER = register(new HotWeather(), "moodle_icon_hot_weather");
    public static final StatusEffect INJURED = register(new Injured(), "moodle_icon_injured");
    public static final StatusEffect BLEEDING = register(new Bleeding(), "moodle_icon_bleeding");

    public static void initialize(){}

    public static StatusEffect register(StatusEffect effect, String name) {
        return Registry.register(Registries.STATUS_EFFECT,new Identifier(ZomboidHealthSystem.ID,name),effect);
    }
}
