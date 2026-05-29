package com.swill.killaura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class KillAuraMod implements ClientModInitializer {
    private static boolean enabled = true;
    private static long lastAttack = 0;
    private static final Random random = new Random();

    @Override
    public void onInitializeClient() {
        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.killaura.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.killaura"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                client.player.sendMessage(Text.literal("§6[KillAura] §e" + (enabled ? "ON" : "OFF")), true);
            }

            if (!enabled) return;

            long now = System.currentTimeMillis();
            int delay = 1700 + random.nextInt(190);

            if (now - lastAttack < delay) return;

            for (var entity : client.world.getEntities()) {
                if (entity == client.player) continue;
                if (!(entity instanceof net.minecraft.entity.player.PlayerEntity)) continue;
                if (client.player.distanceTo(entity) > 4.2) continue;
                if (client.interactionManager == null) continue;

                float yaw = (float)(Math.toDegrees(Math.atan2(
                    entity.getZ() - client.player.getZ(),
                    entity.getX() - client.player.getX()
                )) - 90);
                
                float pitch = (float)(-Math.toDegrees(Math.atan2(
                    entity.getY() + entity.getHeight() / 2 - (client.player.getY() + client.player.getHeight() / 2),
                    Math.sqrt(Math.pow(entity.getX() - client.player.getX(), 2) + Math.pow(entity.getZ() - client.player.getZ(), 2))
                )));

                yaw += (random.nextFloat() - 0.5f) * 4;
                pitch += (random.nextFloat() - 0.5f) * 2;

                client.player.setYaw(yaw);
                client.player.setPitch(pitch);
                client.interactionManager.attackEntity(client.player, entity);
                client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                lastAttack = now;
                break;
            }
        });
    }
}
