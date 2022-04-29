package com.example.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * viewIdName不是viewId
 */
class AutoCancelFollowService : AccessibilityService() {

    var gestureComplete = true

    val TAG = "AutoCancelFollowService"

    val targetPackage = "com.smile.gifmaker"
    val moreBtn = "$targetPackage:id/more_btn"
    val cancelBtn = "$targetPackage:id/qlist_alert_dialog_item_text"

    /**
     * 当系统成功连接到无障碍服务时，会调用此方法。使用此方法可为服务执行任何一次性设置步骤，包括连接到用户反馈系统服务，
     * 如音频管理器或设备振动器。如果您要在运行时设置服务的配置或做出一次性调整，从此处调用 setServiceInfo() 非常方便。
     */
    override fun onServiceConnected() {
        serviceInfo
        Log.d(TAG, "onServiceConnected ----------------------------------------------------------")
    }

    private fun recursionNode(node: AccessibilityNodeInfo) {
        node.apply {
            Log.d(TAG, "----------------node start------------------------------------")
            Log.d(TAG, "text : " + this.text.toString())
            Log.d(TAG, "viewIdResourceName : " + this.viewIdResourceName)
            Log.d(TAG, "childCount : " + this.childCount)
            Log.d(TAG, "childCount : " + this.childCount)
            Log.d(TAG, "----------------node end----------------------------------------")
            Log.d(TAG, "                                             ")
            Log.d(TAG, "                                             ")
            Log.d(TAG, "                                             ")
            val moreBtn = findAccessibilityNodeInfosByViewId(moreBtn)
            if (moreBtn.isNotEmpty()) {
                moreBtn[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                SystemClock.sleep(500)
            }
            val cancelBtn = findAccessibilityNodeInfosByViewId(cancelBtn)
            if (cancelBtn.isNotEmpty()) {
                cancelBtn[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                SystemClock.sleep(500)
            }
        }
        if (node.childCount > 0) {
            for (i in 0 until node.childCount) {
                Log.d(TAG, "child index：$i")
                val child = node.getChild(i)
                if (child != null) {
                    recursionNode(child)
                }
            }
        }
    }

    /**
     * 当系统检测到与无障碍服务指定的事件过滤参数匹配的 AccessibilityEvent 时，会回调此方法。例如，
     * 当用户点击某个按钮或将焦点置于应用中的某个界面控件上，而无障碍服务正在为其提供反馈时。出现这种情况时，
     * 系统会调用此方法，并传递关联的 AccessibilityEvent，服务随后可以对其进行解读并用其向用户提供反馈。
     * 此方法可能会在服务的整个生命周期内被调用多次
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "event type:" + event.eventType)
//        - 此方法会返回一个 AccessibilityNodeInfo 对象。此对象可让您请求生成了无障碍事件的组件的视图布局层次结构,
//        父级和子级）。此功能允许无障碍服务调查事件的完整上下文，包括任何内含视图或子视图的内容和状态。
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (event.source != null) {
                event.source.apply {
//                    recursionNode(this)
                val moreBtn = findAccessibilityNodeInfosByViewId(moreBtn)
                if(moreBtn.isNotEmpty()){
                    SystemClock.sleep(200)
                    moreBtn[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    SystemClock.sleep(500)
                    recycle()
                    return
                }
                val cancelBtn = findAccessibilityNodeInfosByText("取消关注")
                if(cancelBtn.isNotEmpty()){
                    SystemClock.sleep(200)
                    cancelBtn[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    SystemClock.sleep(500)
                    recycle()
                    return
                }
                    val path = Path()
                    path.reset()
                    path.moveTo(540f, 800f)
                    path.lineTo(540f, 1800f)
                    val builder = GestureDescription.Builder()
                    val description =
                        builder.addStroke(GestureDescription.StrokeDescription(path, 100L, 800L))
                            .build()
                    gestureComplete = false
                    dispatchGesture(description, object : GestureResultCallback() {
                        override fun onCompleted(gestureDescription: GestureDescription?) {
                            gestureComplete = true
                        }
                    }, null)
                    SystemClock.sleep(2000)


                    recycle()
                }
            }

        }
    }

    /**
     * 当系统要中断服务正在提供的反馈（通常是为了响应将焦点移到其他控件等用户操作）时，会调用此方法。
     * 此方法可能会在服务的整个生命周期内被调用多次。
     */
    override fun onInterrupt() {
    }

    /**
     * 当系统将要关闭无障碍服务时，会调用此方法。使用此方法可执行任何一次性关闭流程，包括取消分配用户反馈系统服务，
     * 如音频管理器或设备振动器
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind ----------------------------------------------------------")
        return super.onUnbind(intent)
    }
}