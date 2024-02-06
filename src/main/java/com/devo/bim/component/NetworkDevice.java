package com.devo.bim.component;

import com.devo.bim.model.dto.NetworkDeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NetworkDevice {

    public List<NetworkDeviceDTO> getNetworkDeviceDTO() {

        List<NetworkDeviceDTO> networkDeviceDTOs = new ArrayList<>();

        try {
            extractNetworkDeviceInfo(networkDeviceDTOs);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return networkDeviceDTOs;
    }

    private void extractNetworkDeviceInfo(List<NetworkDeviceDTO> networkDeviceDTOs) throws SocketException {

        Enumeration networkDevices = NetworkInterface.getNetworkInterfaces();

        while (networkDevices.hasMoreElements()) {
            NetworkInterface networkDevice = (NetworkInterface) networkDevices.nextElement();
            byte[] mac = networkDevice.getHardwareAddress();

            if (mac != null && mac.length == 6) {
                networkDeviceDTOs.add(new NetworkDeviceDTO(networkDevice, bytes2mac(mac)));
            }
        }
    }

    private static String bytes2mac(byte[] bytes) {
        if (bytes.length != 6) {
            System.out.println("    ");
            return null;
        }
        StringBuffer macString = new StringBuffer();
        byte currentByte;
        for (int i = 0; i < bytes.length; i++) {
            currentByte = (byte) ((bytes[i] & 240) >> 4);
            macString.append(Integer.toHexString(currentByte));
            currentByte = (byte) (bytes[i] & 15);
            macString.append(Integer.toHexString(currentByte));
            macString.append("-");
        }
        macString.delete(macString.length() - 1, macString.length());

        return macString.toString().toUpperCase();
    }
}
