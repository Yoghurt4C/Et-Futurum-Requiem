package ganymedes01.etfuturum.world.generate;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.helpers.BlockState;
import ganymedes01.etfuturum.entities.EntityShulker;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;

public abstract class NBTStructure {
	//TODO: Add crashes when the wrong args are used
	//Should trigger whenever the palette count does not match the map entry count, etc to prevent misuse and reduce possibility of user error

	private final NBTTagCompound compound;
	private final Map<Integer, BlockState>[] palettes = new Map[4];
	private final Map<BlockPos, BlockState>[] buildMaps = new Map[4]; //For storing the results of iterating over the palette and BlockState coords
	private final float theIntegrity;
	private final BlockPos[] sizes;
	private final String location;

	public NBTStructure(String loc) {
		this(loc, 1);
	}

	public NBTStructure(String loc, float integrity) {
		try {
			InputStream file = EtFuturum.class.getResourceAsStream(loc);
			compound = CompressedStreamTools.readCompressed(file);
			file.close();
		} catch (Exception e) {
			Logger.error("Failed to find or read structure NBT file for " + loc);
			throw new RuntimeException(e);
		}
		location = loc;
		theIntegrity = integrity;
		BlockPos size = getPosFromTagList(compound.getTagList("size", 3));
		sizes = new BlockPos[] {size, new BlockPos(size.getZ(), size.getY(), size.getX())}; //sizes[0] is north and south, [1] (z/x flipped) is east/west
		init();
		createPalettes();
		createBuildMaps();
	}

	/**
	 * Builds the structure map, stores it in the buildMaps field
	 */
	private void createBuildMaps() {
		for(int facing = 0; facing < 4; facing++) {
			Map<BlockPos, BlockState> map = new HashMap<>();
			Map<BlockPos, String> structureBlocks = new HashMap<>();
			NBTTagList list = getCompound().getTagList("blocks", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound comp = list.getCompoundTagAt(i);
				BlockPos pos = getListPosFromFacing(comp, facing);
				BlockState state = getPalettes()[facing].get(comp.getInteger("state"));
				boolean isStructureBlock = false;
				if(comp.hasKey("nbt", 10)) {
					if(comp.getCompoundTag("nbt").getString("id").equals("minecraft:structure_block")) {
						//Save it for later, some structure blocks check a block below them so we want the whole map built, so we can check what's below it.
						//If a structure block is in a structure file it only really stores a string, so we just save its string.
						isStructureBlock = true;
						structureBlocks.put(pos, comp.getCompoundTag("nbt").getString("metadata"));
					} else if(state.getType() == BlockState.BlockStateType.BLOCK_ENTITY) {
						setNBTAction(pos, state, comp.getCompoundTag("nbt"), getFacingFromInt(facing));
					}
				}

				//Structure blocks don't exist in 1.7.10 and should not generate naturally anyways.
				if(!isStructureBlock) {
					map.put(pos, state);
				}
			}
			for(Map.Entry<BlockPos, String> entry : structureBlocks.entrySet()) {
				BlockState belowState = entry.getKey().getY() <= 0 ? null : map.get(entry.getKey().down());
				BlockState state = setStructureBlockAction(entry.getKey(), belowState, entry.getValue(), getFacingFromInt(facing));
				if(state != null) {
					map.put(entry.getKey(), state);
				}
			}
			buildMaps[facing] = map; //Add this to the structure map for this facing direction
		}
	}

	/**
	 * Fires whenever a block has NBT stored in it. Specify what you want to set the NBT to.
	 * By default we return null. This is because many block entities use entirely different NBT tags in modern versions.
	 * Additionally, inventory data won't work from modern versions. So you have to specify what to make the NBT yourself.
	 *
	 * You can use state.setCompound() to give the BlockState an NBT tag. Fair warning, the "compound" arg gives the NBT compound from the modern version of the block.
	 * So if you just used state.setCompound(compound) do not expect it to work at all, it exists as a reference to build your own NBT from.
	 *
	 * Do NOT set the X, Y, or Z coords for the block. The game will do it for you.
	 * The structure can generate anywhere, you don't know the world coords of this block yet.
	 *
	 * null = We don't set anything for this block, the game will just create the default NBT for the block.
	 *
	 * @param pos The position (relative to the structure) of the block. This is a structure pos, NOT the world pos!
	 * @param state The BlockState (block/meta)
	 * @param compound What the modern NBT tag for this block contains.
	 * @param facing Which direction the structure is rotated towards.
	 */
	public void setNBTAction(BlockPos pos, BlockState state, NBTTagCompound compound, ForgeDirection facing) {
	}

