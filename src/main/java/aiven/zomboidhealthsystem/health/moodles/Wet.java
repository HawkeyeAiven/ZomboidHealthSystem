package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class Wet extends SimpleMoodle {
    public Wet(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "wet";
    }

    @Override
    protected float maxValue() {
        return 3.5F;
    }

    @Override
    public void update() {
        Player player = getHealth().getPlayer();
        Level level = player.level();

        if(level.getBlockState(player.blockPosition()).getBlock().equals(Blocks.WATER) && player.getVehicle() == null){
            this.setValue(maxValue());
        } else if(level.isRainingAt(player.blockPosition())){
            if(Util.getArmorCount(getPlayer()) < 3) {
                this.addAmount(1.0F / (45 * 20) * Config.getInstance().wet_multiplier * Health.UPDATE_FREQUENCY);
            }
        } else {
            this.addAmount(-1.0F / (45 * 20) * Config.getInstance().wet_multiplier * Health.UPDATE_FREQUENCY);
        }

        if(getHealth().getTemperature().getValue() > Temperature.AVERAGE_TEMPERATURE_BODY + 1.0F && this.getValue() < 2.1F) {
            this.addAmount(0.035F / 20 * Health.UPDATE_FREQUENCY);
        }

        if(getHealth().getExhaustion().getValue() >= 1.5F && getValue() < 1.5F && !getHealth().getTemperature().isFeelingCold()) {
            this.getHealth().getWet().addAmount(1 / 500F * (Util.getArmorCount(getPlayer()) >= 3 ? 1.5F : 1.0F) * Health.UPDATE_FREQUENCY);
        }

        getHealth().getTemperature().addHeat(getValue() * -2.5F);
    }

    @Override
    public void sleep(int sumTicks) {
        addAmount(-1.0F / 400 * sumTicks);
    }
}