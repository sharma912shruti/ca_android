package edu.capella.mobile.android.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.app.CapellaApplication
import edu.capella.mobile.android.utils.*

import edu.capella.mobile.android.utils.DialogUtils.showGenericErrorDialog
import edu.capella.mobile.android.utils.Util.isInternetOn
import edu.capella.mobile.android.utils.Util.openPlayStore
import edu.capella.mobile.android.utils.Validator.checkEmptyString
import edu.capella.mobile.android.base.BaseActivity
import edu.capella.mobile.android.bean.LoginBean
import edu.capella.mobile.android.interfaces.EventListener
import edu.capella.mobile.android.interfaces.NetworkListener
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.network.NetworkHandler
import edu.capella.mobile.android.task.StickyInfoGrabber
import edu.capella.mobile.android.utils.Constants.PROD
import edu.capella.mobile.android.utils.DialogUtils.onLoginErrorDialog
import edu.capella.mobile.android.utils.Util.isAlphaNumeric
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


/**
 * LoginActivity.kt :  Login screen appears after 2 seconds, this screen will appear in case
 * of app timeout or app remains idle for few minutes @see Constants.APP_TIMEOUT_SECONDS
 *
 *  This screen contains following feature<br>
 *  1.) Reset Password (opens mobile native browser).<br>
 *  2.) Forgot Password (opens mobile native browser).<br>
 *  3.) TollFree Number (open mobile native dialer).<br>
 *  4.) Learn about capella university (opens mobile native browser).<br>
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  03-02-2020
 *
 */

class LoginActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener,
    TextWatcher, NetworkListener, ConnectivityReceiver.ConnectivityReceiverListener,View.OnTouchListener {

    var isAutoForwarding: Boolean = false

    var isAdaFontSize: Boolean = true

//    var isFontValue: String = ""

    var isShowPass: Boolean = true

    private var checkUpadateVersion:Boolean?=false

    override fun onStart() {
        overridePendingTransition(0, 0)
        super.onStart()
        OmnitureTrack.trackState("login")
    }

    /**
     * isLogin : Flag will keep track whether user has been log in or not, required for TDD
     */
    private var isLogin: Boolean = false

    /**
     * isInternetConnection : Flag will keep track whether internet connection is on or off
     */
    public var isInternetConnection: Boolean = false

    /**
     * Object of broadcast receiver which listen for Network state change i.e. INTERNET ON/OFF
     * and tells current running activity about it.
     */
    private var connectivityReceiver: ConnectivityReceiver? = null



    /**
     * showCheckBoxDialog: Flag required to handle confirmation popup handling.
     */
    private var showCheckBoxDialog: Boolean = true

    /**
     * isRemember : Flag used to check whether user opted for remembering the login
     * credentials or not.
     */
    private var isRemember: Boolean = false

    /**
     *  onCreate is a factory method to show UI and to write certain business logic.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        if(resources.getBoolean(R.bool.portrait_only)){
            checkFontSizeIncrease()
//        }

//        checkFontSizeIncrease()
        if (intent.getBooleanExtra(Constants.IS_AUTO_FORWARD, false)) {
            val email = Preferences.getValue(PreferenceKeys.SECRET_USER)
            val password = Preferences.getValue(PreferenceKeys.SECRET)
            Preferences.addValue(PreferenceKeys.EMAIL, email.toString())
        }else
        {
            if(BuildConfig.VERSION_NAME.toString().contains(PROD))
            {
                callUpdateAPIProd() // WHEN AUTOLOGIN IS NOT ENABLED THEN ONLY CALL UPDATE CHECK
            }
            else
            {
                callUpdateAPI() // WHEN AUTOLOGIN IS NOT ENABLED THEN ONLY CALL UPDATE CHECK
            }

        }
        prepareAppUpdateMessage()
        attachListener()
        setRememberMeData()
        detectIllusion()
        checkInActivity()
    }

    private fun resetOldLoginCache()
    {
        try
        {
            Preferences.removeKey(PreferenceKeys.EMAIL.toString())
            Preferences.removeKey(PreferenceKeys.SECRET.toString() )
            Preferences.removeKey(PreferenceKeys.SECRET_USER.toString() )
            Preferences.removeKey(PreferenceKeys.APP_KILLED_FROM_RECENT.toString() )

            (application as CapellaApplication).clearAppTimer()
        }catch (t:Throwable){}
    }

    private fun checkInActivity()
    {
        if (intent.getBooleanExtra(Constants.IS_TIME_ERROR, false))
        {
             //  resetOldLoginCache()
               logged_out_inactivity.visibility = View.VISIBLE
            OmnitureTrack.trackAction("login:inactivity-logout-message")
        } else
                logged_out_inactivity.visibility = View.GONE
    }

    private fun detectIllusion() {
        if (intent.getBooleanExtra(Constants.IS_AUTO_FORWARD, false)) {
            this.autoLogin()
        }else
        {
            if (intent.getBooleanExtra(Constants.IS_TIME_ERROR, false))
            {
                resetOldLoginCache()
            }
        }
    }

    /**
     * init() method used to initialize value of variables
     */
    private fun init() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        // versionTxt.text=resources.getString(R.string.capella_mobile)+" "+BuildConfig.VERSION_PREFIX+""+ BuildConfig.VERSION_ID+" ("+BuildConfig.VERSION_CODE+")"
        versionTxt.text =
            resources.getString(R.string.capella_mobile) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"

        ConnectivityReceiver.connectivityReceiverListener = this@LoginActivity
        connectivityReceiver = ConnectivityReceiver(networkLayout)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, intentFilter)
    }

    /**
     * Method triggered by broadcast receiver which is listening for Network state.
     *
     * @param isConnected : Tells whether internet is available or not
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        isInternetConnection = isConnected
/*
            if (!isConnected)
            {
                networkLayout.visibility = View.VISIBLE
            } else
            {

            } else {
                networkLayout.visibility = View.GONE
            }
*/

    }

    /**
     * Method recognizing touch event, based on that closes keyboard if its already open.
     *
     * @param v: Reference widget which got tapped
     * @param event : Type of event i.e. DOWN, UP, MOVE
     * @return : whether event is consumes or not.
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }


        when (v!!.id) {
            R.id.txtEmail -> {
                if(checkUpadateVersion!!)
                {
                    openUpdateScreen()
                }
                else
                {
                    return false
                }
            }

            R.id.txtPassword->
            {
                if(checkUpadateVersion!!)
                {
                    openUpdateScreen()
                }
                else
                {
                    return false
                }
            }


        }

        return true
    }

    /**
     * Method used to set click and change listeners for different widgets used over login screen
     */
    private fun attachListener() {
        parentLinearLayout.setOnTouchListener(this)
        txtEmail.setOnTouchListener(this)
        txtPassword.setOnTouchListener(this)
        childLinearLayout.setOnTouchListener(this)
        checkboxLayout.setOnClickListener(this)
//        checkBox.setOnCheckedChangeListener(this)
        btnLogin.setOnClickListener(this)
        txtPassword.addTextChangedListener(this)
        showPsw.setOnClickListener(this)
        showPsw.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
//        showPswll.setOnClickListener(this)
//        showPswll.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        forgotUserTV.setOnClickListener(this)
        forgotUserTVAda.setOnClickListener(this)
       // forgotUserTxt.setOnClickListener(this)
        resetPswTV.setOnClickListener(this)
        resetPswTVAda.setOnClickListener(this)
       // resetPswTxt.setOnClickListener(this)
        learnAboutTV.setOnClickListener(this)
        tollNumberTxt.setOnClickListener(this)
        tollNumberTxtAda.setOnClickListener(this)
    }
    /**
     * Method used to perform login operation, where it authenticates entered
     * username nad password with server, based on authenticated response shows Campus News UI
     * or shows appropriate message with details.
     */
    private fun doLogin() {
        this.isLogin = true
        OmnitureTrack.trackAction("log-in")
        val usr = txtEmail.text.toString()
        val psw = txtPassword.text.toString()
        if (!checkEmptyString(usr)) {
            if (!checkEmptyString(psw)) {
                onLoginErrorDialog(
                    this,
                    resources.getString(R.string.log_in_error),
                    resources.getString(R.string.please_enter_user_name_password)
                )
            } else {
                OmnitureTrack.trackAction("log-in:unsuccessful")
                onLoginErrorDialog(
                    this,
                    resources.getString(R.string.log_in_error),
                    resources.getString(R.string.please_enter_user_name)
                )
            }

        } else if (!checkEmptyString(psw)) {
            OmnitureTrack.trackAction("log-in:unsuccessful")
            onLoginErrorDialog(
                this,
                resources.getString(R.string.log_in_error),
                resources.getString(R.string.please_enter_password)
            )
        } else if (isInternetConnection) {
            OmnitureTrack.trackAction("log-in:unsuccessful")
            performLogin(usr, psw)
            setRememberCredentials(usr, psw)
        } else if (intent.getBooleanExtra(  Constants.IS_TIME_ERROR,false) && isInternetOn(this@LoginActivity)
        ) {
            performLogin(usr, psw)
            setRememberCredentials(usr, psw)
        }

    }

    companion object {
        var noAccessMobileActive: Boolean = false
    }

    private fun checkFontSizeIncrease()
    {
//        val value=isFontValue.toFloat()
//        txtEmail.setText(value.toString())
        if(isAdaFontSize) {

            forgotLayout!!.visibility=View.VISIBLE
            forgotLayoutAda!!.visibility=View.GONE
            technicalSupportLL!!.visibility=View.VISIBLE
            technicalSupportAda!!.visibility=View.GONE
        }
        else
        {
            technicalSupportAda!!.visibility=View.VISIBLE
            technicalSupportLL!!.visibility=View.GONE
            forgotLayout!!.visibility=View.GONE
            forgotLayoutAda!!.visibility=View.VISIBLE
        }
    }

    /**
     * Method executes in case of inactivity when timeout is done and user tries to open the app
     * again, it executes login authentication api with stored credentials and take user back
     * to campus news screen.
     *
     */
    fun autoLogin() {

        OmnitureTrack.trackAction("autologin:true:EMPID")
        val email = Preferences.getValue(PreferenceKeys.EMAIL)
        val password = Preferences.getValue(PreferenceKeys.SECRET)

        Util.trace("Auto login -1")
        if (email != "" && password != "") {
            isAutoForwarding = true
            splashIllusionLayout.visibility = View.VISIBLE
            txtEmail.setText(email)
            txtPassword.setText(password)
            this.performLogin(email!!, password!!)
        }
    }


    /**
     * Factory method provided by click listener implemented, this method
     * handlers all the click events for the widgets placed over login screen.
     * @param v : @see View
     */
    override fun onClick(v: View?) {
        try {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }
        when (v!!.id) {
            R.id.showPsw -> {
                OmnitureTrack.trackAction("login:show-password")
                val view = this.currentFocus
                view?.let { v ->
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                togglePasswordTypeChange()

//                togglePasswordType()
            }

//            R.id.showPswll -> {
//                OmnitureTrack.trackAction("login:hide-password")
//                val view = this.currentFocus
//                view?.let { v ->
//                    val imm =
//                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(view.windowToken, 0)
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//                }
//
//                togglePasswordType()
//            }

            R.id.forgotUserTV -> {
                OmnitureTrack.trackAction("login:forgot-username-linkout")
                Util.openUrlBrowser(this, Constants.FORGOT_PASSWORD)
            }
            R.id.forgotUserTVAda -> {
                OmnitureTrack.trackAction("login:forgot-username-linkout")
                Util.openUrlBrowser(this, Constants.FORGOT_PASSWORD)
            }

           /* R.id.forgotUserTxt->{
                Util.openUrlBrowser(this, Constants.FORGOT_PASSWORD)
            }*/

            R.id.checkboxLayout -> {

                if(checkUpadateVersion!!)
                {
                    openUpdateScreen()
                }
                else if (showCheckBoxDialog) {
                    openSignInDialog()
//                    openUpdateDialog()

//                checkBox.isChecked = false
//                isRemember = false

                } else {

                    checkBoxImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@LoginActivity,
                            R.drawable.ic_check_box_unchecked
                        )
                    )
                    showCheckBoxDialog = true
                    isRemember = false
//                    keepMeLbl.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+" "+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)
                    checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)

                    OmnitureTrack.trackAction("autologin:false:EMPID")
                }
            }


            R.id.resetPswTV -> {
                OmnitureTrack.trackAction("login:forgot-password-linkout")
                Util.openUrlBrowser(this, Constants.RESET_PASSWORD)
            }
            R.id.resetPswTVAda -> {
                OmnitureTrack.trackAction("login:forgot-password-linkout")
                Util.openUrlBrowser(this, Constants.RESET_PASSWORD)
            }

          /*  R.id.resetPswTxt ->{
                Util.openUrlBrowser(this, Constants.RESET_PASSWORD)
            }*/
            R.id.learnAboutTV -> {
                OmnitureTrack.trackAction("login:learn-about-capella-linkout")
                Util.openUrlBrowser(this, Constants.LEARN_ABOUT_CAPELLA)
            }
            R.id.btnLogin -> {

                if(checkUpadateVersion!!)
                {
                    openUpdateScreen()
                }
                else
                {
                    doLogin()
                }


            }
            R.id.tollNumberTxt -> {
                OmnitureTrack.trackAction("log-in:phone-linkout")
                Util.dialNumber(this, tollNumberTxt.text.toString())
            }
            R.id.tollNumberTxtAda -> {
                OmnitureTrack.trackAction("log-in:phone-linkout")
                Util.dialNumber(this, tollNumberTxt.text.toString())
            }
            /*R.id.tollNumberTxtada->{
                Util.dialNumber(this, tollNumberTxt.text.toString())
            }*/
        }
    }

    /**
     * Method checks, if Edittext contains empty value then "Show" label is
     * hidden, otherwise its visible
     */
    override fun afterTextChanged(editable: Editable?) {

        if (editable.toString().isNotEmpty()) {
            showPsw.visibility = View.VISIBLE
        } else {
            showPsw.visibility = View.GONE
        }

    }

    /**
     *  Text Factory methods not in use, but required by Listener implemented.
     */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    /**
     *  Text Factory methods not in use, but required by Listener implemented.
     */
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    /**
     * Method is used to store login credentials in SharedPrefrences if user selects
     * "Keep me signed in" option.
     *
     * @param userName : user name entered by user at login screen, it can be email.
     * @param password : password entered by user at login screen.
     *
     */
    fun setRememberCredentials(userName: String, password: String) {
        OmnitureTrack.trackAction("autologin:info:EMPID")

        Preferences.addValue(PreferenceKeys.SECRET_USER, userName)
        Preferences.addValue(PreferenceKeys.SECRET, password)

        if (isRemember) {
            Preferences.addValue(
                PreferenceKeys.EMAIL, userName

            )
            Preferences.addValue(
                PreferenceKeys.SECRET,
                password
            )

            Preferences.addBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN, isRemember)
        } else {
            Preferences.addBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN, false)


        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        try
        {
            val newOverride = Configuration(newBase?.resources?.configuration)

            Util.trace("font scale before:",newOverride.fontScale.toString())
//            isFontValue=newOverride.fontScale.toString()
            val fontValue= newOverride.fontScale.toString()

                    if(fontValue.toFloat()==1.2f || fontValue.toFloat()<1.2f) {
                        isAdaFontSize=true
                    }
                    else if(fontValue.toFloat()>1.2f)
                    {
                        isAdaFontSize=false
                    }

            Util.trace("font scale after:",newOverride.fontScale.toString())
            applyOverrideConfiguration(newOverride)
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }

    }


    /**
     * Method is used to store a flag whether user opted to keep remembering
     * its login credentials in SharedPrefrence.
     *
     */
    fun setRememberMeData() {

        isRemember = Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN)

        if (isRemember) {
            showCheckBoxDialog = false
            val email = Preferences.getValue(PreferenceKeys.EMAIL)
            val password = Preferences.getValue(PreferenceKeys.SECRET)
            txtEmail.setText(email)
            txtPassword.setText(password)
            checkBoxImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this@LoginActivity,
                    R.drawable.ic_check_box_checked
                )
            )
