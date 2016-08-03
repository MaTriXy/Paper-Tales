package com.james.papertales.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.james.papertales.R;
import com.james.papertales.Supplier;
import com.james.papertales.utils.ImageUtils;
import com.james.papertales.activities.WallActivity;
import com.james.papertales.data.WallData;
import com.james.papertales.views.CustomImageView;
import com.james.papertales.views.SquareImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<WallData> walls;
    private Activity activity;
    private int layoutMode = 0;
    public final static int LAYOUT_MODE_HORIZONTAL = 1, LAYOUT_MODE_COMPLEX = 2;

    private Supplier supplier;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public View imagel;
        public ViewHolder(View v, View imagel) {
            super(v);
            this.v = v;
            this.imagel = imagel;
        }
    }

    public ListAdapter(Activity activity, ArrayList<WallData> walls) {
        this.activity = activity;
        this.walls = walls;
        supplier = (Supplier) activity.getApplicationContext();
    }

    public void setLayoutMode(int mode) {
        layoutMode = mode;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutMode == LAYOUT_MODE_COMPLEX ? R.layout.layout_item_complex : R.layout.layout_item, parent, false);
        return new ViewHolder(v, v.findViewById(R.id.image));
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, int position) {
        final CustomImageView image = (CustomImageView) holder.imagel;
        image.setImageBitmap(null);

        ((TextView) holder.v.findViewById(R.id.title)).setText(walls.get(position).name);

        holder.v.findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, WallActivity.class);
                intent.putExtra("wall", walls.get(holder.getAdapterPosition()));
                intent.putExtra("up", "Flat");

                if (image.getDrawable() != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Drawable prev = image.getDrawable();
                    if (prev instanceof TransitionDrawable) prev = ((TransitionDrawable) image.getDrawable()).getDrawable(1);
                    Bitmap bitmap;
                    try {
                        bitmap = ImageUtils.drawableToBitmap(prev);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.startActivity(intent);
                        return;
                    }
                    byte[] b = baos.toByteArray();
                    intent.putExtra("preload", b);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(v, bitmap, 5, 5);
                    ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight());
                    activity.startActivity(intent, options.toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });

        if (layoutMode == LAYOUT_MODE_HORIZONTAL && image instanceof SquareImageView) ((SquareImageView) image).setOrientation(SquareImageView.HORIZONTAL);

        if (layoutMode == LAYOUT_MODE_COMPLEX) {
            ((TextView) holder.v.findViewById(R.id.author)).setText(walls.get(position).authorName);
        }

        WallData data = walls.get(holder.getAdapterPosition());
        if (data.images.size() > 0) Glide.with(activity).load(data.images.get(0)).into(image);
    }

    @Override
    public int getItemCount() {
        return walls.size();
    }
}
