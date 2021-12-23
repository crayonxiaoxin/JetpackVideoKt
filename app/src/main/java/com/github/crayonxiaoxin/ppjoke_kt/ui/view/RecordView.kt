package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R

class RecordView : View, View.OnClickListener, View.OnLongClickListener {
    private val PROGRESS_INTERVAL = 100 // 更新间隔 毫秒

    private var progressMaxValue: Int = 0
    private var progressValue: Int = 0
    private var maxDuration: Int
    private var radius: Int
    private var progressWidth: Int
    private var fillColor: Int
    private var progressColor: Int

    private var fillPaint: Paint
    private var progressPaint: Paint

    private var isRecording = false
    private var startRecordTime = 0L

    private var mListener: RecordListener? = null

    // 更新进度
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            progressValue += 1
            postInvalidate()
            if (progressValue <= progressMaxValue) {
                sendEmptyMessageDelayed(0, PROGRESS_INTERVAL.toLong())
            } else {
                finishRecord()
            }
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        context.obtainStyledAttributes(attrs, R.styleable.RecordView).apply {
            progressColor = getColor(R.styleable.RecordView_progress_color, Color.RED)
            fillColor = getColor(R.styleable.RecordView_fill_color, Color.WHITE)
            progressWidth = getDimensionPixelOffset(R.styleable.RecordView_progress_width, 0)
            radius = getDimensionPixelOffset(R.styleable.RecordView_radius, 3.dp)
            maxDuration = getInteger(R.styleable.RecordView_duration, 10)
            recycle()
        }

        setMaxDuration(maxDuration)

        fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fillColor
            style = Paint.Style.FILL
        }
        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = progressColor
            style = Paint.Style.STROKE
            strokeWidth = progressWidth.toFloat()
        }

        setOnTouchListener { _, event ->
//            performClick()
            if (event.action == MotionEvent.ACTION_DOWN) {
                isRecording = true
                startRecordTime = System.currentTimeMillis()
                handler.sendEmptyMessage(0)
            } else if (event.action == MotionEvent.ACTION_UP) {
                val now = System.currentTimeMillis()
                if (now - startRecordTime >= ViewConfiguration.getLongPressTimeout()) {
                    finishRecord()
                }
                handler.removeCallbacksAndMessages(null)
                isRecording = false
                startRecordTime = 0L
                progressValue = 0
                postInvalidate()
            }
            false
        }

        setOnClickListener(this)
        setOnLongClickListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val cx = width / 2f
        val cy = height / 2f
        canvas?.let {
            if (isRecording) {
                it.drawCircle(cx, cy, width / 2f, fillPaint)
                val sweepAngle = (progressValue * 1f / progressMaxValue) * 360f
                val a = progressWidth / 2f
                it.drawArc(a, a, width - a, height - a, -90f, sweepAngle, false, progressPaint)
            } else {
                it.drawCircle(cx, cy, radius.toFloat(), fillPaint)
            }
        }
    }

    private fun setMaxDuration(maxDuration: Int) {
        this.progressMaxValue = maxDuration * 1000 / PROGRESS_INTERVAL
    }

    private fun finishRecord() {
        isRecording = false
        mListener?.onFinished()
    }

    fun setOnRecordListener(listener: RecordListener) {
        this.mListener = listener
    }

    interface RecordListener {
        fun onClick()
        fun onLongClick()
        fun onFinished()
    }

    override fun onClick(v: View?) {
        mListener?.onClick()
    }

    override fun onLongClick(v: View?): Boolean {
        mListener?.onLongClick()
        return true
    }

}