/*
 * Copyright 2015 Lukas Tenbrink
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTTagLists
{
    public static List<INBTBase> nbtBases(NBTTagList nbt)
    {
        return IntStream.range(0, nbt.tagCount()).mapToObj(nbt::get).collect(Collectors.toList());
    }

    public static void writeTo(NBTTagCompound compound, String key, List<? extends INBTBase> lists)
    {
        compound.setTag(key, write(lists));
    }

    public static NBTTagList write(List<? extends INBTBase> lists)
    {
        NBTTagList list = new NBTTagList();
        lists.forEach(list::appendTag);
        return list;
    }

    public static List<NBTTagCompound> compoundsFrom(NBTTagCompound compound, String key)
    {
        return compounds(compound.getTagList(key, Constants.NBT.TAG_COMPOUND));
    }

    public static List<NBTTagCompound> compounds(final NBTTagList nbt)
    {
        return IntStream.range(0, nbt.tagCount()).mapToObj(nbt::getCompoundTagAt).collect(Collectors.toList());
    }

    @Deprecated
    public static void writeCompoundsTo(NBTTagCompound compound, String key, List<NBTTagCompound> list)
    {
        compound.setTag(key, writeCompounds(list));
    }

    @Deprecated
    public static NBTTagList writeCompounds(List<NBTTagCompound> list)
    {
        return write(list);
    }

    public static List<int[]> intArraysFrom(NBTTagCompound compound, String key)
    {
        return intArrays(compound.getTagList(key, Constants.NBT.TAG_INT_ARRAY));
    }

    public static List<int[]> intArrays(final NBTTagList nbt)
    {
        return IntStream.range(0, nbt.tagCount()).mapToObj(nbt::getIntArray).collect(Collectors.toList());
    }

    public static void writeIntArraysTo(NBTTagCompound compound, String key, List<int[]> list)
    {
        compound.setTag(key, writeIntArrays(list));
    }

    public static NBTTagList writeIntArrays(List<int[]> list)
    {
        NBTTagList tagList = new NBTTagList();
        list.forEach(array -> tagList.appendTag(new NBTTagIntArray(array)));
        return tagList;
    }

    public static List<NBTTagList> listsFrom(NBTTagCompound compound, String key)
    {
        return lists(compound.getList(key, Constants.NBT.TAG_LIST));
    }

    public static List<NBTTagList> lists(NBTTagList nbt)
    {
        return (List) IntStream.range(0, nbt.tagCount()).mapToObj(i -> nbt.get(i).getId() == Constants.NBT.TAG_LIST ? nbt.get(i) : new NBTTagList()).collect(Collectors.toList());
    }
}
