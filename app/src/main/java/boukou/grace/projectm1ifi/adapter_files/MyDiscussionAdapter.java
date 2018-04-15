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
import boukou.grace.projectm1ifi.java_files.MyDiscussion;

/**
 * boukou.grace.projectm1ifi.adapter_files
 * Created by grace on 14/04/2018.
 */
public class MyDiscussionAdapter extends RecyclerView.Adapter<MyDiscussionAdapter.MyViewHolder> {

    private List<MyDiscussion> myDiscussionList;

    public MyDiscussionAdapter(List<MyDiscussion> myDiscussionList) {
        this.myDiscussionList = myDiscussionList;
    }

    @NonNull
    @Override
    public MyDiscussionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.disc_item, parent, false);
        return new MyDiscussionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDiscussionAdapter.MyViewHolder holder, int position) {
        MyDiscussion myDiscussion = myDiscussionList.get(position);
        holder.username.setText(myDiscussion.getUsername());
        holder.description.setText(myDiscussion.getDescription());
    }

    @Override
    public int getItemCount() {
        return myDiscussionList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView description;

        MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_disc_text_view);
            description = itemView.findViewById(R.id.phone_num_disc_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("USERNAME", myDiscussionList.get(getLayoutPosition()).getUsername());
                    intent.putExtra("PHONE", myDiscussionList.get(getLayoutPosition()).getPhone_number());
                    intent.putExtra("DESCRIPTION", myDiscussionList.get(getLayoutPosition()).getDescription());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
