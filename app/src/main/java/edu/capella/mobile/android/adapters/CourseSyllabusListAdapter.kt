package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.CourseSyllabusBean
import edu.capella.mobile.android.widgets.CPTextView


/**
 * CourseSyllabusListAdapter.kt : :  An adapter class to set syllabus items in list view
 *
 * Created by Didarul Khan on 03/17/2020.
 *
 * @author Didarul Khan
 * @version 1.0
 * @since 03/17/2020.
 *
 */



class CourseSyllabusListAdapter() : RecyclerView.Adapter<CourseSyllabusListAdapter.SyllabusViewHolder>() {

    lateinit var context: Context
    lateinit var items: ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent?>
    lateinit var syllabusItemListener: SyllabusItemListener


    constructor(ctx: Context,
                item_s: ArrayList<CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent?>,
                syllabusClickListener: SyllabusItemListener)
     :  this() {
        this.context = ctx
        this.items = item_s
        this.syllabusItemListener = syllabusClickListener
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface SyllabusItemListener {
        fun onItemClicked(value: CourseSyllabusBean.CourseSyllabus.CourseSyllabusContents.CourseSyllabusContent?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SyllabusViewHolder {
        return SyllabusViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.syllabus_row_layout,
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
    override fun onBindViewHolder(parent: SyllabusViewHolder, position: Int) {

       val tName = items[position]?.title!!


        parent.syllabusTitle.text = ("$tName")
        //this.picLoader.displayImage(items[position]?.profileImage!!, parent.matesImageLogo)

        parent.syllabusRowParent.contentDescription =  tName + context.getString(R.string.action_available)
        parent.syllabusRowParent.setOnClickListener {
            syllabusItemListener.onItemClicked(items[position])
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
    class SyllabusViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val syllabusTitle = view.findViewById<CPTextView>(R.id.syllabusTitle)!!
        val syllabusRowParent = view.findViewById<LinearLayout>(R.id.syllabusRowParent)!!



    }

}
