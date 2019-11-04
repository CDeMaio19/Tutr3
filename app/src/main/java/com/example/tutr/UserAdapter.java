package com.example.tutr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> usersList;

    public UserAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public CircleImageView profileImage;

        public ViewHolder(View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_list_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = usersList.get(position);
        String profileString = user.getProfilePhoto();
        if (!profileString.equals("default")) {
            byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            holder.profileImage.setImageBitmap(bitmap);
        } else {
            holder.profileImage.setImageResource(R.drawable.graduation_2841875_640);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
