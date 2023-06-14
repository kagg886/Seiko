package com.kagg886.seiko.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

    private static final List<ProtocolPair> protocolPairs = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    private static class ProtocolPair {
        private String label;
        private String value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public ProtocolUnitAdapter() {
        layoutInflater = LayoutInflater.from(SeikoApplication.getCurrentActivity());
    }


    public void setCurrentProtocol(BotConfiguration.MiraiProtocol protocol) {
        protocolPairs.clear();
        ProtocolInjector injector = new ProtocolInjector(protocol);
        for (Field f : ProtocolInjector.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            ProtocolPair protocolPair = new ProtocolPair();
            f.setAccessible(true);
            protocolPair.setLabel(f.getName());
            try {
                protocolPair.setValue(String.valueOf(Objects.requireNonNull(f.get(injector))));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            protocolPairs.add(protocolPair);
        }
        notifyDataSetChanged();
    }

    public ProtocolInjector pack() {
        ProtocolInjector i = new ProtocolInjector();
        for (ProtocolPair protocolPair : protocolPairs) {
            Field f = null;
            try {
                f = i.getClass().getDeclaredField(protocolPair.label);
                if (Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                f.set(i, protocolPair.getValue());
            } catch (Exception e) {
                try {
                    f.set(i, Long.parseLong(protocolPair.getValue()));
                } catch (Exception e1) {
                    try {
                        f.set(i, Integer.parseInt(protocolPair.getValue()));
                    } catch (Exception e2) {
                        try {
                            f.set(i, Boolean.parseBoolean(protocolPair.getValue()));
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
        return protocolPairs.size();
    }

    @Override
    public Object getItem(int position) {
        return protocolPairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_protocol_unit, null);
            viewHolder = new ViewHolder();
            viewHolder.editText = convertView.findViewById(R.id.adapter_protocol_unit_value);
            viewHolder.textView = convertView.findViewById(R.id.adapter_protocol_unit_desc);
            viewHolder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String label = viewHolder.textView.getText().toString();
                    String value = viewHolder.editText.getText().toString();
                    protocolPairs.stream()
                            .filter(pair -> pair.label.equals(label))
                            .findFirst()
                            .ifPresent(pair -> pair.setValue(value));
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ProtocolPair protocolPair = protocolPairs.get(position);
        viewHolder.textView.setText(protocolPair.label);
        viewHolder.editText.setText(protocolPair.value);
        return convertView;
    }


    // 这个文件的结构变化是为了解决listview上下滚动 输入法焦点无法切换的问题
    // 之前那种实现方式不知道为什么会有问题 研究不出 用这种方式可以解决
    public static class ViewHolder {
        private EditText editText;
        private TextView textView;
    }
}
