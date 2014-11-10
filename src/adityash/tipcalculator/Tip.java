package adityash.tipcalculator;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "tip")
public class Tip extends Model {
	@Column(name = "percentage")
	public float percentage;
	@Column(name = "selected")
	public short selected;
	
	public Tip() {
		super();
	}
	
	public Tip(float percentage) {
		super();
		this.percentage = percentage;
		this.selected = 0;
	}
	
	public Tip(float percentage, short selected) {
		super();
		this.percentage = percentage;
		this.selected = selected;
	}
	
	public static Cursor fetchResultCursor() {
		String tableName = Cache.getTableInfo(Tip.class).getTableName();
		String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").from(Tip.class).toSql();
		Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
		return resultCursor;
	}
}
