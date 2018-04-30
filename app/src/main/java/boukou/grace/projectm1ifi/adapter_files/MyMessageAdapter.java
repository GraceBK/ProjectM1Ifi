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
import boukou.grace.projectm1ifi.db.room_db.Msg;

/**
 * boukou.grace.projectm1ifi.adapter_files
 * Created by grace on 25/04/2018.
 */
public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyViewHolder> {

    private List<Msg> msgList;

    public MyMessageAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public MyMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item, parent, false);
        return new MyMessageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        holder.username.setText(msg.nameReceiver);
        holder.status_sms.setText(msg.sms1);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void update(List<Msg> msgList) {
        this.msgList = msgList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView status_sms;

        MyViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_or_phone_text_view);
            status_sms = itemView.findViewById(R.id.status_sms_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("USERNAME", msgList.get(getLayoutPosition()).nameReceiver);
                    intent.putExtra("PHONE", msgList.get(getLayoutPosition()).phoneReceiver);
                    intent.putExtra("DESCRIPTION", msgList.get(getLayoutPosition()).sms1);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

}
