package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import boukou.grace.projectm1ifi.db.room_db.Msg;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/05/2018.
 */
public class ReceiveAdapter extends RecyclerView.Adapter {

    private List<Msg> msgList;

    ReceiveAdapter(List<Msg> msgList) {
        this.msgList = msgList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_item, parent, false);
        return new ReceiveAdapter.MySentSmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        ((ReceiveAdapter.MySentSmsViewHolder) holder).bind(msg);
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
        TextView sms_decrypt;
        TextView num_from;
        Button lire_sms_ok;

        MySentSmsViewHolder(View itemView) {
            super(itemView);
            sms = itemView.findViewById(R.id.tv_sms_received);
            sms_decrypt = itemView.findViewById(R.id.tv_sms_received_decrypt);
            num_from = itemView.findViewById(R.id.tv_num_from);
            lire_sms_ok = itemView.findViewById(R.id.btn_see_receid);
        }

        @SuppressLint("SetTextI18n")
        void bind(Msg msg2) {
            this.sms.setText(msg2.sms1);
            this.sms_decrypt.setText(msg2.sms2);
            this.num_from.setText(msg2.phoneReceiver);

            lire_sms_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sms.getVisibility() == View.VISIBLE) {
                        sms.setVisibility(View.GONE);
                        sms_decrypt.setVisibility(View.VISIBLE);
                        lire_sms_ok.setText("Chiffre");
                    } else {
                        sms.setVisibility(View.VISIBLE);
                        sms_decrypt.setVisibility(View.GONE);
                        lire_sms_ok.setText("Dechiffre");
                    }
                }
            });
        }
    }

}
