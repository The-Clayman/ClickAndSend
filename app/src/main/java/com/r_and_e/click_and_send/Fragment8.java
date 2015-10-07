package com.r_and_e.click_and_send;

import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Fragment8 extends Fragment implements View.OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment8_layout,
				container, false);

		Button button = (Button) view.findViewById(R.id.button);
		button.setOnClickListener(this);


		if (container == null) {
			return null;
		}

		return view;
	}


	@Override
	public void onClick(View v) {

		wizzard_main.sendTohandler(MainActivity.proceed_To_ProfilePage , -1,-1,null);
		getActivity().finish();
	}


}
