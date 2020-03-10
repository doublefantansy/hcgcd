package hzkj.cc.base

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FinishBroadCast : BroadcastReceiver() {
  override fun onReceive(
    context: Context?,
    intent: Intent?
  ) {
    (context as Activity).finish()
  }
}