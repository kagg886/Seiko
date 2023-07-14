package com.tencent.mobileqq.dt

import android.content.Context
import com.tencent.mobileqq.fe.IFEKitLog

object Dtn {
    external fun initContext(context: Context, str: String)

    external fun initLog(iFEKitLog: IFEKitLog)

    external fun initUin(str: String?)
}