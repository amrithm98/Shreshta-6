package shreshta.com.air_help.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import shreshta.com.air_help.Models.ContactModel;
import shreshta.com.air_help.R;

/**
 * Created by amrith on 4/1/17.
 */

public class ContactListAdapter extends BaseAdapter {
    List<ContactModel> contactList;
    Context context;
    public ContactListAdapter(List<ContactModel> list,Context c)
    {
        context=c;
        contactList=list;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
            tag = new Tag(convertView);
        } else {
            tag = (Tag) convertView.getTag();
        }
        final ContactModel contactModel=(ContactModel)getItem(position);
        tag.tvName.setText(contactModel.name);
        tag.tvNumber.setText(contactModel.phone);
        convertView.setTag(tag);
        return null;
    }
    static class Tag {
        TextView tvName, tvNumber;
        Button remove;
        Tag(View v) {
            tvName = (TextView) v.findViewById(R.id.et_name);
            tvNumber = (TextView) v.findViewById(R.id.et_phone);
            remove = (Button) v.findViewById(R.id.remove);
        }
    }
}

