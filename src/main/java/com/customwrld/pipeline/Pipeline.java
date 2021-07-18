package com.customwrld.pipeline;

import com.customwrld.commonlib.CommonLib;
import com.customwrld.pigeon.Pigeon;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class Pipeline extends Plugin {

    @Getter private static Pipeline pipeline;
    private Pigeon pigeon;
    private Set<ServerInfo> onlineServers;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private CommonLib commonLib;

    @Override
    public void onEnable() {
        pipeline = this;

        this.pigeon = new Pigeon();

        this.pigeon.initialize("127.0.0.1", 5672, "customwrld", "proxy");

        this.pigeon.setupDefaultUpdater();

        this.pigeon.acceptDelivery();

        this.onlineServers = new HashSet<>();

        Listeners.setPigeon(this.pigeon);

        this.mongoClient = new MongoClient("127.0.0.1", 27017);
        this.mongoDatabase = this.mongoClient.getDatabase("CUSTOMWRLD");

        this.commonLib = new CommonLib(this.mongoClient, this.mongoDatabase, this.pigeon);

        getProxy().getPluginManager().registerListener(this, new Listeners());

//        getProxy().getScheduler().schedule(this, () -> {
//            for (ServerInfo serverInfo : getProxy().getServers().values()) {
//                serverInfo.ping((serverPing, exception) -> {
//                    boolean online = exception == null || !exception.getCause().getMessage().equals("finishConnect(..) failed: Connection refused");
//
//                    if(!onlineServers.contains(serverInfo) && online) {
//                        this.pigeon.broadcast(new ServerOnlinePayload(serverInfo.getName()));
//                        this.onlineServers.add(serverInfo);
//                    } else if(onlineServers.contains(serverInfo) && !online) {
//                        this.pigeon.broadcast(new ServerOfflinePayload(serverInfo.getName()));
//                        this.onlineServers.remove(serverInfo);
//                    }
//                });
//            }
//        }, 0L, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() { }

}
