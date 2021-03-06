package dev.lambdacraft.perplayerspawns.mixin;

import dev.lambdacraft.perplayerspawns.Main;
import dev.lambdacraft.perplayerspawns.access.ServerWorldAccess;
import dev.lambdacraft.perplayerspawns.access.TACSAccess;
import dev.lambdacraft.perplayerspawns.util.ArrayInt2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldAccess {
	@Shadow @Final private Int2ObjectMap<Entity> entitiesById;

	@Shadow
	public abstract ServerChunkManager getChunkManager();

	@Override
	public void updatePlayerMobTypeMapFromWorld() {
		for (Entity entity : this.entitiesById.values()) {
			boolean isMobEntity = entity instanceof MobEntity;
			if (isMobEntity) {
				MobEntity mobEntity = (MobEntity) entity;
				if (mobEntity.isPersistent() && mobEntity.cannotDespawn()) continue;
			}

			EntityCategory category = entity.getType().getCategory();
			if (category != EntityCategory.MISC && this.getChunkManager().method_20727(entity)) {
				// Update player counts
				((TACSAccess) this.getChunkManager().threadedAnvilChunkStorage).updatePlayerMobTypeMap(entity);
			}
		}
	}
}
