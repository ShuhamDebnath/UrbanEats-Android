package com.shuham.urbaneats.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shuham.urbaneats.core.NotificationHelper
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OrderStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    // Inject Helper inside Worker (KoinComponent allows this)
    private val notificationHelper: NotificationHelper by inject()

    override suspend fun doWork(): Result {
        val orderId = inputData.getString("order_id") ?: return Result.failure()

        // SIMULATION OF BACKEND PUSH NOTIFICATIONS

        // Stage 1: Preparing
        delay(5000)
        notificationHelper.showOrderNotification(orderId, "Preparing 🍳")

        // Stage 2: Out for Delivery
        delay(5000)
        notificationHelper.showOrderNotification(orderId, "Out for Delivery 🛵")

        // Stage 3: Delivered
        delay(5000)
        notificationHelper.showOrderNotification(orderId, "Delivered ✅")

        return Result.success()
    }

}

