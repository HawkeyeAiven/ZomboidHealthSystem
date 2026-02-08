package aiven.zomboidhealthsystem.foundation.player;

import aiven.zomboidhealthsystem.Config;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class HungerHelper {
    private float reduceAmount = 0;
    final Health health;

    public HungerHelper(Health health){
        this.health = health;
    }

    public void reduceHunger(float amount){
        reduceAmount += amount;
        if(reduceAmount >= 1){
            HungerManager hunger = health.getPlayer().getHungerManager();
            hunger.setFoodLevel(hunger.getFoodLevel() - (int) reduceAmount);
            reduceAmount = 0;
        }
    }

    public void update() {
        PlayerEntity player = health.getPlayer();
        if(player.isSleeping() && player.getHungerManager().getFoodLevel() < Config.MIN_HUNGER_FOR_SLEEP.getValue()) {
            player.wakeUp();
            player.sendMessage(Text.translatable("zomboidhealthsystem.message.cant_sleep_hungry"), true);
        }
    }

    public void onSleep() {
        this.reduceHunger(6);
    }
}
