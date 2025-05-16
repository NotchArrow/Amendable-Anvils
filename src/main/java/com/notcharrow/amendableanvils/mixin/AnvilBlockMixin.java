package com.notcharrow.amendableanvils.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AnvilBlockMixin {
	@Inject(at = @At("HEAD"), method = "onBlockBreakStart")
	private void init(BlockState state, World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
		if (player.getMainHandStack().getItem() == Items.IRON_INGOT && state.getBlock() != Blocks.ANVIL
		&& state.getBlock() instanceof AnvilBlock) {
			Direction direction  = state.get(Properties.HORIZONTAL_FACING);
			BlockRotation rotation = switch (direction) {
				case EAST  -> BlockRotation.CLOCKWISE_90;
				case SOUTH -> BlockRotation.CLOCKWISE_180;
				case WEST  -> BlockRotation.COUNTERCLOCKWISE_90;
				default    -> BlockRotation.NONE;
			};

			if (state.getBlock() == Blocks.CHIPPED_ANVIL) {
				world.setBlockState(pos, Blocks.ANVIL.getDefaultState().rotate(rotation));
			} else if (state.getBlock() == Blocks.DAMAGED_ANVIL) {
				world.setBlockState(pos, Blocks.CHIPPED_ANVIL.getDefaultState().rotate(rotation));
			}
			world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
			player.getMainHandStack().setCount(player.getMainHandStack().getCount() - 1);
		}
	}
}