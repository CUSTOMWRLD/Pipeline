package com.customwrld.pipeline.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxyDisconnectPayload extends BasePayload {

    @Transmit UUID uuid;
    @Transmit String player;
    @Transmit String server;

    public ProxyDisconnectPayload() {
        payloadId = "proxy-proxyDisconnect";
    }

    public ProxyDisconnectPayload(UUID uuid, String player, String server) {
        this();
        this.uuid = uuid;
        this.player = player;
        this.server = server;
    }

    @Override
    public void receive() {}

}
