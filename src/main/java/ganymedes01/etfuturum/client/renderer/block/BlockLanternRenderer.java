package ganymedes01.etfuturum.client.renderer.block;

import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockLanternRenderer extends BlockModelBase {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		{
			int i = world.getBlockMetadata(x, y, z);
			float yoffset = i % 2 == 0 ? 0 : 0.0625F;
			renderer.setRenderBounds(0, .4375F, 0, .375F, .875F, .375F);
			renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);

			renderer.uvRotateTop = 1;
			renderer.uvRotateBottom = 2;
			renderer.setRenderBounds(.0625F, 0, 0, .4375F, .4375F, .375F);
			renderFaceYPos(renderer, block, x, y, z, .25F, yoffset, .3125F);
			renderFaceYNeg(renderer, block, x, y, z, .25F, yoffset, .3125F);
			renderer.setRenderBounds(.125F, .5625F, .0625F, .375F, .5625F, .3125F);
			renderFaceYPos(renderer, block, x, y, z, .25F,  + yoffset, .3125F);
			renderer.uvRotateBottom = 0;
			renderer.uvRotateTop = 0;
			
			renderer.setRenderBounds(.0625F, .875F, .0625F, .3125F, 1, .3125F);
			renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
			
			IIcon iicon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
			Tessellator tessellator = Tessellator.instance;
			float r = 0.0625F;
			
			//Lantern chain
			double iIntU8 = iicon.getInterpolatedU(11);
			double iIntV8 = iicon.getInterpolatedV(10);
			
			double iIntU9 = iicon.getInterpolatedU(14);
			double iIntV9 = iicon.getInterpolatedV(12);
			
			tessellator.addVertexWithUV(x + (r * 7D), y + (r * (9D + i)), z + (r * 7D), iIntU8, iIntV9);
			tessellator.addVertexWithUV(x + (r * 7D), y + (r * (11D + i)), z + (r * 7D), iIntU8, iIntV8);
			tessellator.addVertexWithUV(x + (r * 9D), y + (r * (11D + i)), z + (r * 9D), iIntU9, iIntV8);
			tessellator.addVertexWithUV(x + (r * 9D), y + (r * (9D + i)),  z + (r * 9D), iIntU9, iIntV9);

			tessellator.addVertexWithUV(x + (r * 9D), y + (r * (9D + i)), z + (r * 9D), iIntU8, iIntV9);
			tessellator.addVertexWithUV(x + (r * 9D), y + (r * (11D + i)), z + (r * 9D), iIntU8, iIntV8);
			tessellator.addVertexWithUV(x + (r * 7D), y + (r * (11D + i)), z + (r * 7D), iIntU9, iIntV8);
			tessellator.addVertexWithUV(x + (r * 7D), y + (r * (9D + i)),  z + (r * 7D), iIntU9, iIntV9);

			//If meta is not 0, the other parts of the chain will appear
			if (world.getBlockMetadata(x, y, z) == 0) {
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 9D), z + (r * 9D), iIntU8, iIntV9);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 11D), z + (r * 9D), iIntU8, iIntV8);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 11D), z + (r * 7D), iIntU9, iIntV8);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 9D),  z + (r * 7D), iIntU9, iIntV9);

				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 9D), z + (r * 7D), iIntU8, iIntV9);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 11D), z + (r * 7D), iIntU8, iIntV8);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 11D), z + (r * 9D), iIntU9, iIntV8);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 9D),  z + (r * 9D), iIntU9, iIntV9);
			} else {
				double iIntU10 = iicon.getInterpolatedU(11);
				double iIntV10 = iicon.getInterpolatedV(1);
				
				double iIntU11 = iicon.getInterpolatedU(14);
				double iIntV11 = iicon.getInterpolatedV(5);
				
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 11D), z + (r * 7D), iIntU10, iIntV11);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 15D), z + (r * 7D), iIntU10, iIntV10);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 15D), z + (r * 9D), iIntU11, iIntV10);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 11D),  z + (r * 9D), iIntU11, iIntV11);

				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 11D), z + (r * 9D), iIntU10, iIntV11);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 15D), z + (r * 9D), iIntU10, iIntV10);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 15D), z + (r * 7D), iIntU11, iIntV10);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 11D),  z + (r * 7D), iIntU11, iIntV11);

				double iIntU12 = iicon.getInterpolatedU(11);
				double iIntV12 = iicon.getInterpolatedV(6);
				
				double iIntU13 = iicon.getInterpolatedU(14);
				double iIntV13 = iicon.getInterpolatedV(8);
				
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 14D), z + (r * 7D), iIntU12, iIntV13);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 16D), z + (r * 7D), iIntU12, iIntV12);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 16D), z + (r * 9D), iIntU13, iIntV12);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 14D),  z + (r * 9D), iIntU13, iIntV13);

				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 14D), z + (r * 9D), iIntU12, iIntV13);
				tessellator.addVertexWithUV(x + (r * 9D), y + (r * 16D), z + (r * 9D), iIntU12, iIntV12);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 16D), z + (r * 7D), iIntU13, iIntV12);
				tessellator.addVertexWithUV(x + (r * 7D), y + (r * 14D),  z + (r * 7D), iIntU13, iIntV13);
			}
			
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return RenderIDs.LANTERN;
	}
}
