package edu.capella.mobile.android.activity.tabs

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson

import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.OmnitureTrack
import edu.capella.mobile.android.utils.PreferenceKeys
import edu.capella.mobile.android.utils.Preferences
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.LearnerInformation
import kotlinx.android.synthetic.main.fragment_idcard_tab.*


class IdCardTab : Fragment()  {


    lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        OmnitureTrack.trackState("user-profile:learner-ID")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_idcard_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    protected fun setIdCardProfileImage(filePath:String?)
    {
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            idProfileLogoImg.setImageBitmap(bitmap)
        }catch (t : Throwable){
            Util.trace("Profile Logo error $t")
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context

    }

    override fun onDetach() {
        super.onDetach()

    }

      fun updateProfileInfo()
    {
        OmnitureTrack.trackAction("user-profile:learner-ID")
        reloadData()
    }

    private fun reloadData()
    {
        try {
            OmnitureTrack.trackAction("user-profile:learner-ID")
            val gson = Gson()
            val learnerBean =   gson.fromJson<LearnerInformation>(Preferences.getValue(PreferenceKeys.LEARNER_PROFILE), LearnerInformation::class.java)

            if (learnerBean.errorData == null)
                {

                    if(learnerBean?.profileLearnerData?.profile?.firstName == null ||  learnerBean?.profileLearnerData?.profile?.lastName== null)
                    {
                        idcardTroubleTxt.visibility = View.VISIBLE
                        return
                    }

                    idcardTroubleTxt.visibility = View.GONE
                   if(learnerBean.profileLearnerData?.learnerProgram?.program == null)
                   {
                       noIdLayout.visibility= View.VISIBLE
                       idCardSubLayout.visibility= View.GONE
                   }else {

                       noIdLayout.visibility= View.GONE
                       idCardSubLayout.visibility= View.VISIBLE

                       idCardProfileName.text =
                           learnerBean?.profileLearnerData?.profile?.firstName + " " + learnerBean?.profileLearnerData?.profile?.lastName

                       idCardProfileId.text =getString(R.string.id_num) + learnerBean?.profileLearnerData?.profile?.employeeId

                       idCardProfileEmail.text =
                           "" + learnerBean?.profileLearnerData?.profile?.emailAddress?.toLowerCase()

                       //idCardProfileEffactiveDate.text =getString(R.string.effective_date) + " " + Util.getDateMMDDYYYY(learnerBean.profileLearnerData?.learnerProgram?.program?.effectiveDate.toString())
                       idCardProfileEffactiveDate.text =getString(R.string.effective_date) + " " + Util.getDateMMDDYYYY(learnerBean.profileLearnerData?.learnerProgram?.program?.matriculationDate.toString())

                       updateLogo()
                   }

            }

        }catch (t: Throwable)
        {

        }

    }

     fun updateLogo()
    {
        if(Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH) !="")
        {
            setIDProfileLogoImage(Preferences.getValue(PreferenceKeys.CACHED_LOGO_PATH))
        }

    }

    private fun setIDProfileLogoImage(filePath:String?)
    {
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            idProfileLogoImg.setImageBitmap(bitmap)
        }catch (t : Throwable){
            Util.trace("Logo error $t")
        }

    }

    fun isIDCardHaveFirstName(): String
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

}
