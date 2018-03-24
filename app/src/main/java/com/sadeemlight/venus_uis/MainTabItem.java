package com.sadeemlight.venus_uis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sadeemlight.R;

/**
 * Created by VENUS on 5/8/2017.
 */

public class MainTabItem extends View
{
    String m_tabName;
    int    m_notifiCount;

    Context m_parent;
    View m_rootView;
    View     notfRootView;
    TextView txtNotification;
    TextView txtTabname;

    public MainTabItem(Context context, String name)
    {
        super(context);

        m_notifiCount = 0;
        m_parent = context;
        m_tabName = name;

        View m_rootView = LayoutInflater.from(context).inflate(R.layout.tab_mainitem, null);
        txtNotification = (TextView) m_rootView.findViewById(R.id.text_notification);
        txtTabname = (TextView) m_rootView.findViewById(R.id.text_tabname);
        notfRootView = m_rootView.findViewById(R.id.notf_rootview);
    }

    public void update()
    {
        if(m_notifiCount == 0)
        {
            notfRootView.setVisibility(View.GONE);
        }
        else
        {
            notfRootView.setVisibility(View.VISIBLE);
        }

        txtNotification.setText(String.valueOf(m_notifiCount));
        txtTabname.setText(m_tabName);
    }

    public void setNotfCount(int count)
    {
        m_notifiCount = count;
        update();
    }

}
