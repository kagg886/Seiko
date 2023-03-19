package com.kagg886.seiko.util;

import androidx.annotation.NonNull;
import net.mamoe.mirai.LowLevelApiAccessor;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.utils.DeviceInfo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.util
 * @className: ReallyDeviceInfoSpawner
 * @author: kagg886
 * @description: 收集本机设备信息以进行device.json生成
 * @date: 2023/3/12 19:45
 * @version: 1.0
 */
public class DeviceInfoBuilder {
    /*
    {
    "deviceInfoVersion": 2,
    "data": {
        "display": "MIRAI.292031.001",
        "product": "mirai",
        "device": "mirai",
        "board": "mirai",
        "brand": "mamoe",
        "model": "mirai",
        "bootloader": "unknown",
        "fingerprint": "mamoe/mirai/mirai:10/MIRAI.200122.001/8605818:user/release-keys",
        //上面的在Build类里获取

        "bootId": "3D1E0D37-7514-0F05-7690-DB886FCD9AFE",
        "procVersion": "Linux version 3.0.31-93rX6M0s (android-build@xxx.xxx.xxx.xxx.com)", /proc/version
        "baseBand": "",
        "version": {
            "incremental": "5891938",
            "release": "10",
            "codename": "REL"
        },
        "simInfo": "T-Mobile",
        "osType": "android",
        "macAddress": "02:00:00:00:00:00",
        "wifiBSSID": "02:00:00:00:00:00",
        "wifiSSID": "<unknown ssid>",
        "imsiMd5": "c43f0b2230f2c5f128d35746ca4bd396",
        "imei": "864336866074233",
        "apn": "wifi"
    }
}
*/
    private byte[] display;
    private byte[] product;
    private byte[] device;
    private byte[] board;
    private byte[] brand;
    private byte[] model;
    private byte[] bootloader;
    private byte[] fingerprint;
    private byte[] bootId;
    private byte[] procVersion;
    private byte[] baseBand;
    private DeviceInfo.Version version;
    private byte[] simInfo;
    private byte[] osType;
    private byte[] macAddress;
    private byte[] wifiBSSID;
    private byte[] wifiSSID;
    private byte[] imsiMd5;
    private String imei;
    private byte[] apn;

    public String toString(DeviceInfo info) {
        JSONObject object = new JSONObject();
        for (Field f : info.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            String k;
            try {
                Object a = f.get(info);
                if (a instanceof byte[]) {
                    k = new String((byte[]) a);
                } else {
                    k = a.toString();
                }
                object.put(f.getName(),k);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object.toString();
    }

    public DeviceInfo build() {
        //检查字段是否为null，否则自动填充
        DeviceInfo randomDeviceInfo = DeviceInfo.random();
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(this) == null) {
                    Field ran = randomDeviceInfo.getClass().getDeclaredField(field.getName());
                    ran.setAccessible(true);
                    field.set(this,ran.get(randomDeviceInfo));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return new DeviceInfo(display, product, device, board, brand, model, bootloader, fingerprint, bootId, procVersion, baseBand, version, simInfo, osType, macAddress, wifiBSSID, wifiSSID, imsiMd5, imei, apn);
    }

    public DeviceInfoBuilder display(String display) {
        this.display = display.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder product(String product) {
        this.product = product.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder device(String device) {
        this.device = device.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder board(String board) {
        this.board = board.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder brand(String brand) {
        this.brand = brand.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder model(String model) {
        this.model = model.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder bootloader(String bootloader) {
        this.bootloader = bootloader.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder fingerprint(String fingerprint) {
        this.fingerprint = fingerprint.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder bootId(String bootId) {
        this.bootId = bootId.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder procVersion(String procVersion) {
        this.procVersion = procVersion.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder baseBand(String baseBand) {
        this.baseBand = baseBand.getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public DeviceInfoBuilder simInfo(String simInfo) {
        this.simInfo = simInfo.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder osType(String osType) {
        this.osType = osType.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder macAddress(String macAddress) {
        this.macAddress = macAddress.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder wifiBSSID(String wifiBSSID) {
        this.wifiBSSID = wifiBSSID.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder wifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder imsiMd5(String imsiMd5) {
        this.imsiMd5 = imsiMd5.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder imei(String imei) {
        this.imei = imei;
        return this;
    }


    public DeviceInfoBuilder apn(String apn) {
        this.apn = apn.getBytes(StandardCharsets.UTF_8);
        return this;
    }


    public DeviceInfoBuilder() {

    }
}