//            keepMeLbl.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+" "+resources.getString(R.string.ada_keep_me_signe_in_state_checked)
            checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_checked)
//            checkBox.isChecked = isRemember
            if (email == "") {
                checkBoxImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.ic_check_box_unchecked
                    )
                )
//                keepMeLbl.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+" "+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)
//                checkBox.isChecked = false
                checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)
                OmnitureTrack.trackAction("autologin:false:EMPID")
            }
            OmnitureTrack.trackAction("autologin:true:EMPID")
        }
        else
        {
            checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)
            OmnitureTrack.trackAction("autologin:false:EMPID")
        }

    }



    /**
     * Method is used to toggle password visibility.
     */
    private fun togglePasswordTypeChange() {

        if (isShowPass) {

            showPsw.text = resources.getText(R.string.hide)
            txtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance();
            txtPassword.setSelection(txtPassword.text.length)
            OmnitureTrack.trackAction("login:hide-password")
            isShowPass=false

        } else {
            showPsw.text = resources.getText(R.string.show)
            txtPassword.transformationMethod = PasswordTransformationMethod.getInstance();
            txtPassword.setSelection(txtPassword.text.length)
            OmnitureTrack.trackAction("login:show-password")
            isShowPass=true

        }
    }


//    /**
//     * Method is used to toggle password visibility.
//     */
//    private fun togglePasswordType() {
//        Toast.makeText(this@LoginActivity,"check text= "+showPsw.text.toString(),Toast.LENGTH_SHORT).show()
//        if (showPsw.text == resources.getText(R.string.show) || showPsw.text == resources.getText(R.string.show_password)) {
//
//            showPsw.text = resources.getText(R.string.hide)
//            txtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance();
//            txtPassword.setSelection(txtPassword.text.length)
//            OmnitureTrack.trackAction("login:hide-password")
//            Toast.makeText(this@LoginActivity,"nhi",Toast.LENGTH_SHORT).show()
//
//        } else {
//
//            showPsw.text = resources.getText(R.string.show)
//            txtPassword.transformationMethod = PasswordTransformationMethod.getInstance();
//            txtPassword.setSelection(txtPassword.text.length)
//            OmnitureTrack.trackAction("login:show-password")
//            Toast.makeText(this@LoginActivity,"show",Toast.LENGTH_SHORT).show()
//
//
//
//        }
//    }

    /**
     * Factory method of a Checkbox listener, triggers when check box is checked or unchecked
     */
    override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (showCheckBoxDialog) {
                openSignInDialog()
//                checkBox.isChecked = false
//                isRemember = false
            }
        } else {
            showCheckBoxDialog = true
            isRemember = false

        }
    }

    /**
     * Listener created for dialog box which appears when Keep me signed in checkbox is checked.
     */
    inner class ItemEventListener : EventListener {
        override fun confirm() {
            super.confirm()
            showCheckBoxDialog = false
            checkBoxImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this@LoginActivity,
                    R.drawable.ic_check_box_checked
                )
            )
