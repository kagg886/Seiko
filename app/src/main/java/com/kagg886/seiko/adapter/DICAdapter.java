package com.kagg886.seiko.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.dic.DICList;
import com.kagg886.seiko.dic.DictionaryEnvironment;
import com.kagg886.seiko.dic.entity.DictionaryFile;
import com.kagg886.seiko.dic.model.DICParseResult;
import com.kagg886.seiko.event.DialogBroadCast;
import com.kagg886.seiko.event.SnackBroadCast;
import com.kagg886.seiko.util.IOUtil;
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
        @SuppressLint("ViewHolder")
        View v = LayoutInflater.from(SeikoApplication.getCurrentActivity()).inflate(R.layout.adapter_dicitem, null);
        SwitchCompat sw = v.findViewById(R.id.adapter_dicitem_status);
        TextView tx = v.findViewById(R.id.adapter_dicitem_name);

        DICList l = DICList.getInstance();
        tx.setText(l.get(i).getName());

        final JSONObject a = DictionaryEnvironment.getInstance().getDicConfig().optJSONObject(l.get(i).getName());

        // 这里的判空其实不是很必要了， 留着增强健壮性
        if (a == null) {
            sw.setChecked(false);
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

            final String dicName = l.get(i).getName();

            // 勾选前需要验证插件有效性
            if(isChecked) {
                final DictionaryFile dictionaryFile = l.get(i);
                try {
                    dictionaryFile.parseDICCodeFile();
                } catch (Exception e) {
                    DialogBroadCast.sendBroadCast(text(R.string.dic_load_error, dictionaryFile.getName()), IOUtil.getException(e));
                    sw.setChecked(false);
                    // 结束函数
                    return;
                }
            }


            try {
                tmp.put("enabled", isChecked);
                DictionaryEnvironment.getInstance().getDicConfig().put(dicName, tmp);
                DictionaryEnvironment.getInstance().getDicConfig().save();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

        });
        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        DICParseResult result = DICList.getInstance().refresh();
        if(!result.success) {
            SnackBroadCast.sendBroadCast(text(R.string.dic_error));
        }
        super.notifyDataSetChanged();
    }
    private String text(@StringRes int s, Object... args) {
        return String.format(SeikoApplication.getSeikoApplicationContext().getText(s).toString(), args);
    }
}
