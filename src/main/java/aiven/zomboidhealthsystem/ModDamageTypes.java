package aiven.zomboidhealthsystem;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {

    private ModDamageTypes() {

    }

    public static final ResourceKey<DamageType> BLEEDING = register("bleeding");
    public static final ResourceKey<DamageType> COLD = register("cold");
    public static final ResourceKey<DamageType> DROWSINESS = register("drowsiness");
    public static final ResourceKey<DamageType> THIRST = register("thirst");
    public static final ResourceKey<DamageType> HYPOTHERMIA = register("hypothermia");
    public static final ResourceKey<DamageType> HYPERTHERMIA = register("hyperthermia");
    public static final ResourceKey<DamageType> SEVERE_DAMAGE = register("severe_damage");
    public static final ResourceKey<DamageType> HUNGER = register("hunger");
    public static final ResourceKey<DamageType> ZOMBIFICATION = register("zombification");

    private static ResourceKey<DamageType> register(String id){
        return ResourceKey.create(Registries.DAMAGE_TYPE, ZomboidHealthSystem.getResourceLocation(id));
    }

    public static void initialize(){}
}
