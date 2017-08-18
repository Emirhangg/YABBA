package com.latmod.yabba.client;

import com.feed_the_beast.ftbl.lib.client.ModelBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BakedBarrelItemModel extends ModelBase
{
	private final List<BakedQuad> quads;

	public BakedBarrelItemModel(TextureAtlasSprite p, List<BakedQuad> q)
	{
		super(p);
		quads = q;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		return quads;
	}
}