package ganymedes01.etfuturum.mixins;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSettings.GameType.class)
public class MixinGameType {
	private boolean etfu$isSpectator() {
		return (Object)this == SpectatorMode.SPECTATOR_GAMETYPE;
	}
	@Inject(method = "configurePlayerCapabilities", at = @At("HEAD"), cancellable = true)
	public void configureSpecCaps(PlayerCapabilities caps, CallbackInfo ci) {
		if(etfu$isSpectator()) {
			ci.cancel();
			caps.allowFlying = true;
			caps.isCreativeMode = true;
			caps.disableDamage = true;
			caps.allowEdit = false;
			caps.isFlying = true;
		}
	}
}
