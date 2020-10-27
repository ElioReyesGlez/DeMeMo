package com.erg.memorized.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.erg.memorized.R;
import com.erg.memorized.model.Evaluator;

import java.util.ArrayList;

public class AdapterEvaluator extends ArrayAdapter<Evaluator> {

    private final int resource;
    private final Context context;
    private final ArrayList<Evaluator> evaluators;

    public AdapterEvaluator(@NonNull Context context, int resource,
                            @NonNull ArrayList<Evaluator> evaluators) {
        super(context, resource, evaluators);
        this.evaluators = evaluators;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return evaluators == null ? 0 : evaluators.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;

        Evaluator evaluator = evaluators.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder(convertView);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.setIcon(evaluator.getImage());
        viewHolder.setEvaluatorName(evaluator.getName());
        viewHolder.setDescription(evaluator.getDescription());

        return convertView;
    }

    public static class ViewHolder {
        private final ImageView icon;
        private final TextView evaluatorName;
        private final TextView description;

        public ViewHolder(View convertView) {
            this.icon = convertView.findViewById(R.id.iv_section);
            this.evaluatorName = convertView.findViewById(R.id.tv_evaluator_name);
            this.description = convertView.findViewById(R.id.tv_info_section);
        }

        public void setIcon(int res) {
            this.icon.setImageResource(res);
        }

        public void setEvaluatorName(String evaluatorName) {
            this.evaluatorName.setText(evaluatorName);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }
    }
}
