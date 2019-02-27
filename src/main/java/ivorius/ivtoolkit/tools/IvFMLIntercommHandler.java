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

package ivorius.ivtoolkit.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Logger;

/**
 * Created by lukas on 07.06.14.
 */
public abstract class IvFMLIntercommHandler
{
    private Logger logger;
    private String modOwnerID;
    private Object modInstance;

    protected IvFMLIntercommHandler(Logger logger, String modOwnerID, Object modInstance)
    {
        this.logger = logger;
        this.modOwnerID = modOwnerID;
        this.modInstance = modInstance;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    public String getModOwnerID()
    {
        return modOwnerID;
    }

    public void setModOwnerID(String modOwnerID)
    {
        this.modOwnerID = modOwnerID;
    }

    public Object getModInstance()
    {
        return modInstance;
    }

    public void setModInstance(Object modInstance)
    {
        this.modInstance = modInstance;
    }

    public void handleMessages(boolean server, boolean runtime)
    {
        for (InterModComms.IMCMessage message : InterModComms.fetchRuntimeMessages(modInstance))
        {
            onIMCMessage(message, server, true);
        }
    }

    public void onIMCMessage(InterModComms.IMCMessage message, boolean server, boolean runtime)
    {
        try
        {
            boolean didHandle = handleMessage(message, server, runtime);

            if (!didHandle)
            {
                logger.warn("Could not handle message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'");
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception on message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'");
            ex.printStackTrace();
        }
    }

    protected abstract boolean handleMessage(InterModComms.IMCMessage message, boolean server, boolean runtime);

    protected boolean isMessage(String key, InterModComms.IMCMessage message, Class expectedType)
    {
        if (key.equals(message.key))
        {
            if (message.getMessageType().isAssignableFrom(expectedType))
            {
                return true;
            }

            faultyMessage(message, expectedType);
        }

        return false;
    }

    protected Entity getEntity(NBTTagCompound compound, boolean server)
    {
        return getEntity(compound, "worldID", "entityID", server);
    }

    protected Entity getEntity(NBTTagCompound compound, String worldKey, String entityKey, boolean server)
    {
        if (!server)
        {
            return Minecraft.getInstance().world.getEntityByID(compound.getInt(entityKey));
        }
        else
        {
            return Minecraft.getInstance().world.getServer().getWorld(compound.getInt(worldKey)).getEntityByID(compound.getInt(entityKey));
        }
    }

    protected boolean sendReply(InterModComms.IMCMessage message, String value)
    {
        if (message.getSender() == null)
        {
            return false;
        }

        NBTTagCompound cmp = message.getNBTValue();
        FMLInterModComms.sendRuntimeMessage(modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
        return true;
    }

    protected boolean sendReply(InterModComms.IMCMessage message, NBTTagCompound value)
    {
        if (message.getSender() == null)
        {
            return false;
        }

        NBTTagCompound cmp = message.getNBTValue();
        FMLInterModComms.sendRuntimeMessage(modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
        return true;
    }

    protected boolean sendReply(InterModComms.IMCMessage message, ItemStack value)
    {
        if (message.getSender() == null)
        {
            logger.error("Message error! Could not reply to message with key '" + message.key + "' - No sender found");
            return false;
        }

        NBTTagCompound cmp = message.getNBTValue();
        FMLInterModComms.sendRuntimeMessage(modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
        return true;
    }

    private void faultyMessage(InterModComms.IMCMessage message, Class expectedType)
    {
        logger.error("Got message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'; Expected type: '" + expectedType.getName() + "'");
    }
}

