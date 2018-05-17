package boukou.grace.projectm1ifi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.Msg;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 30/04/2018.
 */
public class MsgAdapter extends RecyclerView.Adapter {
    private static final int VIEW_MESSAGE_SENT = 1;
    private static final int VIEW_MESSAGE_RECEIVED = 2;

    private List<Msg> msgList;

    public MsgAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @Override
    public int getItemViewType(int position) {
        Msg msg = msgList.get(position);
        //if (Objects.equals(sms.getSender().getPhone_number(), "0650231529")) {
        //if (Objects.equals(sms.get_address(), "0650231529")) {
        if (Objects.equals(msg.phoneSender, "sender")) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);
            return new MsgAdapter.MySentSmsViewHolder(view);
        } else if (viewType == VIEW_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);
            return new MsgAdapter.MyReceivedSmsViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_MESSAGE_SENT:
                ((MsgAdapter.MySentSmsViewHolder) holder).bind(msg);
                break;
            case VIEW_MESSAGE_RECEIVED:
                ((MsgAdapter.MyReceivedSmsViewHolder) holder).bind(msg);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public void update(List<Msg> msgs) {
        this.msgList = msgs;
        notifyDataSetChanged();
    }

    class MySentSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;
        TextView time;

        MySentSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
//            time = itemView.findViewById(R.id.edit_txt_time_send);
        }

        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
        }
    }

    class MyReceivedSmsViewHolder extends RecyclerView.ViewHolder {

        TextView sms;
        TextView time;

        MyReceivedSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
//            time = itemView.findViewById(R.id.edit_txt_time_send);
        }

        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
        }
    }
}
