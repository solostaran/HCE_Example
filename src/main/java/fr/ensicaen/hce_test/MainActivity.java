package fr.ensicaen.hce_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import static fr.ensicaen.hce_test.HostApduServiceTest.AMOUNT;

public class MainActivity extends AppCompatActivity {

//    private static final String AMOUNT = "amount";
    private TextView lblAmountDesc;
    private EditText editAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lblAmountDesc = (TextView)findViewById(R.id.lblAmountDesc);
        editAmount = (EditText)findViewById(R.id.editAmount);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle b = getIntent().getExtras();
        if (b == null) return;
        int amount = b.getInt(AMOUNT, -1);
        if (amount >= 0) {
            lblAmountDesc.setText(getString(R.string.lbl_amountdesc));
            editAmount.setText(amount);
        } else {
            lblAmountDesc.setText(getString(R.string.no_amount));
            editAmount.setText("");
        }
    }
}
