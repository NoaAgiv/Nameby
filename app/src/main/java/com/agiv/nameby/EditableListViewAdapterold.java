package com.agiv.nameby;

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

import com.agiv.nameby.entities.Name;

import java.util.ArrayList;

public class EditableListViewAdapterold extends BaseAdapter implements ListAdapter {
    private ArrayList<Name> list = new ArrayList<Name>();
    private Context context;
    String dialogBody = "";
    String dialogTitle = "";
    int buttonImage = 0;
    NameTagger.SwitchListsCallBack switchLists;



    public EditableListViewAdapterold(NameTagger.SwitchListsCallBack switchLists, ArrayList<Name> list,
                                      Context context, String dialogTitle, String dialogBody, int buttonImage) {
        this.list = list;
        this.switchLists = switchLists;
        this.context = context;
        this.dialogBody = dialogBody;
        this.dialogTitle = dialogTitle;
        this.buttonImage = buttonImage;
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
//        return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.editable_row, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).name);

        //Handle buttons and add onClickListeners
        ImageButton changeButton = (ImageButton)view.findViewById(R.id.changeButton);
        changeButton.setImageResource(buttonImage);

        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Name name = list.get(position);
                String specificDialogBody = String.format("%s\n%s", dialogBody, name.name);
                new AlertDialog.Builder(context)
                        .setTitle(dialogTitle)
                        .setMessage(specificDialogBody)
                        .setCancelable(false)
                        .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switchLists.switchLists(name);
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