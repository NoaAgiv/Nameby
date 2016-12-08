package com.agiv.names2;

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

import java.util.ArrayList;

public class EditableListViewAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> complementaryList = new ArrayList<String>();
    private Context context;
    String dialogBody = "";
    String dialogTitle = "";
    int bottonImage = 0;



    public EditableListViewAdapter(ArrayList<String> list, ArrayList<String> complementaryList,
                                   Context context, String dialogTitle, String dialogBody, int bottonImage) {
        this.list = list;
        this.complementaryList = complementaryList;
        this.context = context;
        this.dialogBody = dialogBody;
        this.dialogTitle = dialogTitle;
        this.bottonImage = bottonImage;
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
            view = inflater.inflate(R.layout.simplerow, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        ImageButton changeButton = (ImageButton)view.findViewById(R.id.changeButton);
        changeButton.setImageResource(bottonImage);

        changeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = list.get(position);
                String specificDialogBody = String.format("%s\n%s", dialogBody, name);
                new AlertDialog.Builder(context)
                        .setTitle(dialogTitle)
                        .setMessage(specificDialogBody)
                        .setCancelable(false)
                        .setPositiveButton(R.string.mark_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = list.remove(position);
                                complementaryList.add(name);
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