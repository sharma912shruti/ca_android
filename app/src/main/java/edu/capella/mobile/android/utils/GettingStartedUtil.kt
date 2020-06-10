package edu.capella.mobile.android.utils

import com.google.gson.Gson
import edu.capella.mobile.android.bean.GettingStartedCache


/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/24/2020
 *
 *
 */
object GettingStartedUtil
{

    fun init()
    {
        var visited = Preferences.getValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY)
        if(visited== "") // Initialize
        {
            var newObject: GettingStartedCache = GettingStartedCache()
            var gson = Gson()
            var json = gson.toJson(newObject)
            Preferences.addValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY, json)


            var vistedObject: GettingStartedCache = gson.fromJson(
                Preferences.getValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY),
                GettingStartedCache::class.java
            )

            if(vistedObject?.course_ids ==null) {
                vistedObject?.course_ids = ArrayList<GettingStartedCache.CourseId?>()
            }



            var jsonString = gson.toJson(vistedObject)
            Preferences.addValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY, jsonString)
        }
    }

    fun markAsCached(cid: String? , isVisible:Boolean )
    {
        /* initVisitedHistory()*/
        GettingStartedUtil.init()
        var gson = Gson( )
        var vistedObject : GettingStartedCache =  gson.fromJson(Preferences.getValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY) , GettingStartedCache::class.java )

        var empId  = Preferences.getValue( PreferenceKeys.USER_ID)

        if(empId!="" && vistedObject.empid !=null)
        {
            if(empId?.contains(vistedObject!!.empid!!) == false)
            {
                Preferences.removeKey(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY.toString())
                GettingStartedUtil.init()
            }
        }

        vistedObject?.empid = empId

        var courses: ArrayList<GettingStartedCache.CourseId?>? =   vistedObject?.course_ids  //as  ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>?


        if(courses!=null)
        {
            var isFound: Boolean = false
            for( (index , item) in courses.withIndex())
            {
                if(item?.course_id!!.contains(cid.toString()))
                {

                    courses.removeAt(index)
                }
            }
            if(isFound == false)
            {
                var ncid = GettingStartedCache.CourseId()
                ncid.course_id = cid.toString()
                ncid.visibile = isVisible

                courses.add(ncid)
            }
        }

        vistedObject?.course_ids = courses as  ArrayList<GettingStartedCache.CourseId?>?

        var jsonToWrite = gson.toJson(vistedObject)
        Preferences.addValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY , jsonToWrite)

        Util.trace("GS JSON is : $jsonToWrite")

    }


    fun isInCache(cid: String?): GettingStartedCache.CourseId?
    {
        try
        {
            var visited = Preferences.getValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY)
            if (visited == "") // Initialize
            {
                return null // Not found
            }
            init()
            var gson = Gson()
            var vistedObject: GettingStartedCache = gson.fromJson(Preferences.getValue(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY), GettingStartedCache::class.java)

            var empId = Preferences.getValue(PreferenceKeys.USER_ID)

            if (empId != "" && vistedObject.empid != null) {
                if (empId?.contains(vistedObject!!.empid!!) == false) {
                    Preferences.removeKey(PreferenceKeys.VISITED_GETTING_STARTED_HISTORY.toString())
                    init()
                }
            }

            var isSelectedCourseCorrect = false;
            var isCourseFound = false
            for (cors in vistedObject?.course_ids!!)
            {
                if(cors?.course_id!= null && cors?.course_id!!.contains(cid!! , true))
                {
                     return cors
                }
            }
             return null
        }catch (t: Throwable)
        {
            Util.trace("isGetting Error : $t")
            t.printStackTrace()
        }
        return null
    }

}