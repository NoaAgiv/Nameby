package com.agiv.nameby.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.agiv.nameby.NameTagger;
import com.agiv.nameby.R;
import com.agiv.nameby.entities.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EditableListViewAdapter extends BaseAdapter implements ListAdapter {
    private List<?> list = new ArrayList<Object>();
    private Context context;
    String dialogBody = "";
    String dialogTitle = "";
    int buttonImage = 0;
    RemoveItemCallback removeItemCallback;


    public static abstract class RemoveItemCallback {
        public void remove(Object obj){}
    }


    public EditableListViewAdapter(RemoveItemCallback removeItemCallback, List<?> list,
                                   Context context) {
        this(removeItemCallback, list,
                context, context.getString(R.string.remove),
                context.getString(R.string.remove_confirm_body),
                R.drawable.delete);
    }

    public EditableListViewAdapter(RemoveItemCallback removeItem, List<?> list,
                                   Context context, String dialogTitle, String dialogBody, int removeButtonImage) {
        this.list = list;
        this.removeItemCallback = removeItem;
        this.context = context;
        this.dialogBody = dialogBody;
        this.dialogTitle = dialogTitle;
        this.buttonImage = removeButtonImage;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.editable_row, null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).toString());

        ImageButton changeButton = (ImageButton)view.findViewById(R.id.changeButton);
        changeButton.setImageResource(buttonImage);

        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Object item = list.get(position);
                String specificDialogBody = String.format("%s\n%s", dialogBody, item);
                new AlertDialog.Builder(context)
                        .setTitle(dialogTitle)
                        .setMessage(specificDialogBody)
                        .setCancelable(false)
                        .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItemCallback.remove(item);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        });

        return view;
    }
}