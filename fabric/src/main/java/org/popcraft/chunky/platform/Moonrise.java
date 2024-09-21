package org.popcraft.chunky.platform;

import ca.spottedleaf.moonrise.common.util.ChunkSystem;
import ca.spottedleaf.moonrise.libs.ca.spottedleaf.concurrentutil.util.Priority;
import ca.spottedleaf.moonrise.patches.chunk_system.world.ChunkSystemServerChunkCache;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.ServerTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.*;

import java.util.concurrent.CompletableFuture;

public class Moonrise {

    public static final boolean MOONRISE_LOADED = FabricLoader.getInstance().isModLoaded("moonrise");

    public static boolean isMoonrise() {
        return MOONRISE_LOADED;
    }

    // Copied from CraftWorld.getChunkAtAsync
    public static CompletableFuture<Chunk> getChunkAtAsync(final ServerWorld world, final int x, final int z) {
        if (Thread.currentThread() == world.getServer().getThread()) {
            WorldChunk immediate = ((ChunkSystemServerChunkCache) world.getChunkManager()).moonrise$getFullChunkIfLoaded(x, z);
            if (immediate != null) {
                return CompletableFuture.completedFuture(immediate);
            }
        }

        CompletableFuture<Chunk> ret = new CompletableFuture<>();
        ChunkSystem.scheduleChunkLoad(world, x, z, true, ChunkStatus.FULL, true, Priority.NORMAL, chunk -> {
            world.getServer().send(new ServerTask(world.getServer().getTicks(), () -> {
                if (chunk != null) {
                    world.getChunkManager().addTicket(FabricWorld.CHUNKY, new ChunkPos(x, z), 0, Unit.INSTANCE);
                    ret.complete(chunk);
                }
            }));
        });

        return ret;
    }

}
