package edu.capella.mobile.android.junit.beans

import com.google.gson.Gson
import edu.capella.mobile.android.bean.GradeFacultyBean
import edu.capella.mobile.android.bean.GradeStudentBean
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test


class GradeStudentBeanTest {

   var gradeStudentBean  =  GradeStudentBean()

    @Before
    fun setFakeData()
    {
        gradeStudentBean  =  GradeStudentBean()

    }

    @Test
    fun check_for_getter_and_setter()
    {

        gradeStudentBean.courseActivityGrade = null
        gradeStudentBean.serverName= null
        gradeStudentBean.trackingId= null

        assertNull(gradeStudentBean.courseActivityGrade)
        assertNull(gradeStudentBean.serverName)
        assertNull(gradeStudentBean.trackingId)


        var assignmentGrade = GradeStudentBean.CourseActivityGrade.AssignmentGrade()

        assignmentGrade.facultyComments = null
        assignmentGrade.gradedDate = null
        assignmentGrade.id = null
        assignmentGrade.instructionLink = null
        assignmentGrade.learnerComments = null
        assignmentGrade.score = null
        assignmentGrade.status = null
        assignmentGrade.submittedDate = null
        assignmentGrade.title = null
        assignmentGrade.totalPossibleScore = null
        assignmentGrade.webLink = null

        assertNull(assignmentGrade.facultyComments)
        assertNull(assignmentGrade.gradedDate)
        assertNull( assignmentGrade.id)
        assertNull( assignmentGrade.instructionLink)
        assertNull(assignmentGrade.learnerComments)
        assertNull(assignmentGrade.score)
        assertNull(assignmentGrade.status)
        assertNull(assignmentGrade.submittedDate)
        assertNull(assignmentGrade.title)
        assertNull(assignmentGrade.totalPossibleScore)
        assertNull(assignmentGrade.webLink)

        var blogGrade = GradeStudentBean.CourseActivityGrade.BlogsGrade()
        blogGrade.gradedDate = null
        blogGrade.id = null
        blogGrade.instructionLink = null
        blogGrade.score= null
        blogGrade.status = null
        blogGrade.statusDateTime = null
        blogGrade.submittedDate = null
        blogGrade.title = null
        blogGrade.totalPossibleScore= null
        blogGrade.webLink= null

        assertNull(blogGrade.gradedDate)
        assertNull(blogGrade.id)
        assertNull(blogGrade.instructionLink)
        assertNull(blogGrade.score)
        assertNull(blogGrade.status)
        assertNull(blogGrade.statusDateTime)
        assertNull(blogGrade.submittedDate)
        assertNull(blogGrade.title)
        assertNull(blogGrade.totalPossibleScore)
        assertNull(blogGrade.webLink)

        var discussionGrade = GradeStudentBean.CourseActivityGrade.DiscussionsGrade()
        discussionGrade.forumWebLink = null
        discussionGrade.statusDateTime = null
        discussionGrade.id = null
        discussionGrade.instructionLink = null
        discussionGrade.score = null
        discussionGrade.status = null
        discussionGrade.gradedDate = null
        discussionGrade.submittedDate = null
        discussionGrade.title = null
        discussionGrade.totalPossibleScore = null

        assertNull(discussionGrade.forumWebLink)
        assertNull(discussionGrade.statusDateTime)
        assertNull(discussionGrade.id)
        assertNull(discussionGrade.instructionLink)
        assertNull(discussionGrade.score)
        assertNull(discussionGrade.status)
        assertNull(discussionGrade.gradedDate)
        assertNull(discussionGrade.submittedDate)
        assertNull(discussionGrade.title)
        assertNull(discussionGrade.totalPossibleScore)

        var journalsGrade = GradeStudentBean.CourseActivityGrade.JournalsGrade()
        journalsGrade.id = null
        journalsGrade.instructionLink = null
        journalsGrade.score = null
        journalsGrade.status = null
        journalsGrade.title = null
        journalsGrade.gradedDate = null
        journalsGrade.statusDateTime = null
        journalsGrade.submittedDate = null
        journalsGrade.totalPossibleScore = null
        journalsGrade.webLink = null

        assertNull(journalsGrade.id)
        assertNull(journalsGrade.instructionLink)
        assertNull(journalsGrade.score)
        assertNull(journalsGrade.status)
        assertNull(journalsGrade.title)
        assertNull(journalsGrade.gradedDate)
        assertNull(journalsGrade.statusDateTime)
        assertNull(journalsGrade.submittedDate)
        assertNull(journalsGrade.totalPossibleScore)
        assertNull(journalsGrade.webLink)


        var quizzesGrade = GradeStudentBean.CourseActivityGrade.QuizzesGrade()
        quizzesGrade.facultyComments = null
        quizzesGrade.gradedDate = null
        quizzesGrade.id = null
        quizzesGrade.instructionLink = null
        quizzesGrade.score = null
        quizzesGrade.status = null
        quizzesGrade.statusDateTime = null
        quizzesGrade.submittedDate = null
        quizzesGrade.title = null
        quizzesGrade.totalPossibleScore = null
        quizzesGrade.webLink = null

        assertNull(quizzesGrade.facultyComments)
        assertNull(quizzesGrade.gradedDate)
        assertNull(quizzesGrade.id)
        assertNull(quizzesGrade.instructionLink)
        assertNull(quizzesGrade.score)
        assertNull(quizzesGrade.status)
        assertNull(quizzesGrade.statusDateTime)
        assertNull(quizzesGrade.submittedDate)
        assertNull(quizzesGrade.title)
        assertNull(quizzesGrade.totalPossibleScore)
        assertNull(quizzesGrade.webLink)

    }

    @Test
    fun check_for_toString()
    {
        assertNotNull(gradeStudentBean.toString())
    }
    @Test
    fun check_for_hashCode()
    {
        assertNotNull(gradeStudentBean.hashCode())
    }



    @Test
    fun check_whether_bean_is_gson_supported()
    {
        var gson = Gson( )
        var json = gson.toJson(gradeStudentBean)

        var newBean = gson.fromJson(json , GradeStudentBean::class.java )

        assertEquals(gradeStudentBean,newBean)

    }

    @Test
    fun check_whether_bean_should_not_match_with_different_data()
    {
        var gson = Gson( )
        var json = gson.toJson(gradeStudentBean)

        var newBean = gson.fromJson(json , GradeStudentBean::class.java )
        newBean.serverName = "New server name"

        assertNotEquals(gradeStudentBean,newBean)

    }




}
