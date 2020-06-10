package edu.capella.mobile.android

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  09-05-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */
//class CourseToolActivityTest {
//
//    private val currentPkg = "edu.capella.mobile.android"
//    lateinit var baseActivity : Activity
//
//    @Rule
//    @JvmField
//    var activityRule: ActivityTestRule<LoginActivity> =
//        ActivityTestRule(LoginActivity::class.java)
//
//
//
//    @Before
//    fun initPreWork(){
//
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        Preferences.getInstance(context)
//        baseActivity = activityRule.activity
//
//        Espresso.onView(ViewMatchers.withId(R.id.txtEmail)).perform(
//            ViewActions.typeText("SSHARMA45"),
//            ViewActions.closeSoftKeyboard()
//        )
//        Espresso.onView(ViewMatchers.withId(R.id.txtPassword))
//            .perform(ViewActions.typeText("2402451Sh"), ViewActions.closeSoftKeyboard())
//
//        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click())
//
//
//        try {
//            Espresso.onView(ViewMatchers.withId(R.id.coachOkButton))
//                .check((ViewAssertions.matches(ViewMatchers.isDisplayed())))
//                .perform(ViewActions.click())
//        }catch (noView: Throwable)
//        {
//            //THIS IS OK AS WE HAVE COACH SCREEN ONLY FOR FIRST TIME, SO CATCH THIS
//            //EXCEPTION OTHERWISE WHOLE TEST WILL FAIL : JAYESH
//        }
//
//        Espresso.onView(ViewMatchers.withId(R.id.drawerImageView)).perform(ViewActions.click())
//        Espresso.onView(ViewMatchers.withText("Courses")).perform(ViewActions.click())
//
//
//        val cAct = getCurrentActivity() as CoursesActivity
//
//        Espresso.onView(ViewMatchers.withId(R.id.previousCourseListView))
//            .perform( RecyclerViewActions.actionOnItemAtPosition<CourseViewHolder>(cAct.getPreviousCourseListSize()-2,
//                ViewActions.click()
//            ))
//
//        Thread.sleep(1000)
//
//        Espresso.onView(ViewMatchers.withText("Course Tool"))
//            .perform(ViewActions.scrollTo(), ViewActions.click())
//
//        Thread.sleep(1500)
//    }
//
//
//    fun getCurrentActivity(): Activity? {
//        return (baseActivity.application as CapellaApplication).getCurrentRunningActivity()
//    }
//
//    @Test
//    fun check_title_is_threads() {
//        Espresso.onView(ViewMatchers.withId(R.id.genericTitleTxt))
//            .check(ViewAssertions.matches(ViewMatchers.withText("Course Tool")))
//    }
//
//    @Test
//    fun check_course_information_is_available_for_processing()
//    {
//        var activity = getCurrentActivity() as CourseToolActivity
//        Assert.assertNotNull(activity.courseToolAdapter)
//        Assert.assertNotNull(activity.list)
//    }
//
//    @Test
//    fun check_list_item_contain_list(){
//        var activity = getCurrentActivity() as CourseToolActivity
//        Assert.assertTrue(activity.getCourseToolListSize() >= 0)
//    }
//
//    @After
//    fun cleanUp() {
//        // Intents.release()
//    }
//}