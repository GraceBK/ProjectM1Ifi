package boukou.grace.projectm1ifi.adapter_files;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.R;
import boukou.grace.projectm1ifi.java_files.MySms;

/**
 * boukou.grace.projectm1ifi.adapter_files
 * Created by grace on 14/04/2018.
 */
public class MySmsAdapter extends RecyclerView.Adapter {

    private static final int VIEW_MESSAGE_SENT = 1;
    private static final int VIEW_MESSAGE_RECEIVED = 2;

    private List<MySms> mySmsList;

    public MySmsAdapter(List<MySms> mySmsList) {
        this.mySmsList = mySmsList;
    }

    @Override
    public int getItemViewType(int position) {
        MySms sms = mySmsList.get(position);
        //if (Objects.equals(sms.getSender().getPhone_number(), "0650231529")) {
        //if (Objects.equals(sms.get_address(), "0650231529")) {
        if (Objects.equals(sms.get_sender(), "sender")) {
            return VIEW_MESSAGE_SENT;
        } else {
            return VIEW_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_item, parent, false);
            return new MySentSmsViewHolder(view);
        } else if (viewType == VIEW_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_item, parent, false);
            return new MyReceivedSmsViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MySms sms = mySmsList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_MESSAGE_SENT:
                ((MySentSmsViewHolder) holder).bind(sms);
                break;
            case VIEW_MESSAGE_RECEIVED:
                ((MyReceivedSmsViewHolder) holder).bind(sms);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mySmsList.size();
    }

    class MySentSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;
        TextView time;

        MySentSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
            time = itemView.findViewById(R.id.edit_txt_time_send);
        }

        void bind(MySms sms2) {
            this.sms.setText(sms2.get_msg());
        }
    }

    class MyReceivedSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;
        TextView time;

        MyReceivedSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
            time = itemView.findViewById(R.id.edit_txt_time_send);
        }

        void bind(MySms sms2) {
            this.sms.setText(sms2.get_msg());
        }
    }
}
