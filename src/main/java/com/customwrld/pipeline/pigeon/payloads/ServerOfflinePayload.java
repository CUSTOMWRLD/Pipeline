package com.customwrld.pipeline.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class ServerOfflinePayload extends BasePayload {

    @Transmit String name;

    public ServerOfflinePayload() {
        payloadId = "server-serverOffline";
    }

    public ServerOfflinePayload(String name) {
        this();
        this.name = name;
    }

    @Override
    public void receive() {}

}
