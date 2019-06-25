package com.udacity.lacourt.popularmoviesstage1.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.lacourt.popularmoviesstage1.R;
import com.udacity.lacourt.popularmoviesstage1.model.Trailer;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<Trailer> {


    public TrailerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Trailer> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent, false);
        }

        final Trailer currentTrailer = getItem(position);

        ImageView trailerImage = convertView.findViewById(R.id.trailer_image);
        trailerImage.setImageResource(R.mipmap.play_image);

        TextView trailerText = convertView.findViewById(R.id.trailer_text_view);
        trailerText.setText(formatText(currentTrailer.getName()));

        LinearLayout trailerLayout = convertView.findViewById(R.id.trailer_layout);

        final String trailerKey = currentTrailer.getKey();

        trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trailerKey != null) {

                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));

                    getContext().startActivity(webIntent);

                }
            }
        });

        ProgressBar trailerProgressBar = convertView.findViewById(R.id.trailer_progress_bar);
        trailerProgressBar.setVisibility(View.INVISIBLE);

        return convertView;
    }

    private String formatText(String text) {
        String newText;
//        Log.d("FORMAT", "text.legth() = " + text.length());
        if(text.length() > 23) {
            for(int i = 22; i > 0; i-- ) {
                Log.d("FORMAT",  "text.length() = " + text.length() + "text.charAt(" + i +") = " + text.charAt(i));
                if(text.charAt(i) == ' ') {
                    newText = text.substring(0, i + 1) + "\n" + text.substring(i + 1, text.length());
                    Log.d("FORMAT", "new text: " + newText + "\n" + "text");
                    return newText;
                }
            }
        }
        Log.d("FORMAT", "text: " + text);
        return text;
    }
}
