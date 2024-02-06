package com.devo.bim.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.NetworkInterface;

@Data
@NoArgsConstructor
public class NetworkDeviceDTO {

    private String displayName;
    private String name;
    private String ipAddress;
    private String macAddress;

    public NetworkDeviceDTO(NetworkInterface networkDevice, String macAddress){
        this.displayName = networkDevice.getDisplayName();
        this.name = networkDevice.getName();
        this.ipAddress = "";
        this.macAddress = macAddress;
    }
}