//            keepMeLbl.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+" "+resources.getString(R.string.ada_keep_me_signe_in_state_checked)

            checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_checked)
//            checkBox.isChecked = true
            isRemember = true
            OmnitureTrack.trackAction("autologin:true:EMPID")
        }

        override fun update() {
            super.update()
            openPlayStore(this@LoginActivity)
        }

        override fun cancel() {
            super.cancel()
            checkBoxImg.setImageDrawable(
                ContextCompat.getDrawable(
                    this@LoginActivity,
                    R.drawable.ic_check_box_unchecked
                )
            )
            isRemember = false
//            keepMeLbl.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+" "+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)

            checkboxLayout.contentDescription=resources.getString(R.string.ada_keep_me_signe_in)+","+resources.getString(R.string.ada_keep_me_signe_in_state_unchecked)
            OmnitureTrack.trackAction("autologin:false:EMPID")
        }

    }

    /**
     * Listener created for dialog box which appears when Keep me signed in checkbox is checked.
     */
    private fun openSignInDialog() {
        DialogUtils.onKeepMeSignInDialog(this, ItemEventListener())
    }


//    /**
//     * Listener created for dialog box which appears when Keep me signed in checkbox is checked.
//     */
//    private fun openUpdateVersionDialog() {
//        DialogUtils.onUpdateYourAppDialog(this, ItemEventListener())
//    }


    /**
     * Method used to invoke a network call with given username and password
     *
     * @param userName : userName entered over login screen
     * @param password : password entered over login screen
     *
     */
    private fun performLogin(userName: String, password: String) {
       // checkAPI=false
        val params = HashMap<String, Any>()
        params.put(NetworkConstants.USER_NAME, userName)
        params.put(NetworkConstants.PASSWORD, password)


        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.LOGIN_API,
            params,
            NetworkHandler.METHOD_POST,
            this,
            null
        )
        networkHandler.isPostTypeSubmitting = true
        networkHandler.submitMessage = ""
        networkHandler.execute()

        Preferences.addValue(PreferenceKeys.USER_NAME, userName)
        Preferences.addValue(PreferenceKeys.SECRET, password)

        var stickyInfoGrabber = StickyInfoGrabber(applicationContext, userName, password)
        stickyInfoGrabber.startTask()



    }

    /**
     * its network listener methods invoked after network service complete its task.
     *
     *  @param response : response of network service
     *
     *  @see Pair
     *  @see NetworkHandler
     *
     */
    override fun onNetworkResponse(response: Pair<String, Any>) {

        try {
           if (response.first == NetworkConstants.SUCCESS) {
                Util.trace("Login first :  " + response.first)
                Util.trace("Login second :  " + response.second)
                val gson = Gson()
                val loginBean =
                    gson.fromJson<LoginBean>(response.second.toString(), LoginBean::class.java)
                Util.trace("LoginBean sting  :  " + loginBean.toString())

                if (loginBean.errorData == null) {

                    if(isAlphaNumeric(loginBean.authData!!.employeeId!!.value!!)) {
                        noAccessMobileActive=false
                        Preferences.removeKey(PreferenceKeys.APP_KILLED_FROM_RECENT.toString())
                        OmnitureTrack.trackAction("log-in")

                        Preferences.addValue(
                            PreferenceKeys.USER_ID,
                            loginBean.authData!!.employeeId!!.value!!
                        )
                        Preferences.addValue(PreferenceKeys.LOGIN_INFO, response.second.toString())
                        Preferences.addValue(PreferenceKeys.SECRET, txtPassword.text.toString())

                        val intent = Intent(this, CampusActivity::class.java)

                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        /**
                         * IN Case KeepMe Signed in, auto login is enabled so we will check on CampusNews
                         * screen whether new version of App is available of not.
                         */
                        intent.putExtra(Constants.IS_APP_UPDATE_CHECK_REQUIRED, isAutoForwarding)
                        startActivity(intent)


                        Preferences.addValue(
                            PreferenceKeys.PROFILE_ID,
                            loginBean.authData!!.employeeId!!.value!!
                        )
                        OmnitureTrack.initBasic()


                        finish()
                    }else
                    {
                        Preferences.addBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN,false)
                        openNoAccessMobileScreen()
                        noAccessMobileActive=true
                    }
                } else {
                    checkAutoLoginUnsuccessful()
                    splashIllusionLayout.visibility = View.GONE

                    OmnitureTrack.trackAction("log-in:unsuccessful")
                    //showGenericErrorDialog(this) // NO, DONOT SHOW GENERIC FOR THIS CASE
                    if (isAutoForwarding == false) {
                        onLoginErrorDialog(
                            this,
                            resources.getString(R.string.log_in_error),
                            resources.getString(R.string.sign_in_error)
                        )
                    }
                    isAutoForwarding = false
                }
            } else {
                checkAutoLoginUnsuccessful()
                splashIllusionLayout.visibility = View.GONE
                OmnitureTrack.trackAction("login:unsuccessful:" + response.second.toString())
                if (isAutoForwarding == false) {

                    showGenericErrorDialog(this)
                }

                isAutoForwarding = false

            }
        } catch (t: Throwable) {
            checkAutoLoginUnsuccessful()
            splashIllusionLayout.visibility = View.GONE
        }

    }


    private fun checkAutoLoginUnsuccessful() {
        if (intent.getBooleanExtra(Constants.IS_AUTO_FORWARD, false)) {
            if (Preferences.getBoolean(PreferenceKeys.KEEP_ME_SIGNED_IN) == false) {
                txtEmail.setText("")
                txtPassword.setText("")
            }
        }

    }

    /**
     *  Factory method of activity, used to handler certain functionality as par need
     *  here its checking if LoginActivity is opening and IS_TIMEOUT flag is true in shared preference
     *  then, show Timeout message over top of the activity
     */

    override fun onResume() {
        super.onResume()
        init()

      /*  val currentActivity = Preferences.getValue(PreferenceKeys.CURRENT_ACTIVITY)
        if (currentActivity != Constants.LOGIN_ACTIVITY && currentActivity != Constants.SPLASH_ACTIVITY)
        {
            if (intent.getBooleanExtra("IS_TIME_ERROR", false))
            {
                if (intent.getBooleanExtra(Constants.AUTO_LOGIN, false) == true)
                {
                    logged_out_inactivity.visibility = View.GONE
                    this.autoLogin()
                } else
                    logged_out_inactivity.visibility = View.VISIBLE
                return
            }
        } else {
            logged_out_inactivity.visibility = View.GONE
        }*/


        setRememberMeData()
    }

    /**
     * Factory method of activity, executes when activity goes in paused state.
     *
     */
    override fun onPause() {
        super.onPause()
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver)
            ConnectivityReceiver.connectivityReceiverListener = null
        }
    }

    /**
     *  Factory method of activity, executes when activity is going to terminate.
     */
    override fun onDestroy() {
        super.onDestroy()

    }

     fun openUpdateScreen()
    {
        OmnitureTrack.trackAction("login:update-required-screen")
        val intent = Intent(this@LoginActivity, UpdateVersionActivity::class.java)
        startActivity(intent)
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_anim)
    }


    private fun prepareAppUpdateMessage() {
        var stringFirst =
            resources.getString(R.string.your_update_text)

        var stringSecond = resources.getString(
            R.string.get_update_msg
        )

        val spannable = SpannableString(stringFirst + " " + stringSecond)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
//                openUpdateVersionDialog()

//                openUpdateScreen()
               openPlayStore(this@LoginActivity)

                OmnitureTrack.trackAction("login:update-required-alert-get-update")

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

                if (updateTxt.isPressed()) {
                    ds.setColor(
                        ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.blue_900
                        )
                    );
                } else {
                    ds.setColor(
                        ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.checkBoxColor
                        )
                    );
                }
                updateTxt.invalidate();


