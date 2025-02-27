package de.siphalor.mousewheelie.client.mixin;

import de.siphalor.mousewheelie.client.Config;
import de.siphalor.mousewheelie.client.util.inventory.SlotRefiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Shadow public ClientPlayerEntity player;
	private ItemStack mouseWheelie_mainHandStack;
	private ItemStack mouseWheelie_offHandStack;

	@Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"))
	public void onItemUse(CallbackInfo callbackInfo) {
		if(Config.otherRefill.value) {
			mouseWheelie_mainHandStack = player.getMainHandStack();
			mouseWheelie_mainHandStack = mouseWheelie_mainHandStack.isEmpty() ? null : mouseWheelie_mainHandStack.copy();
			mouseWheelie_offHandStack = player.getOffHandStack();
			mouseWheelie_offHandStack = mouseWheelie_offHandStack.isEmpty() ? null : mouseWheelie_offHandStack.copy();
		}
	}

	@Inject(method = "doItemUse", at = @At("RETURN"))
	public void onItemUsed(CallbackInfo callbackInfo) {
		if(mouseWheelie_mainHandStack != null) {
			if(player.getMainHandStack().isEmpty()) {
				SlotRefiller.set(player.inventory, mouseWheelie_mainHandStack);
				SlotRefiller.refill();
			}
			mouseWheelie_mainHandStack = null;
		}
		if(mouseWheelie_offHandStack != null) {
			if(player.getMainHandStack().isEmpty()) {
				SlotRefiller.set(player.inventory, mouseWheelie_offHandStack);
				SlotRefiller.refill();
			}
			mouseWheelie_offHandStack = null;
		}
	}
}
