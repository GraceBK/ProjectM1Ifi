package boukou.grace.projectm1ifi.adapter_files;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import boukou.grace.projectm1ifi.DetailActivity;
import boukou.grace.projectm1ifi.R;
import boukou.grace.projectm1ifi.java_files.MyContact;

/**
 * boukou.grace.projectm1ifi.adapter_files
 * Created by grace on 11/04/2018.
 */
public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.MyViewHolder> {

    private List<MyContact> myContactList;

    public MyContactAdapter(List<MyContact> myContactList) {
        this.myContactList = myContactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyContact myContact = myContactList.get(position);
        holder.username.setText(myContact.getUsername());
        holder.phone_number.setText(myContact.getPhone_number());
    }

    @Override
    public int getItemCount() {
        return myContactList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView phone_number;

        MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_text_view);
            phone_number = itemView.findViewById(R.id.phone_num_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("USERNAME", myContactList.get(getLayoutPosition()).getUsername());
                    intent.putExtra("PHONE", myContactList.get(getLayoutPosition()).getPhone_number());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
