package edu.capella.mobile.android.fcm_service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.urbanairship.push.fcm.AirshipFirebaseIntegration

/*
 * Class Name.kt : class description goes here
 *
 * @author  :  SSharma
 * @version :  1.0
 * @since   :  2/26/2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
class CapellaFireBaseService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        AirshipFirebaseIntegration.processMessageSync(applicationContext, p0);
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        AirshipFirebaseIntegration.processNewToken(applicationContext);
    }
}