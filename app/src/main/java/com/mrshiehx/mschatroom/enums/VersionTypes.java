package com.mrshiehx.mschatroom.enums;

import com.mrshiehx.mschatroom.MSChatRoom;
import com.mrshiehx.mschatroom.utils.Utils;

public enum VersionTypes {
    ALPHA(1, 6),
    BETA(7, 8),
    RELEASE(9, Utils.getVersionCode(MSChatRoom.getContext()));

    int versionCodeFromToStart;
    int versionCodeFromToEnd;

    VersionTypes(int versionCodeFromToStart, int versionCodeFromToEnd) {
        this.versionCodeFromToStart = versionCodeFromToStart;
        this.versionCodeFromToEnd = versionCodeFromToEnd;
    }

    public int getVersionCodeFromToStart() {
        return versionCodeFromToStart;
    }

    public int getVersionCodeFromToEnd() {
        return versionCodeFromToEnd;
    }
}
