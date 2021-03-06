package com.comvee.tnb.ui.privatedoctor;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.comvee.BaseApplication;
import com.comvee.ComveeBaseAdapter;
import com.comvee.tnb.R;
import com.comvee.tnb.common.MyBaseAdapter;
import com.comvee.tnb.model.MemberDoctorInfo;
import com.comvee.tool.ImageLoaderUtil;
import com.comvee.tool.ViewHolder;

public class MemberDoctorAdapter extends ComveeBaseAdapter<MemberDoctorInfo> {

	public MemberDoctorAdapter() {
		super(BaseApplication.getInstance(),  R.layout.member_server_item_doc_detail);
	}

	@Override
	protected void getView(ViewHolder holder, int position) {
		final MemberDoctorInfo model = getItem(position);
		ImageView img_doc = holder.get(R.id.img_doc);
		TextView doc_name = holder.get(R.id.tv_doc_name);
		TextView doc_desc = holder.get(R.id.tv_doc_desc);
		TextView doc_label_1 = holder.get(R.id.tv_label_1);
		TextView doc_label_2 = holder.get(R.id.tv_label_2);
		TextView doc_label_3 = holder.get(R.id.tv_label_3);
		TextView doc_address = holder.get(R.id.tv_doc_address);
		doc_label_1.setVisibility(View.INVISIBLE);
		doc_label_2.setVisibility(View.INVISIBLE);
		doc_label_3.setVisibility(View.INVISIBLE);
		ImageLoaderUtil.getInstance(context).displayImage(model.PER_PER_REAL_PHOTO, img_doc, ImageLoaderUtil.doc_options);
		doc_name.setText(model.PER_NAME);
		doc_desc.setText(model.PER_POSITION);
		doc_address.setText(model.HOS_NAME);
		if (!TextUtils.isEmpty(model.TAGS)) {
			String str[] = model.TAGS.replace("^$%", "@").split("@");
			for (int i = 0; i < str.length; i++) {
				if (TextUtils.isEmpty(str[i]))
					continue;
				switch (i) {
				case 0:
					doc_label_1.setText(str[i]);
					doc_label_1.setVisibility(View.VISIBLE);
					break;
				case 1:
					doc_label_2.setText(str[i]);
					doc_label_2.setVisibility(View.VISIBLE);
					break;
				case 2:
					doc_label_3.setText(str[i]);
					doc_label_3.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}

			}

		}
	}
}
