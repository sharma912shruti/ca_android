package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.PictureLoader
import edu.capella.mobile.android.bean.ClassmatesListBean
import edu.capella.mobile.android.widgets.CPTextView


/**
 * ClassMatesListAdapter.kt : :  An adapter class to set items in list view
 *
 * Created by Jayesh Lahare on 1/3/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 1/3/2020.
 *
 */



class ClassMatesListAdapter() : RecyclerView.Adapter<ClassMatesListAdapter.MatesViewHolder>() {

    lateinit var context: Context
    lateinit var items: ArrayList<ClassmatesListBean.ClassmatesData.CourseMember.Member?>
    lateinit var matesItemListener: MatesItemListener
    lateinit var picLoader: PictureLoader

    constructor(ctx: Context, item_s: ArrayList<ClassmatesListBean.ClassmatesData.CourseMember.Member?>, matesItemListener_: MatesItemListener
    ) : this() {
        this.context = ctx
        this.items = item_s
        this.matesItemListener = matesItemListener_
        this.picLoader = PictureLoader(this.context)
        this.picLoader.clearCache()

    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface MatesItemListener {
        fun onItemClicked(value: ClassmatesListBean.ClassmatesData.CourseMember.Member?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MatesViewHolder {
        return MatesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.classmate_row_layout,
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
    override fun onBindViewHolder(parent: MatesViewHolder, position: Int) {

        val fName = items[position]?.firstName!!
        val lName = items[position]?.lastName!!

        parent.mateName.text = ("$fName $lName")
        this.picLoader.displayImage(items[position]?.profileImage!!, parent.matesImageLogo)

        parent.subLayout.setOnClickListener {
            matesItemListener.onItemClicked(items[position])
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
    class MatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matesImageLogo = view.findViewById<ImageView>(R.id.classmateSmallLogo)!!
        val mateName = view.findViewById<CPTextView>(R.id.classmateName)!!

        val subLayout = view.findViewById<LinearLayout>(R.id.classMateSubLayout)


    }

}