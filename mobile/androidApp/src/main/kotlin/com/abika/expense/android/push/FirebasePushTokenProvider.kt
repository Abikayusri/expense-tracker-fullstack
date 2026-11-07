package com.abika.expense.android.push

import android.content.Context
import com.abika.expense.domain.auth.PushTokenProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebasePushTokenProvider(private val context: Context) : PushTokenProvider {

    override suspend fun currentToken(): String? {
        if (!isFirebaseConfigured(context)) return null
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                continuation.resume(if (task.isSuccessful) task.result else null)
            }
        }
    }

    companion object {
        /** False when the build had no google-services.json, so Firebase never initialised. */
        fun isFirebaseConfigured(context: Context): Boolean = FirebaseApp.getApps(context).isNotEmpty()
    }
}
