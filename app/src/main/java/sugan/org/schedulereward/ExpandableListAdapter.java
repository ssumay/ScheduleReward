package sugan.org.schedulereward;

import android.widget.BaseExpandableListAdapter;

/**
 * Created by eunsoo on 2017-12-01.
 * updated by eunsoo on summer 2021
 */

public abstract  class ExpandableListAdapter extends BaseExpandableListAdapter {


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }



    @Override
    public long getChildId(int groupPosition, int childPosition)  {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition)

    {

        return groupPosition;

    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1)
    {
        return true;
    }
}
