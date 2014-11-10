package adityash.tipcalculator;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.Cache;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

public class TipCalculator extends ActionBarActivity {
	private EditText etTotalAmt;
	private EditText etSplit;
	private TextView tvTipAmt;
	private ListView lvTipPercents;
	private TipListAdaptor tipListAdaptor;
	
	private ActionMode currentActionMode;
	private Tip curTip;
	private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        mode.setTitle("Actions");
	        mode.getMenuInflater().inflate(R.menu.tip_operations_menu, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }
	    
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	      switch (item.getItemId()) {
	        case R.id.menu_edit:
	        	mode.finish();
	        	showCreateUpdateActivity(curTip);
	        	return true;
	        case R.id.menu_delete:
	        	String tableName = Cache.getTableInfo(Tip.class).getTableName();
				new Delete().from(Tip.class).where(tableName+".Id = ?", curTip.getId()).execute();
				tipListAdaptor.reloadCursor();
	        	mode.finish();
	        	return true;
	        default:
	        	return false;
	      }
	    }
	    
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        currentActionMode = null;
	    } 
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        setupAdapter();
    }

	private void setupAdapter() {
		initializeTips();
		etTotalAmt = (EditText) findViewById(R.id.etTotalAmt);
		etSplit = (EditText) findViewById(R.id.etSplit);
		tvTipAmt = (TextView) findViewById(R.id.tvTipAmt);
		tipListAdaptor = new TipListAdaptor(this, Tip.fetchResultCursor());
		lvTipPercents = (ListView) findViewById(R.id.lvTipPercents);
		lvTipPercents.setAdapter(tipListAdaptor);
		
		etTotalAmt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculateTip(s.toString(), etSplit.getText().toString());
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		etSplit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				calculateTip(etTotalAmt.getText().toString(), s.toString());
			}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		tipListAdaptor.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				try {
					calculateTip(etTotalAmt.getText().toString(), etSplit.getText().toString());
				} catch(Exception e) {}
			}
		});
		lvTipPercents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("Long Clicked!");
				if (currentActionMode != null) { return false; }
				long tipId = tipListAdaptor.getItemId(position);
				String tableName = Cache.getTableInfo(Tip.class).getTableName();
				curTip = (Tip) new Select().from(Tip.class).where(tableName+".Id = ?", tipId).executeSingle();
				startSupportActionMode(modeCallBack);
		        view.setSelected(true);
				return true;
			}
		});
		lvTipPercents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				long tipId = tipListAdaptor.getItemId(position);
				String tableName = Cache.getTableInfo(Tip.class).getTableName();
				new Update(Tip.class).set("selected = 0").execute();
				new Update(Tip.class).set("selected = 1").where(tableName+".Id = ?", tipId).execute();
				tipListAdaptor.reloadCursor();
			}
		});

	}

	private void initializeTips() {
		if(new Select().from(Tip.class).execute().isEmpty()) {
			new Tip(10).save();
			new Tip(15, (short) 1).save();
			new Tip(20).save();
		}
	}
	
	public void calculateTip(String amtStr, String splitStr) {
		float amt = 0.0F;
		short split = 1;
		try {
			amt = Float.parseFloat(amtStr);
		} catch(Exception e) {}
		try {
			split = Short.parseShort(splitStr);
		} catch(Exception e) {}
		Tip tip = new Select().from(Tip.class).where("selected = 1").executeSingle();
		
		if(tip != null) {
			float percentage = tip.percentage;
			tvTipAmt.setText("$"+(amt*percentage/(split*100))+(split > 1 ? " each":""));
		}
	}
	
	public void addTip(View v) {
		curTip = new Tip();
		showCreateUpdateActivity(curTip);
	}
	
	private void showCreateUpdateActivity(Tip tip) {
		Intent i = new Intent(TipCalculator.this, CreateUpdateTip.class);
		
		i.putExtra("percent", tip.percentage);
		startActivityForResult(i, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Boolean cancel = data.getBooleanExtra("cancel", false);
			if(!cancel) {
				curTip.percentage = data.getFloatExtra("percent", (float) 0.0);
				curTip.save();
				tipListAdaptor.reloadCursor();
			}
		}
	}
    
}
