package edu.capella.mobile.android.activity.tabs

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import edu.capella.mobile.android.BuildConfig
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.*
import edu.capella.mobile.android.activity.ProfileActivity
import edu.capella.mobile.android.bean.LearnerInformation
import edu.capella.mobile.android.network.NetworkConstants
import edu.capella.mobile.android.task.StickyInfoGrabber
import kotlinx.android.synthetic.main.fragment_profile_tab.*


class ProfileTab : Fragment() {


   lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        OmnitureTrack.trackState("user-profile")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        //callProfileDetailApi()

    }



    fun updateProfileInfo()
    {

        reloadData()
    }
    private fun initViews()
    {
        //profileSwipeToRefresh.setSwipeableChildren(R.id.profileTabScroller, android.R.id.empty);

        editProfileEmailTxt.setOnClickListener{
            val task = StickyInfoGrabber(activityContext)
           // task.generateMuleSoftStickySessionForTargetUrl(NetworkConstants.EDIT_EMAIL_ON_CAMPUS , BuildConfig.STICKY_FORWARD_URL)
            DialogUtils.screenNamePrefix = "user-profile:edit-email-link-out"
            task.validateAndOpenUrl(NetworkConstants.EDIT_EMAIL_ON_CAMPUS , BuildConfig.STICKY_FORWARD_URL)
        }

        viewEditProfileTxt.setOnClickListener{
            val task = StickyInfoGrabber(activityContext)
           // task.generateMuleSoftStickySessionForTargetUrl(NetworkConstants.VIEW_EDIT_PROFILE_ON_CAMPUS , BuildConfig.STICKY_FORWARD_URL)
            DialogUtils.screenNamePrefix = "user-profile:view-edit-profile-link-out"
            task.validateAndOpenUrl(NetworkConstants.VIEW_EDIT_PROFILE_ON_CAMPUS , BuildConfig.STICKY_FORWARD_URL)
        }


        handleScrollView()
    }
    private fun handleScrollView()
    {
        val listener: OnScrollChangedListener

        listener = OnScrollChangedListener {
            val scrollY: Int = profileTabScroller.getScrollY()
            if (scrollY < 50)
            { //threshold
                (activity as ProfileActivity).enablePullToRefresh(true)
            } else {
                (activity as ProfileActivity).enablePullToRefresh(false)
                //profileSwipeToRefresh.setEnabled(false)
            }
        }

        profileTabScroller.getViewTreeObserver().addOnScrollChangedListener(listener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context

    }

    override fun onDetach() {
        super.onDetach()

    }



//    protected fun setProfileImage(filePath:String?)
//    {
//        try {
//            val bitmap = BitmapFactory.decodeFile(filePath)
//            profileLogoImg.setImageBitmap(bitmap)
//        }catch (t : Throwable){
//            Util.trace("Profile Logo error $t")
//        }
//
//    }

    private fun reloadData()
    {
        try {


            val gson = Gson()
            val learnerBean =   gson.fromJson<LearnerInformation>(Preferences.getValue(PreferenceKeys.LEARNER_PROFILE), LearnerInformation::class.java)



            if (learnerBean.errorData == null)
            {
                if(learnerBean?.profileLearnerData?.profile?.firstName == null ||  learnerBean?.profileLearnerData?.profile?.lastName== null)
                {
                    profileTroubleTxt.visibility = View.VISIBLE
                    return
                }
                profileTroubleTxt.visibility = View.GONE
                profileTabScroller.visibility = View.VISIBLE

                profileNameTxt.text =
                    learnerBean?.profileLearnerData?.profile?.firstName + " " + learnerBean?.profileLearnerData?.profile?.lastName

                profileIdTxt.text = "ID# " + learnerBean?.profileLearnerData?.profile?.employeeId

                profileAddressTxt.text =
                    "" + learnerBean?.profileLearnerData?.profile?.city + ", " + learnerBean?.profileLearnerData?.profile?.state

                profileEmailTxt.text = "" + learnerBean?.profileLearnerData?.profile?.emailAddress!!.toLowerCase()

                if(learnerBean?.profileLearnerData?.profile?.aboutMe != null && learnerBean?.profileLearnerData?.profile?.aboutMe!!.isNotEmpty())
                {
                    profileTabAboutLayout.visibility =  View.VISIBLE


                    var aboutMeTxt = Html.fromHtml( learnerBean?.profileLearnerData?.profile?.aboutMe.toString()  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
                    aboutMeTxt = filterBRToPlain(aboutMeTxt)

                   // profileAboutText.text =Html.fromHtml( learnerBean?.profileLearnerData?.profile?.aboutMe.toString()  , Html.FROM_HTML_MODE_LEGACY).toString().trim()
                    profileAboutText.text =aboutMeTxt
                }else
                {
                    profileTabAboutLayout.visibility =  View.GONE
                }
                updateLogo()

            } else
            {
                DialogUtils.showGenericErrorDialog(activityContext)
            }
        }catch (t: Throwable)
        {
                Util.trace("Profile loading issue")
               DialogUtils.showGenericErrorDialog(activityContext)
        }


    }

    fun isProfileHaveFirstName(): String
    {
        try {

            val gson = Gson()
            val learnerBean =   gson.fromJson<LearnerInformation>(Preferences.getValue(PreferenceKeys.LEARNER_PROFILE), LearnerInformation::class.java)

            return learnerBean?.profileLearnerData?.profile?.firstName.toString()
        }catch (t:Throwable)
        {
            return ""
        }
    }


    fun updateLogo()
    {
        if(Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH) !="")
        {
            setProfileLogoImage(Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH))
        }

    }

    private fun setProfileLogoImage(filePath:String?)
    {
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            profileLogoImg.setImageBitmap(bitmap)
        }catch (t : Throwable){
            Util.trace("Logo error $t")
        }

    }

    private fun filterBRToPlain(text:String):String
    {
        var tags1 = arrayListOf<String>("<br>", "< br>", "< br>","< br >","</br>","</ br>","< /br>","< / br>")

        var newAboutMe = text
        for(item in tags1)
        {
            if(newAboutMe.contains(item , true))
            {
                newAboutMe = newAboutMe.replace(item , "")
            }
        }


        return newAboutMe

    }

    private fun filterHtmlToPlain(text:String) :String
    {
        var tags1 = arrayListOf<String>("<html>", "<head>", "<body>","<B>","<I>","<U>","<strong>","<h1>", "<h2>", "<h3>","<h4>","<h5>","<h6>","<em>","<a>", "</a>", "<img>", "<area>", "<map>","<ul>","<ol>","<li>","<dl>","<dd>", "<dt>","<table>","<tr>","<th>","<td>","<span>","</span>","<p>","</p>","<p style>",   "<span style","<sub>","</sub>","<sup>","</sup>","<hr>","<hr width",  "</hr>")
        var tags2 = arrayListOf<String>("</html>", "</head>", "</body>","</B>","</I>","</U>","</strong>","</h1>", "</h2>", "</h3>","</h4>","</h5>","</h6>","</em>","</a>", "< /a>", "</img>", "</area>", "</map>","</ul>","</ol>","</li>","</dl>","</dd>", "</dt>","</table>","</tr>","</th>","</td>","<span >","</span>","<p >","</p>","<p style>",   "<span style","<sub >","</sub>","</ sup>","< /sup>","</ hr>","< /hr>"  ,"</hr>")


        var newAboutMe = text
        for(item in tags1)
        {
            if(newAboutMe.contains(item , true))
            {
                newAboutMe = newAboutMe.replace(item , "")
            }
        }

        for(item in tags2)
        {
            if(newAboutMe.contains(item , true))
            {
                newAboutMe = newAboutMe.replace(item , "")
            }
        }

        return newAboutMe
    }

}
