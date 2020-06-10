package edu.capella.mobile.android.fcm_service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.urbanairship.push.fcm.AirshipFirebaseIntegration

/**
 * CapellaAppFCMService.kt :  Firebase service to listen incoming pushed notification.
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-02-2020
 *
 */
class CapellaAppFCMService :FirebaseMessagingService(){

    /**
     * Method executes when any pushed notification arrives
     *
     * @param remoteMessage : Message pushed from server for mobile client
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        AirshipFirebaseIntegration.processMessageSync(applicationContext, remoteMessage)
    }

    /**
     * Method executes when service registered with device successfully
     *
     * @param token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AirshipFirebaseIntegration.processNewToken(applicationContext)
    }
}