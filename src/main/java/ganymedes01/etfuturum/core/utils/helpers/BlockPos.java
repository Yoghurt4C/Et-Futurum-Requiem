package ganymedes01.etfuturum.core.utils.helpers;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPos extends Vec3i {

	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

	private static final int NUM_X_BITS = 26;
	private static final int NUM_Z_BITS = NUM_X_BITS;
	private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
	private static final int Y_SHIFT = 0 + NUM_Z_BITS;
	private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
	private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
	private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
	private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

	public BlockPos(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockPos(double x, double y, double z) {
		super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
	}

	public BlockPos(Entity source) {
		this(source.posX, source.posY, source.posZ);
	}

	public BlockPos(Vec3 source) {
		this(source.xCoord, source.yCoord, source.zCoord);
	}

	public BlockPos(Vec3i source) {
		this(source.getX(), source.getY(), source.getZ());
	}

	public BlockPos add(double x, double y, double z) {
		return new BlockPos(getX() + x, getY() + y, getZ() + z);
	}

	public BlockPos add(int x, int y, int z) {
		return new BlockPos(getX() + x, getY() + y, getZ() + z);
	}

	public BlockPos add(Vec3i vec) {
		return new BlockPos(getX() + vec.getX(), getY() + vec.getY(), getZ() + vec.getZ());
	}

	public BlockPos multiply(int factor) {
		return new BlockPos(getX() * factor, getY() * factor, getZ() * factor);
	}

	public BlockPos up() {
		return this.up(1);
	}

	public BlockPos subtract(Vec3i vec) {
		return new BlockPos(getX() - vec.getX(), getY() - vec.getY(), getZ() - vec.getZ());
	}

	public BlockPos up(int n) {
		return this.offset(EnumFacing.UP, n);
	}

	public BlockPos down() {
		return this.down(1);
	}

	public BlockPos down(int n) {
		return this.offset(EnumFacing.DOWN, n);
	}

	public BlockPos north() {
		return this.north(1);
	}

	public BlockPos north(int n) {
		return this.offset(EnumFacing.NORTH, n);
	}

	public BlockPos south() {
		return this.south(1);
	}

	public BlockPos south(int n) {
		return this.offset(EnumFacing.SOUTH, n);
	}

	public BlockPos west() {
		return this.west(1);
	}

	public BlockPos west(int n) {
		return this.offset(EnumFacing.WEST, n);
	}

	public BlockPos east() {
		return this.east(1);
	}

	public BlockPos east(int n) {
		return this.offset(EnumFacing.EAST, n);
	}

	public BlockPos offset(EnumFacing facing) {
		return this.offset(facing, 1);
	}

	public BlockPos offset(EnumFacing facing, int n) {
		return new BlockPos(getX() + facing.getFrontOffsetX() * n, getY() + facing.getFrontOffsetY() * n,
				getZ() + facing.getFrontOffsetZ() * n);
	}

	public BlockPos offset(ForgeDirection facing) {
		return this.offset(facing, 1);
	}

	public BlockPos offset(ForgeDirection facing, int n) {
		return new BlockPos(getX() + facing.offsetX * n, getY() + facing.offsetY * n, getZ() + facing.offsetZ * n);
	}

	public BlockPos crossProductBP(Vec3i vec) {
		return new BlockPos(getY() * vec.getZ() - getZ() * vec.getY(), getZ() * vec.getX() - getX() * vec.getZ(),
				getX() * vec.getY() - getY() * vec.getX());
	}

	public long toLong() {
		return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | (getZ() & Z_MASK) << 0;
	}

	public static BlockPos fromLong(long serialized) {
		int j = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
		int k = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
		int l = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
		return new BlockPos(j, k, l);
	}

	@Override
	public Vec3i crossProduct(Vec3i vec) {
		return crossProductBP(vec);
	}

	// Roadhog360 start
	public static AxisAlignedBB getBB(BlockPos pos1, BlockPos pos2) {
		return AxisAlignedBB.getBoundingBox((double) pos1.getX(), (double) pos1.getY(), (double) pos1.getZ(),
				(double) pos2.getX(), (double) pos2.getY(), (double) pos2.getZ());
	}

	public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
		return iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()),
				Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()),
				Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
	}

	public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, final int endX,
			final int endY, final int endZ) {

		return new Iterable<BlockPos>() {
			public Iterator<BlockPos> iterator() {
				return new AbstractIterator<BlockPos>() {
					private boolean field_191534_b = true;
					private int field_191535_c;
					private int field_191536_d;
					private int field_191537_e;

					protected BlockPos computeNext() {
						if (this.field_191534_b) {
							this.field_191534_b = false;
							this.field_191535_c = startX;
							this.field_191536_d = startY;
							this.field_191537_e = startZ;
							return new BlockPos(startX, startY, startZ);
						} else if (this.field_191535_c == endX && this.field_191536_d == endY
								&& this.field_191537_e == endZ) {
							return (BlockPos) this.endOfData();
						} else {
							if (this.field_191535_c < endX) {
								++this.field_191535_c;
							} else if (this.field_191536_d < endY) {
								this.field_191535_c = startX;
								++this.field_191536_d;
							} else if (this.field_191537_e < endZ) {
								this.field_191535_c = startX;
								this.field_191536_d = startY;
								++this.field_191537_e;
							}

							return new BlockPos(this.field_191535_c, this.field_191536_d, this.field_191537_e);
						}
					}
				};
			}
		};
	}
}