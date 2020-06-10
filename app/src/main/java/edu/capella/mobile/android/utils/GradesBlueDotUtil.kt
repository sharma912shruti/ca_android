package edu.capella.mobile.android.utils

import com.google.gson.Gson
import edu.capella.mobile.android.bean.VisitedGradesStatusHistory

/**
 * Class Name.kt : class description goes here
 *
 * @author  :  jayesh.lahare
 * @version :  1.0
 * @since   :  4/24/2020
 *
 *
 */
object GradesBlueDotUtil
{

    fun init()
    {
        var visited = Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY)
        if(visited== "") // Initialize
        {
            var newObject: VisitedGradesStatusHistory = VisitedGradesStatusHistory()
            var gson = Gson()
            var json = gson.toJson(newObject)
            Preferences.addValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY, json)


            var vistedObject: VisitedGradesStatusHistory = gson.fromJson(
                Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY),
                VisitedGradesStatusHistory::class.java
            )

            if (vistedObject?.visited_assignment_items == null) {
                vistedObject?.visited_assignment_items =
                    VisitedGradesStatusHistory.VisitedAssignmentItems()
            }

            if (vistedObject?.visited_assignment_items?.assignments == null) {
                vistedObject?.visited_assignment_items?.assignments =
                    ArrayList<VisitedGradesStatusHistory.VisitedAssignmentItems.Assignment?>()
            }

            if (vistedObject?.course_identifiers == null) {
                vistedObject?.course_identifiers =
                    ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>()
            }

            var jsonString = gson.toJson(vistedObject)
            Preferences.addValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY, jsonString)
        }
    }

    fun markAsRead(id: String?, gradedDateTime: String?, submittedDateTime: String?, statusDateTime: String?)
    {
        /* initVisitedHistory()*/
        GradesBlueDotUtil.init()
        var gson = Gson( )
        var vistedObject : VisitedGradesStatusHistory =  gson.fromJson(Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY) , VisitedGradesStatusHistory::class.java )


        var empId  = Preferences.getValue( PreferenceKeys.USER_ID)

        if(empId!="" && vistedObject.employeeId !=null)
        {
            if(empId?.contains(vistedObject!!.employeeId!!) == false)
            {
                Preferences.removeKey(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY.toString())
                GradesBlueDotUtil.init()
            }
        }

        vistedObject?.employeeId = empId

        var courses: ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>? =   vistedObject?.course_identifiers  //as  ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>?

        var currentCourseId:String = Preferences.getValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER)!!
        if(courses!=null)
        {
            var isFound: Boolean = false
            for(item in courses)
            {
                if(item?.course_identifier!!.contains(currentCourseId))
                {
                    isFound = true
                }
            }
            if(isFound == false)
            {
                var cid : VisitedGradesStatusHistory.CourseIdentifier = VisitedGradesStatusHistory.CourseIdentifier(currentCourseId)
                courses.add(cid)
            }
        }

        vistedObject?.course_identifiers = courses as  ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>?

        var idLookingCurrently:String? =  id
        var gradeDateLooking:String? = gradedDateTime
        var submittedDateLooking:String? = submittedDateTime
        var statusDateLooking:String? = statusDateTime

        var listArray: ArrayList<VisitedGradesStatusHistory.VisitedAssignmentItems.Assignment?>? =  vistedObject?.visited_assignment_items?.assignments



        var isFound = false
        if(listArray!=null) {
            for ((index , item) in listArray!!.withIndex())
            {
                if (item?.id!!.contains(idLookingCurrently!!, true))
                {
                    Util.trace("Duplicate : " + item?.id)
                    isFound = false
                    listArray.removeAt(index)
                    break
                }
            }
        }

        if(isFound == false)
        {
            var assign = VisitedGradesStatusHistory.VisitedAssignmentItems.Assignment(idLookingCurrently , gradeDateLooking , submittedDateLooking , statusDateLooking)


            vistedObject?.visited_assignment_items?.assignments!!.add(assign)
        }

        var jsonToWrite = gson.toJson(vistedObject)
        Preferences.addValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY , jsonToWrite)

        Util.trace("JSON is : $jsonToWrite")

    }


    fun markAsReadFaculty(id: String?, gradedDateTime: String?, submittedDateTime: String?, statusDateTime: String?)
    {
        /* initVisitedHistory()*/
        GradesBlueDotUtil.init()
        var gson = Gson( )
        var vistedObject : VisitedGradesStatusHistory =  gson.fromJson(Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY) , VisitedGradesStatusHistory::class.java )


        var empId  = Preferences.getValue( PreferenceKeys.USER_ID)

        if(empId!="" && vistedObject.employeeId !=null)
        {
            if(empId?.contains(vistedObject!!.employeeId!!) == false)
            {
                Preferences.removeKey(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY.toString())
                GradesBlueDotUtil.init()
            }
        }

        vistedObject?.employeeId = empId

        var courses: ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>? =   vistedObject?.course_identifiers  //as  ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>?

        var currentCourseId:String = Preferences.getValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER)!!
        if(courses!=null)
        {
            var isFound: Boolean = false
            for(item in courses)
            {
                if(item?.course_identifier!!.contains(currentCourseId))
                {
                    isFound = true
                }
            }
            if(isFound == false)
            {
                var cid : VisitedGradesStatusHistory.CourseIdentifier = VisitedGradesStatusHistory.CourseIdentifier(currentCourseId)
                courses.add(cid)
            }
        }

        vistedObject?.course_identifiers = courses as  ArrayList<VisitedGradesStatusHistory.CourseIdentifier?>?

        var idLookingCurrently:String? =  id
        var gradeDateLooking:String? = gradedDateTime
        var submittedDateLooking:String? = submittedDateTime
        var statusDateLooking:String? = statusDateTime

        var listArray: ArrayList<VisitedGradesStatusHistory.VisitedAssignmentItems.Assignment?>? =  vistedObject?.visited_assignment_items?.assignments



        var isFound = false
        /*if(listArray!=null)
        {
            for ((index , item) in listArray!!.withIndex())
            {
                if (item?.id!!.contains(idLookingCurrently!!, true) && item?.submitDate)
                {
                    Util.trace("Duplicate : " + item?.id)
                    isFound = false
                    listArray.removeAt(index)
                    break
                }
            }
        }*/

        if(isFound == false)
        {
            var assign = VisitedGradesStatusHistory.VisitedAssignmentItems.Assignment(idLookingCurrently , gradeDateLooking , submittedDateLooking , statusDateLooking)


            vistedObject?.visited_assignment_items?.assignments!!.add(assign)
        }

        var jsonToWrite = gson.toJson(vistedObject)
        Preferences.addValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY , jsonToWrite)

        Util.trace("JSON is : $jsonToWrite")

    }

    fun isUnRead(id: String?, gradedDateTime: String?, submittedDateTime: String?, statusDateTime: String?): Boolean
    {
        try {
            var visited = Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY)
            if (visited == "") // Initialize
            {
                return true
            }
            init()
            var gson = Gson()
            var vistedObject: VisitedGradesStatusHistory = gson.fromJson(Preferences.getValue(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY), VisitedGradesStatusHistory::class.java)

            var empId = Preferences.getValue(PreferenceKeys.USER_ID)

            if (empId != "" && vistedObject.employeeId != null) {
                if (empId?.contains(vistedObject!!.employeeId!!) == false) {
                    Preferences.removeKey(PreferenceKeys.VISITED_GRADES_STATUS_HISTORY.toString())
                    init()
                }
            }

            var isSelectedCourseCorrect = false;
            var isCourseFound = false
            for (cors in vistedObject?.course_identifiers!!)
            {
                if(cors?.course_identifier!= null && cors?.course_identifier!!.contains(Preferences.getValue(PreferenceKeys.SELECTED_COURSE_IDENTIFIER)!! , true))
                {
                    Util.trace("Due to course retrun true")
                    isCourseFound = true
                }
            }

            if(isCourseFound == false)
            {
                Util.trace("1 Due to course retrun true")
                return true // ITS FRESH COURSE OPENES SO ITS UnRead
            }

            for(items in vistedObject?.visited_assignment_items?.assignments!!)
            {
                if(items?.id == id && items?.gradeDate == gradedDateTime && items?.submitDate == submittedDateTime && items?.statusDate == statusDateTime)
                {
                    Util.trace("items found retrun false")
                    return false
                }
            }



        }catch (t: Throwable)
        {
            Util.trace("isUnread Error : $t")
            t.printStackTrace()
        }
        Util.trace("items default return true")
        return true // By Default UnRead = true

    }

}