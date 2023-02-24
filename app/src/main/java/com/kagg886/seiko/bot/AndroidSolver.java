package com.kagg886.seiko.bot;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import com.kagg886.seiko.activity.CaptchaActivity;
import com.kagg886.seiko.activity.MainActivity;
import com.kagg886.seiko.activity.SMSActivity;
import com.kagg886.seiko.event.SnackBroadCast;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.network.RetryLaterException;
import net.mamoe.mirai.network.UnsupportedSliderCaptchaException;
import net.mamoe.mirai.utils.DeviceVerificationRequests;
import net.mamoe.mirai.utils.DeviceVerificationResult;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.bot
 * @className: AndroidSolver
 * @author: kagg886
 * @description: 验证解决器
 * @date: 2022/12/13 12:31
 * @version: 1.0
 */
public class AndroidSolver extends LoginSolver {
    private final MainActivity avt;

    public AndroidSolver(MainActivity avt) {
        this.avt = avt;
    }

    @Nullable
    @Override
    public Object onSolveDeviceVerification(@NotNull Bot bot, @NotNull DeviceVerificationRequests requests, @NotNull Continuation<? super DeviceVerificationResult> $completion) {
        Intent i = new Intent(avt, SMSActivity.class);
        i.putExtra("phone",requests.getSms().getPhoneNumber());
        i.putExtra("country",requests.getSms().getCountryCode());
        requests.getSms().requestSms(new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                if (o instanceof RetryLaterException) {
                    SnackBroadCast.sendBroadCast(((RetryLaterException) o).getMessage());
                    return;
                }
                Log.i("CallBack",o.getClass().getName());
            }
        });
        avt.verifyCall.launch(i);
        ActivityResult result = avt.getResult();
        if (result.getResultCode() != Activity.RESULT_OK) {
            if (result.getResultCode() == SMSActivity.RESULT_RETRY) {
                onSolveDeviceVerification(bot,requests,$completion);
            }
            throw new UnsupportedOperationException("用户已取消设备SMS验证");
        }
        return requests.getSms().solved(result.getData().getStringExtra("code"));
    }

    @Nullable
    @Override
    public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
        return null;
    }

    @Override
    public boolean isSliderCaptchaSupported() {
        //用于通知Mirai-Core此解决器支持滑动验证
        return true;
    }

    @Nullable
    @Override
    public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String s, @NotNull Continuation<? super String> continuation) {
        Intent i = new Intent(avt, CaptchaActivity.class);
        i.putExtra("url", s);
        avt.verifyCall.launch(i);
        ActivityResult result = avt.getResult();
        if (result.getResultCode() != Activity.RESULT_OK) {
            throw new UnsupportedSliderCaptchaException("滑块验证被用户取消");
        }
        return result.getData().getStringExtra("ticket");
    }
}
