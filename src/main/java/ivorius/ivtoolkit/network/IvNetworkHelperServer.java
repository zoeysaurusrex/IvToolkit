/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ivorius.ivtoolkit.network;

import io.netty.channel.Channel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukas on 02.07.14.
 */
public class IvNetworkHelperServer
{
    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network, EntityPlayer player, Object... params)
    {
        if (!(player instanceof EntityPlayerMP))
            throw new UnsupportedOperationException();

        network.sendTo(PacketTileEntityData.packetEntityData(tileEntity, context, params), (EntityPlayerMP) player);
    }

    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params)
    {
        sendToPlayersWatchingChunk(tileEntity.getWorld(), tileEntity.getPos().getX() / 16, tileEntity.getPos().getZ() / 16, network, PacketTileEntityData.packetEntityData(tileEntity, context, params));
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, SimpleNetworkWrapper network, IMessage message)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            network.sendTo(message, playerMP);
        }
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Channel channel, Object message)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            sendToPlayer(channel, playerMP, message);
        }
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Packet packet)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            playerMP.connection.sendPacket(packet);
        }
    }

    public static void sendToPlayer(Channel channel, EntityPlayerMP playerMP, Object message)
    {
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
        channel.writeAndFlush(message);
    }

    public static List<EntityPlayerMP> getPlayersWatchingChunk(World world, int chunkX, int chunkZ)
    {
        if (world.isRemote || !(world instanceof WorldServer))
        {
            return Collections.emptyList();
        }

        ArrayList<EntityPlayerMP> playersWatching = new ArrayList<>();

        WorldServer server = (WorldServer) world;
        PlayerChunkMap playerManager = server.getPlayerChunkMap();

        List<EntityPlayer> players = server.playerEntities;
        List<EntityPlayerMP> mpplayers = ((List) players.stream().filter(p -> p instanceof EntityPlayerMP).collect(Collectors.toList()));

        playersWatching.addAll(mpplayers.stream().filter(player -> playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)).collect(Collectors.toList()));

        return playersWatching;
    }

    public static void sendEEPUpdatePacketToPlayer(Entity entity, String capabilityKey, EnumFacing facing, String context, SimpleNetworkWrapper network, EntityPlayer player, Object... params)
    {
        if (!(player instanceof EntityPlayerMP))
            throw new UnsupportedOperationException();

        network.sendTo(PacketEntityCapabilityData.packetEntityData(entity, capabilityKey, facing, context, params), (EntityPlayerMP) player);
    }

    public static void sendEEPUpdatePacket(Entity entity, String capabilityKey, EnumFacing facing, String context, SimpleNetworkWrapper network, Object... params)
    {
        if (entity.world.isRemote)
            throw new UnsupportedOperationException();

        for (EntityPlayer player : ((WorldServer) entity.world).getEntityTracker().getTrackingPlayers(entity))
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, player, params);

        if (entity instanceof EntityPlayerMP) // Players don't 'track' themselves
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, (EntityPlayer) entity, params);
    }
}
