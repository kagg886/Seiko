package com.kagg886.seiko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.util.ProtocolInjector;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 协议收集适配器
 *
 * @author kagg886
 * @date 2023/6/2 20:15
 **/
public class ProtocolUnitAdapter extends BaseAdapter {

    private final List<ViewBinder> binder = new ArrayList<>();


    public void setCurrentProtocol(BotConfiguration.MiraiProtocol protocol) {
        binder.clear();


        ProtocolInjector injector = new ProtocolInjector(protocol);

        for (Field f : ProtocolInjector.class.getDeclaredFields()) {
            ViewBinder vb = new ViewBinder();
            f.setAccessible(true);
            vb.setTitle(f.getName());
            try {
                vb.setValue(Objects.requireNonNull(f.get(injector)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            binder.add(vb);
        }
        notifyDataSetChanged();
    }

    public ProtocolInjector pack() {
        ProtocolInjector i = new ProtocolInjector();
        for (ViewBinder vn : binder) {
            Field f = null;
            try {
                f = i.getClass().getDeclaredField(vn.getTitle());
                if (Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                f.set(i, vn.getValue());
            } catch (Exception e) {
                try {
                    f.set(i, Long.parseLong(vn.getValue()));
                } catch (Exception e1) {
                    try {
                        f.set(i, Integer.parseInt(vn.getValue()));
                    } catch (Exception e2) {
                        try {
                            f.set(i, Boolean.parseBoolean(vn.getValue()));
                        } catch (Exception e3) {
                            throw new RuntimeException(e3);
                        }
                    }
                }
            }
        }
        return i;
    }

    @Override
    public int getCount() {
        return binder.size();
    }

    @Override
    public Object getItem(int position) {
        return binder.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return binder.get(position).getRoot();
    }


    public static class ViewBinder {
        private final EditText editText;
        private final TextView textView;

        private final View root;

        public ViewBinder() {
            root = LayoutInflater.from(SeikoApplication.getSeikoApplicationContext()).inflate(R.layout.adapter_protocol_unit, null);
            editText = root.findViewById(R.id.adapter_protocol_unit_value);
            textView = root.findViewById(R.id.adapter_protocol_unit_desc);
        }

        public View getRoot() {
            return root;
        }

        public String getTitle() {
            return textView.getText().toString();
        }

        public void setTitle(String tex) {
            textView.setText(tex);
        }

        public String getValue() {
            return editText.getEditableText().toString();
        }

        public void setValue(Object val) {
            editText.setText(val.toString());
        }

        @NonNull
        @NotNull
        @Override
        public String toString() {
            return getTitle() + ":" + getValue();
        }
    }
}
