package net.chrotos.chrotoscloud.player;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.net.InetAddress;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public interface SidedPlayer extends Kickable {
    Object getSidedObject();
    UUID getUniqueId();
    String getName();
    Locale getLocale();
    String getResourcePackHash();
    void setResourcePack(@NonNull String url, @NonNull String hash);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required);
    void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, TextComponent prompt);
    InetAddress getIPAddress();
    default boolean hasResourcePackApplied(@NonNull String hash) {
        return getResourcePackHash() != null && getResourcePackHash().equalsIgnoreCase(hash);
    }
}
