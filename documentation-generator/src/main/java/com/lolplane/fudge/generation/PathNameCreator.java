package com.lolplane.fudge.generation;

import org.apache.commons.lang3.StringUtils;

public class PathNameCreator {
    private static final String SPECIAL_FILESYSTEM_CHARACTERS = "[\\\\/?`'\" ]";

    public String createPathName(String targetedName) {
        return StringUtils.abbreviateMiddle(StringUtils.lowerCase(targetedName).replaceAll(SPECIAL_FILESYSTEM_CHARACTERS, "_"), "...", 250);
    }

}
