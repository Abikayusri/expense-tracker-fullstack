package com.abika.expense.android.push

import com.abika.expense.core.datastore.TokenStore
import com.abika.expense.domain.auth.DeviceRegistry
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ExpenseMessagingService : FirebaseMessagingService() {

    private val tokenStore: TokenStore by inject()
    private val deviceRegistry: DeviceRegistry by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        // Only a logged-in user can own a device; after the next login the login hook registers it.
        scope.launch {
            if (tokenStore.current() != null) deviceRegistry.register(token)
        }
    }

    // Called while the app is in the foreground; in the background the system shows the notification itself.
    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        BudgetNotifications.show(
            context = this,
            id = message.messageId?.hashCode() ?: System.currentTimeMillis().toInt(),
            title = notification.title ?: "Expense Tracker",
            body = notification.body.orEmpty(),
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
