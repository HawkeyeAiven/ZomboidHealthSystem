package aiven.zomboidhealthsystem;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BLEEDING = register("bleeding");
    public static final RegistryKey<DamageType> COLD = register("cold");
    public static final RegistryKey<DamageType> DROWSINESS = register("drowsiness");
    public static final RegistryKey<DamageType> THIRST = register("thirst");
    public static final RegistryKey<DamageType> HYPOTHERMIA = register("hypothermia");
    public static final RegistryKey<DamageType> HYPERTHERMIA = register("hyperthermia");
    public static final RegistryKey<DamageType> SEVERE_DAMAGE = register("severe_damage");

    private static RegistryKey<DamageType> register(String name){
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(ZomboidHealthSystem.ID, name));
    }

    public static void initialize(){}
}
