package com.kagg886.seiko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import org.json.JSONObject;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.adapter
 * @className: DICAdapter
 * @author: kagg886
 * @description: 管理DIC的Adapter
 * @date: 2023/1/9 18:50
 * @version: 1.0
 */
public class DICAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        if (DICList.getInstance() == null) {
            return 0;
        }
        return DICList.getInstance().size();
    }

    @Override
    public Object getItem(int i) {
        return DICList.getInstance().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(SeikoApplication.getSeikoApplicationContext()).inflate(R.layout.adapter_dicitem, null);
        SwitchCompat sw = v.findViewById(R.id.adapter_dicitem_status);
        TextView tx = v.findViewById(R.id.adapter_dicitem_name);

        DICList l = DICList.getInstance();
        tx.setText(l.get(i).getName());

        final JSONObject a = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(l.get(i).getName());
        if (a == null) {
            sw.setChecked(true);
        } else {
            sw.setChecked(a.optBoolean("enabled",true));
        }

        sw.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!compoundButton.isPressed()) {
                return;
            }
            JSONObject tmp;
            if (a == null) {
                tmp = new JSONObject();
            } else {
                tmp = a;
            }
            try {
                tmp.put("enabled", isChecked);
                DictionaryEnvironment.getInstance().getDicConfig().put(l.get(i).getName(), tmp);
                DictionaryEnvironment.getInstance().getDicConfig().save();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

        });
        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        DICList.getInstance().refresh();
        super.notifyDataSetChanged();
    }
}
