package edu.capella.mobile.android.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.widgets.CPTextView


/**
 * DialogUtils.kt : class provides feature to open dialogs from application
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  10-02-2020
 *
 *
 */

object DialogUtils {


    var screenNamePrefix:String  = ""
    /**
     *  static function to update your app in dialog from login screen.
     */
    fun onUpdateYourAppDialog(mcontext: Context, Listener: EventListener) {
        try {
            val dialog = Dialog(mcontext,R.style.DialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.update_your_app_layout)

            val updateTxt = dialog.findViewById(R.id.updateTxt) as TextView

            val backButton = dialog.findViewById(R.id.backButton) as ImageView


            updateTxt.setOnClickListener {
                Listener.update()
                dialog.dismiss()
            }
            backButton.setOnClickListener {
                dialog.dismiss()
            }


            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     *  static function to open keep me singed in dialog from login screen.
     */
    fun onKeepMeSignInDialog(mcontext: Context, Listener: EventListener) {
        try {
            val dialog = Dialog(mcontext,R.style.DialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.keep_me_signed_layout)
            val cancelTV = dialog.findViewById(R.id.cancelTV) as TextView
            val comfirmTV = dialog.findViewById(R.id.comfirmTV) as TextView

            comfirmTV.setOnClickListener {
                Listener.confirm()
                dialog.dismiss()
            }
            cancelTV.setOnClickListener {
                dialog.dismiss()
//                checkBox.isChecked = false
//                Listener.cancel()
//                    Handler().postDelayed({
//                        dialog.dismiss()
//                }, 500)

            }
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun onShowDialog(mcontext: Context,title:String,message:String) {

        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(mcontext)

        // set message of alert dialog
        dialogBuilder.setMessage(message)
//            // if the dialog is cancelable
//            .setCancelable(true)
            // positive button text and action
            .setPositiveButton(mcontext.resources.getString(R.string.ok), DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
    }

    fun onLoginErrorDialog(mcontext: Context,title:String,message:String) {

        // build alert dialog
        try {
            val dialog = Dialog(mcontext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.login_error_dialog_layout)
            val headerTxt = dialog.findViewById(R.id.headerTxt) as CPTextView
            val messageTxt = dialog.findViewById(R.id.message) as CPTextView
            val continueTV = dialog.findViewById(R.id.okTxtView) as CPTextView

            headerTxt.text=title

            messageTxt.text=message

            continueTV.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }



     lateinit var  genericDialog :Dialog
    fun showGenericErrorDialog(context: Context){
        // build alert dialog
        releaseGenericDialog()
        try {
            genericDialog = Dialog(context)
            genericDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            genericDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            genericDialog.setContentView(R.layout.generic_dialog_layout)
            val continueTV = genericDialog.findViewById(R.id.okTxtView) as CPTextView

            continueTV.setOnClickListener {
                genericDialog.dismiss()
            }
//            val window: Window = genericDialog.window!!
//            window.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
            genericDialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun releaseGenericDialog()
    {
        try {
            if(genericDialog!=null)
            {
                genericDialog.dismiss()
            }
        }catch (t:Throwable){}
    }

    fun showDialogOnGoOutSide(mContext: Context,listener: EventListener){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(true)
//            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_go_outside_layout)
            val cancelTV = dialog.findViewById(R.id.cancelTV) as CPTextView
            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView

            continueTV.setOnClickListener {
                dialog.dismiss()
                listener.confirm()
                if(screenNamePrefix!="")
                {OmnitureTrack.trackAction(screenNamePrefix + ":confirm")}
                screenNamePrefix =""
            }
            cancelTV.setOnClickListener { dialog.dismiss()
                if(screenNamePrefix!="")
                {OmnitureTrack.trackAction(screenNamePrefix + ":cancel")}
                screenNamePrefix = ""
            }

//            val window: Window = dialog.window!!
//            window.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun showDialogOnGoOutSide(mContext: Context,listener: EventListener , customMessage:String){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(true)
//            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_go_outside_layout)
            dialog.findViewById<TextView>(R.id.messageTxt).text = customMessage
            val cancelTV = dialog.findViewById(R.id.cancelTV) as CPTextView
            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView

            continueTV.setOnClickListener {
                dialog.dismiss()
                listener.confirm()
                if(screenNamePrefix!="") {
                    OmnitureTrack.trackAction(screenNamePrefix + ":confirm")
                }
                screenNamePrefix =""
            }
            cancelTV.setOnClickListener {
                dialog.dismiss()
                if(screenNamePrefix!="") {
                    OmnitureTrack.trackAction(screenNamePrefix + ":cancel")
                }
                screenNamePrefix = ""
            }
//            val window: Window = dialog.window!!
//            window.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    fun showDialogOnUpdateVersion(mContext: Context,listener: EventListener){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_update_version)
            val cancelTV = dialog.findViewById(R.id.cancelTV) as CPTextView
            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView

            continueTV.setOnClickListener {
                dialog.dismiss()
                listener.update()
            }
            cancelTV.setOnClickListener {

                dialog.dismiss()
                listener.cancleUpdate()
                 }
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    fun showSessionTimeout(mContext: Context,listener: EventListener){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.session_timeout_layout)

            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView
            continueTV.setOnClickListener {
                dialog.dismiss()
                listener.confirm()
            }
            val window: Window = dialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun showDialogOnGoOutTarget(
        mContext: Context,
        listener: EventListener
    ){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(true)
//            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_go_outside_target_date_layout)
            val cancelTV = dialog.findViewById(R.id.cancelTV) as CPTextView
            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView

            continueTV.setOnClickListener {
                dialog.dismiss()

            }
            cancelTV.setOnClickListener {
                listener.cancel()
                dialog.dismiss()
            }
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    fun showDialogEditingdraft(
        mContext: Context,
        listener: EventListener
    ){
        try {
            val dialog = Dialog(mContext/*,R.style.DialogTheme*/)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_warning_layout)
            val cancelTV = dialog.findViewById(R.id.cancelTV) as CPTextView
            val continueTV = dialog.findViewById(R.id.continueTV) as CPTextView

            continueTV.setOnClickListener {
                listener.confirm()
                dialog.dismiss()

            }
            cancelTV.setOnClickListener {
                listener.cancel()
                dialog.dismiss()
                OmnitureTrack.trackAction("course:discussions:drafts-edit")
            }
            dialog.show()
        }catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}
