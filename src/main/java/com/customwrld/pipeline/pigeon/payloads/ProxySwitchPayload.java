package com.customwrld.pipeline.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ProxySwitchPayload extends BasePayload {

    @Transmit UUID uuid;
    @Transmit String player;
    @Transmit String serverFrom;
    @Transmit String serverTo;

    public ProxySwitchPayload() {
        payloadId = "proxy-proxySwitch";
    }

    public ProxySwitchPayload(UUID uuid, String player, String serverFrom, String serverTo) {
        this();
        this.uuid = uuid;
        this.player = player;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public void receive() {}

}
