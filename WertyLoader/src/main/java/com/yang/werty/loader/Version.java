package com.yang.werty.loader;

import com.yang.werty.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

    private static final Pattern EXPRESSION = Pattern.compile("(?<major>\\d{1,5})\\.(?<minor>\\d{1,5})\\.(?<build>\\d{1,8})");

    private int major;
    private int minor;
    private int build;

    private Version(int major, int minor, int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    public static Version of(String version) {
        if (StringUtils.isEmpty(version)) {
            throw new IllegalArgumentException("wrong version: null");
        }
        Matcher matcher = EXPRESSION.matcher(version);
        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group("major"));
            int minor = Integer.parseInt(matcher.group("minor"));
            int update = Integer.parseInt(matcher.group("update"));
            return new Version(major, minor, update);
        } else {
            throw new IllegalArgumentException("wrong version: " + version);
        }
    }

    public boolean isUpdated(String version) {
        Version ver = of(version);
        return ver.major > this.major || (ver.major == this.major && isMinorUpdated(ver));
    }

    private boolean isMinorUpdated(Version ver) {
        return ver.minor > this.minor || (ver.minor == this.minor && isBuildUpdated(ver));
    }

    private boolean isBuildUpdated(Version ver) {
        return ver.build > this.build;
    }
}
