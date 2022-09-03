package com.fscsoftware.managetasks.ui.custom_views

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView

/*
* Works sometimes, it should be advantage for be used in next versions.
* Current Problems:
*   1. adapter recycler view not notify changes
*   2. view Children size overflow (if size is bigger tha size parent it overflow is showing)
*   3. Not tested enough yet
* */
class FlowAutoSizeLayout : RecyclerView.LayoutManager() {

    private var realWidth = 0

    /*
    * Execute before onLayoutChildren to define layout width and height
    * In this case as RecyclerView.LayoutManager it is executed for each view
    * */
    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {

        detachAndScrapAttachedViews(recycler)
        realWidth = View.MeasureSpec.getSize(widthSpec)

        /*
        * When all views have been process, the state change their item count at last item
        * when we can get the last item we can iterate and get children view
        * */
        val viewCount = state.itemCount
        if (viewCount <= 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
            return
        }

        var currentHeight = 0
        var currentWidth = 0

        var left = 0
        var top = 0

        for (i in 0 until viewCount) {

            val view = recycler.getViewForPosition(i)
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            //check if child can be placed in the current row, else go to next line
            if (left + view.measuredWidth > realWidth) {
                //new line
                currentWidth = Math.max(currentWidth, left)

                //reset for new line
                left = 0
                top += view.measuredHeight
            }

            currentHeight = Math.max(currentHeight, top + view.measuredHeight)

            left += view.measuredWidth
        }

        currentWidth = Math.max(left, currentWidth)

        /*
        * It assign layout dimensions, so what it should be all measures of their children views
        * */
        setMeasuredDimension(
            RecyclerView.resolveSize(currentWidth, widthSpec),
            RecyclerView.resolveSize(currentHeight, heightSpec)
        )

    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        //Repaint views from recycler view
        detachAndScrapAttachedViews(recycler)

        var currentHeight = 0
        var currentWidth = 0

        var left = 0
        var top = 0

        val viewCount = state.itemCount
        for (i in 0 until viewCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            //check if child can be placed in the current row, else go to next line
            if (left + view.measuredWidth > realWidth) {
                //new line
                currentWidth = Math.max(currentWidth, left)

                //reset for new line
                left = 0
                top += view.measuredHeight
            }

            view.layout(left
                , top
                , left + view.measuredWidth
                , top + view.measuredHeight
            )

            currentHeight = Math.max(currentHeight, top + view.measuredHeight)

            left += view.measuredWidth
        }

    }


    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }


}

