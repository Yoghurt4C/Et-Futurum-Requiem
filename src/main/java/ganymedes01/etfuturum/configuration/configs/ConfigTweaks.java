package ganymedes01.etfuturum.configuration.configs;

import java.io.File;

import ganymedes01.etfuturum.configuration.ConfigBase;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class ConfigTweaks extends ConfigBase {

	public static boolean dyableShulkers;

	public static boolean enableOldGravel;
	public static boolean enableRoses;
	
	public static boolean shulkersSpawnAnywhere;
	public static boolean spawnAnywhereShulkerColors;
	public static boolean deepslateReplacesCobblestone;
	
	public static final String catAbandoned = "abandoned ideas";
	public static final String catCustomTweaks = "custom tweaks";
	public static final String catBedrockParity = "bedrock parity";

	public static final String PATH = configDir + File.separator + "tweaks.cfg";
	public static final ConfigTweaks configInstance = new ConfigTweaks(new File(Launch.minecraftHome, PATH));
	
	public ConfigTweaks(File file) {
		super(file);
		setCategoryComment(catBedrockParity, "Features that differ from Bedrock Edition in one way or another, or new content that isn't in Java Edition at all.");
		setCategoryComment(catAbandoned, "Scrapped concepts, abandoned ideas, old versions of changed content, etc.");
		setCategoryComment(catCustomTweaks, "Tweaks made that are original and not vanilla in any way.");
		
		configCats.add(getCategory(catBedrockParity));
		configCats.add(getCategory(catAbandoned));
	}

	@Override
	protected void syncConfigOptions() {
		Configuration cfg = configInstance;

		dyableShulkers = cfg.getBoolean("dyableShulkers", catBedrockParity, true, "Clicking a Shulker to dye them. As an added bonus, you can also click them with a water bucket (water is not consumed) or pour water on them to remove the dye.");
		
		enableRoses = cfg.getBoolean("enableOldRoses", catAbandoned, true, "");
		enableOldGravel = cfg.getBoolean("enableOldGravel", catAbandoned, true, "");
		
		shulkersSpawnAnywhere = cfg.getBoolean("shulkersSpawnAnywhere", catCustomTweaks, false, "For compatibility reasons, you may want the Shulker to spawn anywhere in the End in random groups like Endermen. These are uncommon.\nShulkers spawned in this way will despawn naturally, unless seated, given armor through a dispenser, or name tagged. Right now Shulkers are otherwise inacessible.");
		spawnAnywhereShulkerColors =  cfg.getBoolean("spawnAnywhereShulkerColors", catCustomTweaks, true, "If spawn anywhere is enabled, spawn Shulkers matching the color of modded biome blocks. Currently supports Enderlicious and Hardcore Ender Expansion terrain blocks.");
		deepslateReplacesCobblestone = cfg.getBoolean("deepslateReplacesCobblestone", catCustomTweaks, false, "If you want cobblestone to be replaced with cobbled deepslate during world generation.");
	}

}
