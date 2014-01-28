package cl.ciisa.tableModel;

import java.util.ArrayList;

import cl.ciisa.appaccounts.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAccountAdapter extends BaseAdapter{
    protected Activity activity; 
    protected ArrayList<ListAccount> items;
    
    public ListAccountAdapter(Context cnt, ArrayList<ListAccount> items){
    	this.activity=(Activity) cnt;
    	this.items=items;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0).getId();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
        View vi=arg1;		
        if(arg1 == null) {
        	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	vi = inflater.inflate(R.layout.view_list_account, null);
        }
            
        ListAccount item = items.get(arg0);
        
        //ImageView image = (ImageView) vi.findViewById(R.id.imagen);
        //int imageResource = activity.getResources().getIdentifier(item.getRutaImagen(), null, activity.getPackageName());
        //image.setImageDrawable(activity.getResources().getDrawable(imageResource));
        
        TextView nombre = (TextView) vi.findViewById(R.id.textName);
        nombre.setText(item.getName());
        
        TextView tipo = (TextView) vi.findViewById(R.id.textType);
        tipo.setText(item.getType());
        
        TextView price = (TextView) vi.findViewById(R.id.textPrice);
        price.setText("$"+String.valueOf(item.getPrice()));

        return vi;
	}
    
    
}
