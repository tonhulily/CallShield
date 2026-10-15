package vn.callshield.screening

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.core.app.NotificationCompat
import vn.callshield.R
import vn.callshield.data.PhoneNormalizer
class CallScreeningServiceImpl : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val rawNumber = callDetails.handle?.schemeSpecificPart.orEmpty()
        val number = PhoneNormalizer.normalize(rawNumber)
        val prefs = getSharedPreferences("callshield_cache", Context.MODE_PRIVATE)
        val blockedNumbers = prefs.getStringSet("blocked_numbers", emptySet()).orEmpty()
        val shouldBlock = number.isNotBlank() && blockedNumbers.contains(number)

        val behavior = prefs.getString("call_behavior", "notify") ?: "notify"
        val brand = prefs.getStringSet("blocked_labels", emptySet()).orEmpty()
            .firstOrNull { it.substringBefore('\t') == number }
            ?.substringAfter('\t', "Danh sách chặn") ?: "Danh sách chặn"

        if (shouldBlock) notifyMatchedCall(number, brand, behavior == "block")

        val reject = shouldBlock && behavior == "block"
        val response = CallResponse.Builder()
            .setDisallowCall(reject)
            .setRejectCall(reject)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)
    }
    private fun notifyMatchedCall(number: String, brand: String, blocked: Boolean) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "callshield_blocked_calls"
        manager.createNotificationChannel(
            NotificationChannel(channelId, "Cuộc gọi đã chặn", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Thông báo khi CallShield nhận diện số nằm trong danh sách chặn"
            }
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.sym_call_missed)
            .setContentTitle(if (blocked) "CallShield đã chặn cuộc gọi" else "CallShield nhận diện số trong danh sách")
            .setContentText("$number • $brand")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                if (blocked) "Cuộc gọi từ $number đã bị từ chối theo chế độ chặn của bạn. Thương hiệu/đơn vị: $brand."
                else "Số $number thuộc danh sách theo dõi của thương hiệu/đơn vị: $brand. Cuộc gọi vẫn được phép hiển thị theo lựa chọn của bạn."
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify((number.hashCode() and 0x7fffffff), notification)
    }
}