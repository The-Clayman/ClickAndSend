package com.r_and_e.click_and_send;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Fragment7 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState ) {

		// TODO Auto-generated method stub
		if (container == null) {
			return null;
		}

		return (LinearLayout) inflater.inflate(R.layout.fragment7_layout,
				container, false);
	}

}
