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

    private List<Msg> msgList;

    MsgAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);
        return new MsgAdapter.MySentSmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MsgAdapter.MySentSmsViewHolder) holder).bind(msgList.get(position));
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
        TextView status_sms;
        TextView status_key;
        TextView time;

        MySentSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.edit_txt_sms_send);
            status_sms = itemView.findViewById(R.id.tv_sms_status);
            status_key = itemView.findViewById(R.id.tv_key_status);
        }

        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
            this.status_sms.setText(msg2.status_sms);
            //this.status_key.setText(msg2.status_key);
        }
    }
}
