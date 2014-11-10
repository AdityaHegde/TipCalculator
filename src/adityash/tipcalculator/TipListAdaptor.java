package adityash.tipcalculator;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TipListAdaptor extends CursorAdapter {

	public TipListAdaptor(Context context, Cursor c) {
		super(context, c, 0);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    return LayoutInflater.from(context).inflate(R.layout.tip, parent, false);
    }
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Button btn = (Button) view.findViewById(R.id.btnTip);
	    // Extract properties from cursor
	    float percentage = cursor.getFloat(cursor.getColumnIndexOrThrow("percentage"));
	    short selected = cursor.getShort(cursor.getColumnIndexOrThrow("selected"));
	    btn.setText((selected == 1 ? "*" : "") + percentage+"%");
	}
	
	public void reloadCursor() {
		this.swapCursor(Tip.fetchResultCursor()).close();
	}

}
