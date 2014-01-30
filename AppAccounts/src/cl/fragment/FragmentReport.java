package cl.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.ciisa.appaccounts.R;

public class FragmentReport extends Fragment{
	protected View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	 v = inflater.inflate(R.layout.frgmnt_report, container, false);
    	return v;	
    }
}
