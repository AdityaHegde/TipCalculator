package adityash.tipcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class CreateUpdateTip extends ActionBarActivity {
	private EditText etTipPercentage;
	private float percent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.create_update_tip);
	    setListeners();
	}

	private void setListeners() {
		percent = getIntent().getFloatExtra("percent", (float) 0.0);
		etTipPercentage = (EditText) findViewById(R.id.etTipPercentage);
		etTipPercentage.setText(percent+"");
		etTipPercentage.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					percent = Float.parseFloat(s.toString());
				} catch(Exception e) {}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
			
		});	
	}
	
	public void onSave(View v) {
		Intent data = new Intent();
		data.putExtra("percent", percent);
		data.putExtra("cance", false);
		setResult(RESULT_OK, data);
		this.finish();
	}
	
	public void onCancel(View v) {
		Intent data = new Intent();
		data.putExtra("cance", true);
		setResult(RESULT_OK, data);
		this.finish();
	}

}
