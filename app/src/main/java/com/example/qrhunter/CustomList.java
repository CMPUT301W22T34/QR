package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
/**
 * @description to show all player
 *
 *
 */

public class CustomList extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    public CustomList(Context context, ArrayList<User> users){
        super(context,0,users);
        this.users = users;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }
        User user =users.get(position);



        TextView userName = view.findViewById(R.id.city_text);
        TextView userScore = view.findViewById(R.id.province_text);

        userName.setText(user.getUserName());
        userScore.setText(String.valueOf(user.getTotal()));
        //userScore.setText(user.getUserID());
        return view;

    }
}