	/**
	 * Fires for all of the locations of a structure block
	 * Structure blocks are placed around structures in "data" mode with strings to tell what they are.
	 * This includes things such as them being placed above chests to indicate they have a loot table.
	 * They're also used a lot instead of the "entities" list so the entity NBT doesn't have to be stored over and over.
	 *
	 * For example, end cities use them with the string "Chest" above chests to indicate their loot chests. So you'd set below.
	 * End cities also use them with the string "Sentry" to determine where the Shulkers are, and "Elytra" to determine where the elytra item frame should be.
	 * However igloos use the entity list tag to store its entities instead, and ruined portal chests do not use structure blocks.
	 *
	 * Do NOT set the X, Y, or Z coords for below's NBT, if you give it NBT. The game will do it for you.
	 * The structure can generate anywhere, you don't know the world coords of this block yet.
	 *
	 * null = We don't set anything for this block, the game will just create the default NBT for the block.
	 *
	 * @param pos The position (relative to the structure) of this structure block. This is a structure pos, NOT the world pos!
	 * @param below The BlockState (block/meta) below this one. Its nbt could be null, or the value itself may also be null so be careful.
	 *              This will only return BlockState types of BLOCK_ENTITY, so you can be safe in giving them NBT.
	 *              However you'll need to make sure that block entity has an IInventory before attempting to give it a loot table (chests, dispensers, etc)
	 * @param data What string did this structure block have written inside
	 * @param facing Which direction the structure is rotated towards.
	 * @return If you want this location to have a BlockState (remember BlockState can store an entity too)
	 */
	public BlockState setStructureBlockAction(BlockPos pos, BlockState below, String data, ForgeDirection facing) {
		return new BlockState(Blocks.air);
	}

	private BlockPos getListPosFromFacing(NBTTagCompound comp, int facing) {
		BlockPos size = getSize(getFacingFromInt(facing));
		BlockPos pos = getPosFromTagList(comp.getTagList("pos", 3));
		if(facing == 1) { //South
			pos = new BlockPos(size.getX() - pos.getX(), pos.getY(), size.getZ() - pos.getZ());
		} else if (facing == 2) { //West
			pos = new BlockPos(pos.getZ(), pos.getY(), pos.getX());
		} else if (facing == 3) { //East
			pos = new BlockPos(size.getX() - pos.getZ(), pos.getY(), size.getZ() - pos.getX());
		}
		return pos;
	}

	public final  NBTTagCompound getCompound() {
		return compound;
	}

	public final  Map<Integer, BlockState>[] getPalettes() {
		return palettes;
	}

