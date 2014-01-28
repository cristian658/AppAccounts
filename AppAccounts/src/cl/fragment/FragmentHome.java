package cl.fragment; 

import cl.ciisa.appaccounts.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentHome  extends Fragment{	
	protected View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	 v = inflater.inflate(R.layout.frgmnt_home, container, false);
    	return v;	
    }
}
