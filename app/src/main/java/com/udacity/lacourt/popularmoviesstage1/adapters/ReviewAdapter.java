package com.udacity.lacourt.popularmoviesstage1.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.lacourt.popularmoviesstage1.R;
import com.udacity.lacourt.popularmoviesstage1.model.Review;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review>  {

    public ReviewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Review> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }

        final Review currentReview = getItem(position);

        TextView reviewAuthor = convertView.findViewById(R.id.review_author);
        reviewAuthor.setText(getContext().getResources().getText(R.string.review_by) + " " + currentReview.getAuthor());

        TextView reviewContent = convertView.findViewById(R.id.review_content);
        reviewContent.setText(formatContent(currentReview.getContent()));

        TextView reviewReadMore = convertView.findViewById(R.id.review_read_more);

        final String currentReviewUrl = currentReview.getUrl();

        reviewReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentReviewUrl != null) {

                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentReviewUrl));

                    getContext().startActivity(webIntent);

                }
            }
        });

        ProgressBar trailerProgressBar = convertView.findViewById(R.id.review_progress_bar);
        trailerProgressBar.setVisibility(View.INVISIBLE);

        return convertView;
    }

    private String formatContent(String text) {
        String newText;
        int maxTextLength = 80;

        if(text.length() > maxTextLength) {

            for(int i = maxTextLength; i > 0; i-- ) {

                if(text.charAt(i) == ' ') {

                    newText = text.substring(0, i + 1) + "...";
                    return newText;
                }
            }
        }

        return text;
    }

}
