package com.customwrld.pipeline;

import com.customwrld.pigeon.Pigeon;
import com.customwrld.pipeline.pigeon.payloads.ProxyDisconnectPayload;
import com.customwrld.pipeline.pigeon.payloads.ProxySwitchPayload;
import com.customwrld.pipeline.pigeon.payloads.ServerOfflinePayload;
import com.customwrld.pipeline.pigeon.payloads.ServerOnlinePayload;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public final class Pipeline extends Plugin implements Listener {

    // TODO: Ping server every 5 seconds, if it doesnt respond send offline message and remove from set, if it does respond and its not in the set make it online

    @Getter private static Pipeline pipeline;
    private Pigeon pigeon;
    private Set<ServerInfo> onlineServers;

    @Override
    public void onEnable() {
        pipeline = this;

        this.pigeon = new Pigeon();
        this.pigeon.initialize("127.0.0.1", 5672, "customwrld", "proxy");

        this.pigeon.getPayloadsRegistry()
                .registerPayloadsInPackage("com.customwrld.pipeline.pigeon.payloads");

        this.pigeon.setupDefaultUpdater();

        this.pigeon.acceptDelivery();

        this.onlineServers = new HashSet<>();

        getProxy().getPluginManager().registerListener(this, this);

//        (InetAddress.getByName(Server.getServerInfo().getAddress().split(":")[0]).isReachable(5000)) ? "reachable" : "not reachable";

        getProxy().getScheduler().schedule(this, () -> {
            for (ServerInfo serverInfo : getProxy().getServers().values()) {
                serverInfo.ping((serverPing, exception) -> {
                    boolean online = exception == null || !exception.getCause().getMessage().equals("finishConnect(..) failed: Connection refused");

                    if(!onlineServers.contains(serverInfo) && online) {
                        this.pigeon.broadcast(new ServerOnlinePayload(serverInfo.getName()));
                        this.onlineServers.add(serverInfo);
                    } else if(onlineServers.contains(serverInfo) && !online) {
                        this.pigeon.broadcast(new ServerOfflinePayload(serverInfo.getName()));
                        this.onlineServers.remove(serverInfo);
                    }
                });
            }
        }, 0L, 5, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String fromServer = (event.getFrom() != null ? event.getFrom().getName() : null);
        String toServer = player.getServer().getInfo().getName();

        this.pigeon.broadcast(new ProxySwitchPayload(player.getUniqueId(), player.getName(), fromServer, toServer));
    }

    @EventHandler
    public void onServerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Server server = player.getServer();

        if(server != null) {
            this.pigeon.broadcast(new ProxyDisconnectPayload(player.getUniqueId(), player.getName(), server.getInfo().getName()));
        }
    }
}
