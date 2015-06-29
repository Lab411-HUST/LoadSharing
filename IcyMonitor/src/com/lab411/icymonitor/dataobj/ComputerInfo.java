/*
 * Copyright 2013 Thanasis Georgiou
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

package com.lab411.icymonitor.dataobj;

/**
 * Stores computer info for the connection list.
 * Created by sakisds on 16/06/13.
 */
public class ComputerInfo {
    private final String mName, mAddress;
    private final int mType;
    private boolean mToBeRemoved = false;

    public ComputerInfo(String name, String address, int type) {
        mName = name;
        mAddress = address;
        mType = type;
    }

    /**
     * Get the name of this computer.
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the address of this computer.
     *
     * @return
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Get the type (laptop/server/desktop) of this computer.
     *
     * @return
     */
    public int getType() {
        return mType;
    }

    public void setToBeRemoved() {
        mToBeRemoved = true;
    }

    public boolean isToBeRemoved() {
        return mToBeRemoved;
    }
}
