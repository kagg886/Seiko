package com.kagg886.seiko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kagg886.seiko.R;
import com.kagg886.seiko.SeikoApplication;
import com.kagg886.seiko.plugin.SeikoDescription;
import com.kagg886.seiko.service.BotRunnerService;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.adapter
 * @className: PluginAdapter
 * @author: kagg886
 * @description: 插件适配器
 * @date: 2022/12/22 15:33
 * @version: 1.0
 */
public class PluginAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        if (BotRunnerService.INSTANCE == null) { //服务绑定和活动加载是异步的，此时需要手动刷新程序
            return 0;
        }
        return BotRunnerService.INSTANCE.getSeikoPluginList().size();
    }

    @Override
    public Object getItem(int position) {
        return BotRunnerService.INSTANCE.getSeikoPluginList().get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(SeikoApplication.getSeikoApplicationContext()).inflate(R.layout.adapter_plugin, null);
        TextView name = v.findViewById(R.id.adapter_plugin_name);
        TextView author = v.findViewById(R.id.adapter_plugin_author);
        TextView desc = v.findViewById(R.id.adapter_plugin_description);
        TextView ver = v.findViewById(R.id.adapter_plugin_ver);
        SeikoDescription seikoDesc = BotRunnerService.INSTANCE.getSeikoPluginList().get(position).getDescription();
        name.setText(seikoDesc.getName());
        author.setText(seikoDesc.getAuthor());
        desc.setText(seikoDesc.getDesc());
        ver.setText(seikoDesc.getVerCode());
        return v;
    }
}
