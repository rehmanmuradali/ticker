package ticker.views.com.timer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private CircularView circularViewWithoutText;
    private CircularView circularViewWithTimer;
    private CircularView circularViewWithCustomText;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


    }

    private void init() {

        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        Button pause = findViewById(R.id.pause);
        Button resume = findViewById(R.id.resume);

        initCircularViewWithoutText();
        initCircularViewWithTimer();
        initCircularViewWithCustomText();

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        pause.setOnClickListener(this);
        resume.setOnClickListener(this);


    }


    private void initCircularViewWithoutText() {
        circularViewWithoutText = findViewById(R.id.circular_view);
        CircularView.OptionsBuilder builderWithoutText = new
                CircularView.OptionsBuilder()
                .shouldDisplayText(false)
                .setCounterInSeconds(CircularView.OptionsBuilder.INFINITE);
        circularViewWithoutText.setOptions(builderWithoutText);
    }

    private void initCircularViewWithTimer() {
        circularViewWithTimer = findViewById(R.id.circular_view_with_timer);
        CircularView.OptionsBuilder builderWithTimer = new
                CircularView.OptionsBuilder()
                .shouldDisplayText(true)
                .setCounterInSeconds(100)
                .setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {
                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Finished ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTimerCancelled() {
                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Cancelled ", Toast.LENGTH_SHORT).show();
                    }
                });
        circularViewWithTimer.setOptions(builderWithTimer);
    }


    private void initCircularViewWithCustomText() {
        circularViewWithCustomText = findViewById(R.id.circular_view_with_custom_text);
        CircularView.OptionsBuilder builderWithoutText = new
                CircularView.OptionsBuilder()
                .setCounterInSeconds(CircularView.OptionsBuilder.INFINITE)
                .setCustomText("Waiting for Customer");
        circularViewWithCustomText.setOptions(builderWithoutText);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.start:
                initCircularViewWithoutText();
                initCircularViewWithTimer();
                initCircularViewWithCustomText();
                circularViewWithoutText.startTimer();
                circularViewWithTimer.startTimer();
                circularViewWithCustomText.startTimer();
                break;

            case R.id.stop:
                circularViewWithoutText.stopTimer();
                circularViewWithTimer.stopTimer();
                circularViewWithCustomText.stopTimer();
                break;

            case R.id.pause:

                if (circularViewWithCustomText.pauseTimer()) {

                    isPause = true;

                } else {

                    Toast.makeText(this, "Timer finished before pausing", Toast.LENGTH_SHORT).show();
                }





                break;

            case R.id.resume:

                if (isPause) {

                    circularViewWithCustomText.resumeTimer();
                    isPause = false;

                } else {
                    Toast.makeText(this, "Timer Not Paused before", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }
}