//                ds.setColor(
//                    ContextCompat.getColor(
//                        this@LoginActivity,
//                        R.color.checkBoxColor
//                    )
//                );
//

                ds.setUnderlineText(false);

            }
        }
        updateTxt.setHighlightColor(Color.TRANSPARENT);
        spannable.setSpan(
            clickableSpan,
            stringFirst!!.length,
            stringFirst!!.length + stringSecond!!.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        updateTxt.text = spannable
        updateTxt.setMovementMethod(LinkMovementMethod.getInstance());
        if(Util.isAdaEnabled(this@LoginActivity))
        {
            updateTxt.setOnClickListener {
                openPlayStore(this@LoginActivity)
                OmnitureTrack.trackAction("login:update-required-alert-get-update")
            }
        }
        else
        {
            updateTxt.setOnClickListener(null)
        }
    }

    private fun callUpdateAPIProd() {
        // checkAPI=true
        val params = HashMap<String, Any>()

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.APP_VERSION_PROD,
            params,
            NetworkHandler.METHOD_POST,
            updateListener, null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    private fun callUpdateAPI() {
      // checkAPI=true
        val params = HashMap<String, Any>()

        val networkHandler = NetworkHandler(
            this,
            NetworkConstants.APP_VERSION,
            params,
            NetworkHandler.METHOD_POST,
            updateListener, null
        )

        networkHandler.setSilentMode(true)
        networkHandler.execute()
    }

    fun openNoAccessMobileScreen()
    {
        val intent = Intent(this@LoginActivity, NoAccessMobileActivity::class.java)
        startActivity(intent)

    }


    private val updateListener: NetworkListener = object : NetworkListener {
        override fun onNetworkResponse(response: Pair<String, Any>)
        {
            try {

                Util.trace("version first :  " + response.first)
                Util.trace("version second :  " + response.second)

                if (response.first == NetworkConstants.SUCCESS) {

                    val serverAppVersion = response.second.toString()
                    val versionCode = BuildConfig.VERSION_NAME.toString()
                    val splitValue = versionCode.split("\\s".toRegex())[0]
                    val appVersion = splitValue.replace(".", "")
                    val serVersion = serverAppVersion.replace(".", "")

                    val numServerVersion = serVersion.toDouble()
                    val localVersion = appVersion.toDouble()

                   if (numServerVersion > localVersion)
//                   if(true)
                    {
                        checkUpadateVersion = true
                        updateAlertLayout.visibility = View.VISIBLE
                        Util.announceAda(getString(R.string.ada_warning_msg_app_update), this@LoginActivity)
                        // Preferences.addBoolean(PreferenceKeys.CURRENT_APP_VERSION,true)
                        openUpdateScreen()

                        OmnitureTrack.trackAction("login:update-required-alert")

                    } else {
                        checkUpadateVersion = false
                        updateAlertLayout.visibility = View.GONE
                        // Preferences.addBoolean(PreferenceKeys.CURRENT_APP_VERSION,false)
                    }

                }
            }catch (t:Throwable)
            {
                Util.trace("Version compare issue : $t")
                t.printStackTrace()
            }
        }
    }



}

