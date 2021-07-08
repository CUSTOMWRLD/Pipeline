package com.customwrld.pipeline;

import com.customwrld.commonlib.modules.pigeon.payloads.network.ProxyPlayerConnectPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.network.ProxyPlayerDisconnectPayload;
import com.customwrld.commonlib.modules.pigeon.payloads.network.ProxyPlayerSwitchPayload;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.commonlib.util.PigeonUtil;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.pigeon.Pigeon;
import lombok.Setter;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Listeners implements Listener {

    @Setter private static Pigeon pigeon;

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String fromServer = (event.getFrom() != null ? event.getFrom().getName() : null);
        String toServer = player.getServer().getInfo().getName();

        if(fromServer == null) {
            PigeonUtil.broadcast(new ProxyPlayerConnectPayload(player.getUniqueId(), player.getName(), toServer));
        } else {
            PigeonUtil.broadcast(new ProxyPlayerSwitchPayload(player.getUniqueId(), player.getName(), fromServer, toServer));
        }
    }

    @EventHandler
    public void onServerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Server server = player.getServer();

        if(server != null) {
            pigeon.broadcast(new ProxyPlayerDisconnectPayload(playerUUID, player.getName(), server.getInfo().getName()));
        }
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        BungeeAudiences adventure = Pipeline.getPipeline().adventure();
        UUID playerUUID = event.getConnection().getUniqueId();

        Profile profile = Profile.getProfile(playerUUID);

        Punishment punishment = profile.api().getActiveBan();

        if (punishment != null) {
            event.setCancelReason(adventure.Component.join(Component.newline(), getPunishmentMessage(punishment)));
            event.setCancelReason(new ComponentBuilder("Test").color(ChatColor.RED).create());
            event.setCancelled(true);
            return;
        }

        // TODO: Load and Cache profile and do flame checks

//        event.setCancelReason(new ComponentBuilder("Test").color(ChatColor.RED).create());
//        event.setCancelled(true);
    }

    public List<Component> getPunishmentMessage(Punishment punishment) {
        Date date = new Date(punishment.getAddedAt());

        return Arrays.asList(
                MC.Style.SEPARATOR,
                Component.text("You are " + punishment.api().getContext() + (!punishment.api().isPermanent() ? " for " + punishment.api().getTimeRemaining() : "") + ".")
                        .color(NamedTextColor.RED),
                Component.empty(),
                Component.text("Reason: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getAddedReason())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Added On: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(TimeUtil.dateToString(date, true))
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Punishment ID: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getId())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.empty(),
                Component.text("Appeal At: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text("https://customwrld.com/discord")
                                        .color(NamedTextColor.AQUA)
                                        .decoration(TextDecoration.UNDERLINED, true)
                        ),
                MC.Style.SEPARATOR
        );
    }

}
