package ganymedes01.etfuturum.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FallingDripstoneRenderer extends Render
{
	private final RenderBlocks field_147920_a = new RenderBlocks();
	private static final String __OBFID = "CL_00000994";

	public FallingDripstoneRenderer()
	{
		this.shadowSize = 0.5F;
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(EntityFallingDripstone p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		World world = p_76986_1_.func_145807_e();
		Block block = ModBlocks.pointed_dripstone;
		int i = MathHelper.floor_double(p_76986_1_.posX);
		int j = MathHelper.floor_double(p_76986_1_.posY);
		int k = MathHelper.floor_double(p_76986_1_.posZ);

		if (block != null && block != world.getBlock(i, j, k))
		{
			GL11.glPushMatrix();
			GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
			this.bindEntityTexture(p_76986_1_);
			GL11.glDisable(GL11.GL_LIGHTING);
			Tessellator tessellator;

			this.field_147920_a.blockAccess = world;
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setTranslation((double)((float)(-i) - 0.5F), (double)((float)(-j) - 0.5F), (double)((float)(-k) - 0.5F));
			field_147920_a.setRenderBoundsFromBlock(block);
			field_147920_a.drawCrossedSquares(ModBlocks.pointed_dripstone.getIcon(0, p_76986_1_.field_145814_a), i, j, k, 1.0F);
			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityFallingBlock p_110775_1_)
	{
		return TextureMap.locationBlocksTexture;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return this.getEntityTexture((EntityFallingBlock)p_110775_1_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		this.doRender((EntityFallingDripstone)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
}
