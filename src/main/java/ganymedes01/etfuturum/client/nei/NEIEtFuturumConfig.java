package ganymedes01.etfuturum.client.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.item.ItemStack;

public class NEIEtFuturumConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		if (ConfigBlocksItems.enableBanners) {
			API.registerRecipeHandler(new BannerPatternHandler());
			API.registerUsageHandler(new BannerPatternHandler());
		}

		if (ConfigBlocksItems.enableBeetroot) {
			API.hideItem(new ItemStack(ModBlocks.beetroot));
		}
	}

	@Override
	public String getName() {
		return Reference.MOD_NAME;
	}

	@Override
	public String getVersion() {
		return Reference.VERSION_NUMBER;
	}
}