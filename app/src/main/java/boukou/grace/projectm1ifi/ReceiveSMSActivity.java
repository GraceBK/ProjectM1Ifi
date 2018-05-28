package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;

public class ReceiveSMSActivity extends AppCompatActivity {

    RecyclerView mySmsRecycler;
    ReceiveAdapter adapter;
    MsgViewModel viewModel;

    List<Msg> msgList;

    private AppDatabase db;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_sms);
        Toolbar toolbar = findViewById(R.id.toolbar_receive);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        db = AppDatabase.getDatabase(getApplicationContext());

        getSupportActionBar().setTitle("Message recu");
        msgList = db.msgDao().getAllMsgReceive();

        Msg msg = new Msg();

        mySmsRecycler = findViewById(R.id.rc_receive);
        mySmsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReceiveAdapter(msgList);

        mySmsRecycler.smoothScrollToPosition(msgList.size());

        mySmsRecycler.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MsgViewModel.class);
        viewModel.getMsgList().observe(this, msgs -> {
            new AsyncTask<Msg, Void, Void>() {

                @Override
                protected Void doInBackground(Msg... msgs) {
                    final List<Msg> msgs1 = db.msgDao().getAllMsgReceive();
                    runOnUiThread(() -> adapter.update(msgs1));
                    return null;
                }
            }.execute(msg);
        });
    }
}
