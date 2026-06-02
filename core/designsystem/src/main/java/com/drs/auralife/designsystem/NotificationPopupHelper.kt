package com.drs.auralife.designsystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.core.common.util.AppNotification
import com.drs.auralife.core.designsystem.R
import com.drs.auralife.core.navigation.AppNavigator

@SuppressLint("InflateParams")
class NotificationPopupHelper(
    private val appNavigator: AppNavigator,
) {
    fun show(anchor: View) {
        val context = anchor.context
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_notification_list, null)
        val rvNotifications = popupView.findViewById<RecyclerView>(R.id.rvNotifications)
        val text = popupView.findViewById<TextView>(R.id.text)

        val notifications = AppNotification.getNotifications(context)

        text.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE

        rvNotifications.layoutManager = LinearLayoutManager(context)

        val adapter = NotificationAdapter(
            notifications,
            { appNavigator.navigateToFilmDetails(it.first) },
            { AppNotification.removeNotification(context, it) },
        )

        rvNotifications.adapter = adapter

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true,
        )
        popupWindow.showAsDropDown(anchor)
    }
}
