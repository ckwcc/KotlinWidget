package com.ckw.zfsoft.customwidget.viewgroupwidget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.ckw.zfsoft.customwidget.R

/**
 * Created by ckw
 * on 2017/11/22.
 * 简单的流式布局
 */
class TagView (context: Context): ViewGroup(context) {
    val TAG : String = "TagView"

    constructor(context: Context,attrs: AttributeSet): this(context){
        val array: TypedArray = context!!.obtainStyledAttributes(attrs, R.styleable.TagView)
        array.recycle()

    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context,attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //建议的长宽，但还不是最终的，由模式决定
        val suggestWidth = MeasureSpec.getSize(widthMeasureSpec)
        val suggestHeight = MeasureSpec.getSize(heightMeasureSpec)

        //测量子view的尺寸信息
        measureChildren(widthMeasureSpec,heightMeasureSpec)

        /**
         *  主要处理 width 和 height AT_MOST 测量模式下的情况
         *  在 width 方面，TagView 中的子元素要求出所有行中的宽度最大的一行，并且这个数值
         *  不能大于 parent 给出的建议宽度
         * */

        var cWidth: Int//子控件的宽
        var cHeight: Int//子控件的高
        var lineWidth: Int = paddingLeft + paddingRight
        var lineMaxWidth: Int = lineWidth
        var lineHeight: Int = paddingTop + paddingBottom
        var singleLineHeight: Int = 0;
        var childParams: MarginLayoutParams
        var resultWidth: Int = suggestWidth//最后的测量宽度
        var resultHeight: Int = suggestHeight//最后的测量高度

        for (index in 0 until childCount){
            val view = getChildAt(index)
            childParams = view.layoutParams as MarginLayoutParams//强转
            cWidth = view.measuredWidth + childParams.leftMargin + childParams.rightMargin
            cHeight = view.measuredHeight + childParams.topMargin + childParams.bottomMargin
            /**如果后者不判断的话，当出现widthMode为MeasureSpec.EXACTLY,
             * 而heightMode == MeasureSpec.AT_MOST时
             * 会出现最后设置的高度为零的情况,导致界面不显示
             * */
            if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST){

                if(lineWidth + cWidth > suggestWidth){//需要进行换行
                    lineWidth = cWidth + paddingLeft + paddingRight//新的一行的初始宽度
                    lineHeight += singleLineHeight//增加了一行的高度（只记录了满行的高度）

                    //换行后重置新单行高度
                    singleLineHeight = cHeight
                }else{
                    lineWidth += cWidth
                    if(lineWidth > lineMaxWidth){
                        lineMaxWidth = lineWidth
                    }
                }

                if(singleLineHeight < cHeight){//目的是设置单行的高度为这一行中最高的childView
                    singleLineHeight = cHeight
                }

                if(index == childCount - 1){//当来到最后一个childview的时候，不管这行有没有满，都要加上这一行的高度
                    lineHeight += singleLineHeight
                }

            }

        }

        if(widthMode == MeasureSpec.AT_MOST){
            Log.d("----","lineMaxWidth:"+lineMaxWidth)
            resultWidth = lineMaxWidth
        }

        if(heightMode == MeasureSpec.AT_MOST){
            Log.d("----","lineheight:"+lineHeight)
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

        var singleLineHeight: Int = 0
        var lp: MarginLayoutParams
        var childWidth: Int
        var childHeight: Int

        for (index in 0 until childCount){
            var view = getChildAt(index)
            lp = view.layoutParams as MarginLayoutParams
            childWidth = view.measuredWidth + lp.leftMargin + lp.rightMargin
            childHeight = view.measuredHeight + lp.topMargin + lp.bottomMargin

            if(left + childWidth > right){//该换行了
                left = paddingLeft//新起点都是这个
                top += singleLineHeight
                singleLineHeight = childHeight
            }else{
                if(singleLineHeight < childHeight){
                    singleLineHeight = childHeight
                }
            }

            if(top >= bottom){
                break
            }

            //绘制子view的位置
            view.layout(left + lp.leftMargin,top + lp.topMargin,left + childWidth,top + childHeight)

            left += childWidth//绘制完一个view后，left的位置显然要增加上刚刚绘制的view的宽度
        }
    }

}
