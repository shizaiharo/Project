package tw.cgu.b0929056.testapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class TitleBar extends ConstraintLayout {
    ConstraintLayout Title_Bar;
    ImageButton BackBtn, HelpBtn;
    TextView TitleTxv;
    public TitleBar(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_bar, this, true);
        Title_Bar = (ConstraintLayout) findViewById(R.id.title_bar);
        BackBtn = (ImageButton) findViewById(R.id.Bar_BackButton);
        HelpBtn = (ImageButton) findViewById(R.id.Bar_HelpButton);
        TitleTxv = (TextView) findViewById(R.id.Bar_TitleTextView);
    }

    public void setTitle(String Title) {
        TitleTxv.setText(Title);
    }
}
