package com.kagg886.seiko.bot;

import android.annotation.SuppressLint;
import android.util.Log;
import com.kagg886.seiko.util.ObjectUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.internal.spi.EncryptService;
import net.mamoe.mirai.internal.spi.EncryptServiceContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.dsbbs2.t544.Tlv544Sign;

/**
 * tlv544实现器，未来将支持更多类型
 *
 * @author kagg886
 * @date 2023/6/13 17:21
 **/
public class SeikoEncryptServiceImpl implements EncryptService {

    public SeikoEncryptServiceImpl() {
        Log.d(getClass().getName(), "EncryptService Patched!");
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public byte[] encryptTlv(@NotNull EncryptServiceContext context, int tlvType, @NotNull byte[] payload) {
        if (tlvType != 0x544) return null;
        Bot.getInstance(context.getId()).getLogger().debug(String.format("encryptTLV,type=0x%s:", Integer.toHexString(tlvType)));

        byte[] bytes;
        if (payload[payload.length - 1] == 0) {
            bytes = ObjectUtils.copyInto(payload, new byte[payload.length], 4, 4, payload.length);
        } else {
            bytes = payload;
        }
        return Tlv544Sign.signBytes(bytes);
    }
}