	public final void buildStructure(World world, Random rand, int x, int y, int z) {
		buildStructure(world, rand, x, y, z, getFacingFromInt(rand.nextInt(4)));
	}
	public final boolean buildStructure(World world, Random rand, int x, int y, int z, ForgeDirection facing) {
		Map<BlockPos, BlockState> blockSet = buildMaps[getIntFromFacing(facing)];
		Map<BlockPos, BlockState> entitySet = new HashMap<>(); //Yes entities are stored in the BlockState class for simplicity because I don't want the map to use Object...
		for(Map.Entry<BlockPos, BlockState> entry : blockSet.entrySet()) {
			if (entry.getValue().getType() == BlockState.BlockStateType.ENTITY) { //We want to do entities last
				entitySet.put(entry.getKey(), entry.getValue());
				continue;
			}
			BlockPos pos = entry.getKey();
			setBlockState(world, rand, pos.getX() + x, pos.getY() + y, pos.getZ() + z, entry.getValue());
		}
		for(Map.Entry<BlockPos, BlockState> entry : entitySet.entrySet()) {
			Entity entity = entry.getValue().createNewEntity(world);
			BlockPos pos = entry.getKey();
			entity.readFromNBT(entry.getValue().getCompound());
			entity.setPosition(pos.getX() + x + 0.5D, pos.getY() + y, pos.getZ() + z + 0.5D);
			world.spawnEntityInWorld(entity);
			Logger.info(entity.posX + " " + entity.posY + " " + entity.posZ + " " + world.getBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ).getUnlocalizedName());
			if(entity instanceof EntityShulker) {
				Logger.info(((EntityShulker) entity).getAttachmentPos().toString());
			}
		}
		return true;
	}

	private void setBlockState(World world, Random rand, int x, int y, int z, BlockState state) {
		BlockState newState = getBlockState(world, rand, x, y, z, state);
		if(newState != null) {
			world.setBlock(x, y, z, state.getBlock(), state.getMeta(), 3);
			if (state.getType() == BlockState.BlockStateType.BLOCK_ENTITY) {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te != null) {
					if (state.getCompound() != null) {
						state.getCompound().setInteger("x", x);
						state.getCompound().setInteger("y", y);
						state.getCompound().setInteger("z", z);
						te.readFromNBT(state.getCompound());
					}
					if (te instanceof IInventory && state.getLootTable() != null) {
						WeightedRandomChestContent.generateChestContents(world.rand, state.getLootTable().getItems(world.rand), (IInventory) te, state.getLootTable().getCount(world.rand));
					}
				}
			}
		}
	}

	/**
	 * Override if this block should turn into something else when placing itself.
	 * null = nothing, don't use Blocks.air
	 *
	 *
	 */
	public BlockState getBlockState(World world, Random rand, int x, int y, int z, BlockState state) {
		return getIntegrity() == 1 || getIntegrity() > rand.nextFloat() ? state : null;
	}

	public final BlockPos getSize(ForgeDirection facing) {
		if(facing.ordinal() < 2 || facing.ordinal() > 5) {
			throw new IllegalArgumentException("ForgeDirection object must be NORTH, SOUTH, EAST or WEST!");
		}
		return facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH ? sizes[0] : sizes[1];
	}

	public final NBTTagCompound getPaletteEntryFromIndex(int i) {
		return getCompound().getTagList("palette", 10).getCompoundTagAt(i);
	}

	public final String getBlockNamespaceFromPaletteEntry(int i) {
		return getPaletteEntryFromIndex(i).getString("Name");
	}

	public final int getPaletteCount() {
		return getCompound().getTagList("palette", 10).tagCount();
	}

	public final Set<Pair<Integer, NBTTagCompound>> getPaletteNBT() {
		Set<Pair<Integer, NBTTagCompound>> set = new HashSet<>();
		for(int i = 0; i < getPaletteCount(); i++) {
			set.add(new ImmutablePair<>(i, getPaletteEntryFromIndex(i)));
		}
		return set;
	}

	public final Map<String, String> getPropertiesFromIndex(int index) {
		return getProperties(getPaletteEntryFromIndex(index));
	}

	public final Map<String, String> getProperties(NBTTagCompound compound) {
		Map<String, String> map = new HashMap<>();
		for(Map.Entry<String, NBTBase> props : (Set<Map.Entry<String, NBTBase>>)compound.getCompoundTag("Properties").tagMap.entrySet()) {
			if(props.getValue().getId() == 8) {
				map.put(props.getKey(), ((NBTTagString)props.getValue()).func_150285_a_());
			}
		}
		return map;
	}

	/**
	 * Gets the namespace ID from a palette entry
	 */
	public final String getBlockID(int index) {
		return getPaletteEntryFromIndex(index).getString("Name");
	}

	/**
	 * Gets a properties from the "properties" portion of a BlockState palette entry in structure NBT
	 * Returns NULL if the block has no state properties.
	 */
	public final String getBlockProperty(NBTTagCompound nbt, String property) {
		if (!nbt.hasKey("properties")) return null;
		return nbt.getCompoundTag("properties").getString(property);
	}

	/**
	 * Used for converting the NBTTagLists found in structure NBT that contain exactly 3 ints, to a BlockPos.
	 * This should ONLY be used on NBTTagLists that contain no more or less than 3 integers.
	 */
	public final BlockPos getPosFromTagList(NBTTagList list) {
		if(list.tagCount() != 3 && list.getId() != 3) {
			throw new IllegalArgumentException("This is not a BlockPos taglist!");
		}
		return new BlockPos(getIntFromTagList(list, 0), getIntFromTagList(list, 1), getIntFromTagList(list, 2));
	}

	protected int getIntFromTagList(NBTTagList list, int index) {
		if (index >= 0 && index < list.tagCount())
		{
			NBTBase nbtbase = (NBTBase)list.tagList.get(index);
			return nbtbase.getId() == 3 ? ((NBTTagInt) nbtbase).func_150287_d() : 0;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * 0 = North (-z)
	 * 1 = South (+z)
	 * 2 = West (-x)
	 * 3 = East (+x)
	 */
	public final ForgeDirection getFacingFromInt(int i) {
		if(i < 0 || i > 3) {
			throw new IllegalArgumentException("Facing int must be at least 0 and at most 3!");
		}
		return ForgeDirection.values()[i + 2];
	}

	public final int getIntFromFacing(ForgeDirection dir) {
		if(dir.ordinal() < 2 || dir.ordinal() > 5) {
			throw new IllegalArgumentException("ForgeDirection object for structure must be NORTH, SOUTH, EAST or WEST!");
		}
		return dir.ordinal() - 2;
	}

	private void createPalettes() {
		palettes[0] = createPalette(ForgeDirection.NORTH);
		palettes[1] = createPalette(ForgeDirection.SOUTH);
		palettes[2] = createPalette(ForgeDirection.WEST);
		palettes[3] = createPalette(ForgeDirection.EAST);
	}

	public final String getLocation() {
		return location;
	}

	public final float getIntegrity() {
		return theIntegrity;
	}

	/**
	 * Ran four times for each direction. This populates four different versions of the palette for the different rotations.
	 *
	 * NOTE: If the palette entry is minecraft:structure_block it doesn't matter what you put here, it's handled later because they store special data.
	 * I recommend you don't map it to anything, or just set it to null as it won't make a difference.
	 * Override setStructureBlockAction if you want something to appear where a BlockState is.
	 *
	 * Also don't set NBT here just yet, the NBT is stored in the block map itself so if you want to give one of your block entities NBT, override that function instead.
	 */
	public abstract Map<Integer, BlockState> createPalette(ForgeDirection facing);

	/**
	 * Override this if you want code to run before the maps and palettes are built.
	 */
	public void init() {}
}
