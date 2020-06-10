package edu.capella.mobile.android.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.AssignmentsBean
import edu.capella.mobile.android.widgets.CPTextView


/**
 * AssignmentsListAdapter.kt : :  An adapter class to set Assignment items in list view
 *
 * Created by Didarul Khan on 04/04/2020.
 *
 * @author Didarul.Khan
 * @version 1.0
 * @since 01/27/2020.
 *
 */



class AssignmentsListAdapter() : RecyclerView.Adapter<AssignmentsListAdapter.AssignmentsViewHolder>() {

    lateinit var context: Context
    lateinit var items: ArrayList<AssignmentsBean.CourseAssignment?>
    private lateinit var assignmentsItemListener: AssignmentsItemListener


    constructor(ctx: Context,
                item_s: ArrayList<AssignmentsBean.CourseAssignment?>,
                listener: AssignmentsItemListener)
            :  this() {
        this.context = ctx
        this.items = item_s
        this.assignmentsItemListener = listener
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface AssignmentsItemListener {
        fun onItemClicked(value: AssignmentsBean.CourseAssignment?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AssignmentsViewHolder {
        return AssignmentsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.assignments_row_layout,
                parent,
                false
            )
        )
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: AssignmentsViewHolder, position: Int) {

        val tName = Html.fromHtml( Util.str(items[position]?.title!!) , Html.FROM_HTML_MODE_LEGACY).toString().trim()


        parent.assignmentsTitle.text = tName
        //this.picLoader.displayImage(items[position]?.profileImage!!, parent.matesImageLogo)

        parent.assignmentsRowParent.setOnClickListener{
            Util.trace("Clicked..")
            this.assignmentsItemListener.onItemClicked(items[position])
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


    /**
     * An ViewHolder class working as a placeholder for items used in list.
     *
     * @param view
     * @see RecyclerView.ViewHolder
     *
     */
    class AssignmentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val assignmentsTitle: TextView = view.findViewById<CPTextView>(R.id.assignmentsTitle)
        val assignmentsRowParent: LinearLayout = view.findViewById(R.id.assignmentsRowParent)



    }

}
