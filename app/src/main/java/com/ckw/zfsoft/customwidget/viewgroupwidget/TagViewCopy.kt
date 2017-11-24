package com.ckw.zfsoft.customwidget.viewgroupwidget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by ckw
 * on 2017/11/22.
 */
class TagViewCopy(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val suggestWidth = MeasureSpec.getSize(widthMeasureSpec)
        val suggestHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec,heightMeasureSpec)

        var childWidth: Int//子控件的宽
        var childHeight: Int//子控件的高
        var lineWidth: Int = paddingLeft + paddingRight
        var lineMaxWidth: Int = lineWidth
        var lineHeight: Int = paddingTop + paddingBottom
        var singleLineHeight: Int = 0
        var childParams: MarginLayoutParams
        var resultWidth: Int = suggestWidth
        var resultHeight: Int = suggestHeight

        for (index in 0 until childCount){//含头不含尾
            val view = getChildAt(index)
            childParams = view.layoutParams as MarginLayoutParams
            childWidth = view.measuredWidth + childParams.leftMargin + childParams.rightMargin
            childHeight = view.measuredHeight + childParams.topMargin + childParams.bottomMargin

            if(widthMode == MeasureSpec.AT_MOST){

                if(lineWidth + childWidth > suggestWidth){
                    //重新计算下一行
                    lineWidth = childWidth + paddingLeft + paddingRight//新一行的初始宽度
                    lineHeight += singleLineHeight//换行之后，需要增加高度

                    singleLineHeight = childHeight//新的一行的初始高度
                }else{
                    lineWidth += childWidth
                    if(lineWidth > lineMaxWidth){
                        lineMaxWidth = lineWidth
                    }
                }

                if(singleLineHeight < childHeight){
                    singleLineHeight = childHeight
                }

                if(index == childCount - 1){
                    lineHeight += singleLineHeight
                }

            }
        }

        if(widthMode == MeasureSpec.AT_MOST){
            resultWidth = lineMaxWidth
        }

        if(heightMode == MeasureSpec.AT_MOST){
            resultHeight = lineHeight
            if(resultHeight > suggestHeight){
                resultHeight = suggestHeight
            }
        }

        setMeasuredDimension(resultWidth,resultHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left: Int = paddingLeft
        var right: Int = width - paddingRight
        var top: Int = paddingTop
        var bottom: Int = height - paddingBottom

        var singleLineHeight = 0
        var lp: MarginLayoutParams
        var childWidth: Int
        var childHeight: Int

        for (index in 0 until  childCount ){
            var view = getChildAt(index)
            lp = view.layoutParams as MarginLayoutParams

            childWidth = view.measuredWidth + lp.leftMargin + lp.rightMargin
            childHeight = view.measuredHeight + lp.topMargin + lp.bottomMargin

            if(left + childWidth > right){//换行
                left = paddingLeft
                top += singleLineHeight
                singleLineHeight = childHeight
            }else{
                if(singleLineHeight < childHeight){
                    singleLineHeight = childHeight
                }
            }

            if(top >= bottom) break

            view.layout(left + lp.leftMargin,top + lp.topMargin,left + childWidth,top + childHeight)

            left += childWidth
        }

    }
}