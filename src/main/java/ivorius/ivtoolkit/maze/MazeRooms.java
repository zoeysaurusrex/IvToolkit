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

package ivorius.ivtoolkit.maze;

import com.google.common.base.Function;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

import javax.annotation.Nullable;

/**
 * Created by lukas on 13.04.15.
 */
public class MazeRooms
{
    public static Function<MazeRoom, NBTTagIntArray> toNBT()
    {
        return new Function<MazeRoom, NBTTagIntArray>()
        {
            @Nullable
            @Override
            public NBTTagIntArray apply(MazeRoom input)
            {
                return input.storeInNBT();
            }
        };
    }

    public static Function<NBTTagIntArray, MazeRoom> fromNBT()
    {
        return new Function<NBTTagIntArray, MazeRoom>()
        {
            @Nullable
            @Override
            public MazeRoom apply(@Nullable NBTTagIntArray input)
            {
                return new MazeRoom(input);
            }
        };
    }
}
