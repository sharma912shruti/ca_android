package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.PictureLoader
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.CourseListBean
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.widgets.CPTextView

/**
 * CourseListAdapter.kt : :  An adapter class to set items in list view
 *
 * Created by Jayesh Lahare on 1/3/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 1/3/2020.
 *
 */



class CourseListAdapter() : RecyclerView.Adapter<CourseViewHolder>() {

    lateinit  var context: Context
    lateinit  var items: ArrayList<CourseListBean.NewCourseroomData.CurrentCourseEnrollment?>
    lateinit  var courseItemListener: CourseItemListener
    lateinit var picLoader : PictureLoader
    constructor(
          ctx: Context,

            item_s: ArrayList<CourseListBean.NewCourseroomData.CurrentCourseEnrollment?>,
            courseItemListener_: CourseItemListener) : this() {
        this.context = ctx
        this.items = item_s
        this.courseItemListener = courseItemListener_
        this.picLoader = PictureLoader(this.context)
       this.picLoader.clearCache()

    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface CourseItemListener {
        fun onItemClicked(value: CourseListBean.NewCourseroomData.CurrentCourseEnrollment?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CourseViewHolder {
        return CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.courses_list_row, parent, false))
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: CourseViewHolder, position: Int) {

        if(items[position]?.courseSection?.course?.subject.equals(Constants.UOS_COURSE) || items[position]?.courseSection?.course?.subject.equals(Constants.FPO_COURSE)){
            parent.instructorLayout.visibility = View.GONE
        }else{
            parent.instructorLayout.visibility = View.VISIBLE
        }


        parent.courseIdentifier.text=  items[position]?.courseSection?.course?.number.toString()
        parent.courseTitle.text=  items[position]?.courseSection?.course?.title.toString()

        parent.courseTagLine.text=   items[position]?.courseSection?.section.toString()

        try {
            picLoader.displayImage(
                items[position]?.courseSection?.instructor?.profileImage!!,
                parent.instructorLogoImg
            )
        }catch(t:Throwable)
        {
            Util.trace("Logo crash  $t")
        }

        parent.instructorNameTxt.text=  items[position]?.courseSection?.instructor?.fullName.toString()
        parent.currentCourseParentAccount.setOnClickListener {
            courseItemListener.onItemClicked(items[position])
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return items.size
    }
}

/**
 * An ViewHolder class working as a placeholder for items used in list.
 *
 * @param view
 * @see RecyclerView.ViewHolder
 *
 */
class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    val courseIdentifier: TextView = view.findViewById<CPTextView>(R.id.courseIdentifier)
    val courseTitle: TextView = view.findViewById<CPTextView>(R.id.courseTitle)!!
    val courseTagLine: TextView = view.findViewById<CPTextView>(R.id.courseTagLine)!!
//    val startDate = view.findViewById<CPTextView>(R.id.startDate)!!
//    val endDate = view.findViewById<CPTextView>(R.id.endDate)!!
//    val section = view.findViewById<CPTextView>(R.id.section)!!
    val instructorLogoImg: ImageView = view.findViewById<ImageView>(R.id.instructorLogoImg)
    val instructorNameTxt: TextView = view.findViewById<CPTextView>(R.id.instructorNameTxt)
    val currentCourseParentAccount:LinearLayout = view.findViewById<LinearLayout>(R.id.course_current_parent_layout)
    val instructorLayout :LinearLayout = view.findViewById(R.id.instructor_layout)


}
