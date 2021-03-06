package com.oolive.xianrou.adapter.viewholder;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.live.R;
import com.oolive.xianrou.model.XRReportTypeModel;
import com.oolive.xianrou.util.ViewUtil;

/**
 * @包名 com.fanwe.xianrou.adapter.viewholder
 * @描述
 * @作者 Su
 * @创建时间 2017/4/7 9:38
 **/
public class XRReportTypeViewHolder extends XRBaseViewHolder<XRReportTypeModel> {
    private AppCompatRadioButton radioButton;
    private TextView nameTextView;

    public XRReportTypeViewHolder(ViewGroup parent, @LayoutRes int layout) {
        super(parent, layout);
        radioButton = (AppCompatRadioButton) itemView.findViewById(R.id.rb_xr_view_holder_report_type);
        nameTextView = (TextView) findViewById(R.id.tv_name_xr_view_holder_report_type);
    }

    @Override
    public void bindData(Context context, XRReportTypeModel entity, int position) {
        setHolderEntity(entity);
        setHolderEntityPosition(position);

        ViewUtil.setText(nameTextView, entity.getName());
    }

    public void setChecked(boolean checked) {
        radioButton.setChecked(checked);
    }


}
