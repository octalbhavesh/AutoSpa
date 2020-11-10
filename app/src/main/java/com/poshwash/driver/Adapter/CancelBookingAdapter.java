package com.poshwash.driver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.databinding.DataBindingUtil;

import com.poshwash.driver.databinding.MybookingChildBinding;
import com.poshwash.driver.databinding.MybookingParentBinding;
import com.poshwash.driver.views.Activity.BookingDetail;
import com.poshwash.driver.views.Activity.MainActivity;
import com.poshwash.driver.Beans.MyBookingChildModal;
import com.poshwash.driver.Beans.MyBookingModal;
import com.poshwash.driver.Constant.GlobalControl;
import com.poshwash.driver.Constant.AutoSpaConstant;
import com.poshwash.driver.R;
import com.poshwash.driver.Utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.util.List;

public class CancelBookingAdapter extends BaseExpandableListAdapter {
    Context context;
    List<MyBookingModal> myBookingModals;
    ExpandableListView expandableListView;
    DisplayImageOptions options;
    long mLastViewClickTime = 0;


    //Constructor to initialize values
    public CancelBookingAdapter(Context context, List<MyBookingModal> myBookingModals, ExpandableListView expandableListView) {

        this.context = context;
        this.myBookingModals = myBookingModals;
        this.expandableListView = expandableListView;
        options = Util.displayOption(context);
    }

    @Override
    public int getGroupCount() {
        return myBookingModals.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return myBookingModals.get(i).getMyBookingChildModals().size();
    }

    @Override
    public Object getGroup(int i) {
        return myBookingModals.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return myBookingModals.get(i).getMyBookingChildModals().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            MybookingParentBinding mybookingParentBinding = DataBindingUtil.
                    inflate(LayoutInflater.from(context), R.layout.mybooking_parent, null, false);
            view = mybookingParentBinding.getRoot();
        }
        view.setClickable(false);
        view.setFocusable(false);

        expandableListView.expandGroup(i);
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        MybookingChildBinding mybookingChildBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.mybooking_child, null, false);
        final MyBookingChildModal myBookingChildModal = myBookingModals.get(i).getMyBookingChildModals().get(i1);

        if (myBookingChildModal.getImage() != null && !myBookingChildModal.getImage().equals("")) {
            ImageLoader.getInstance().displayImage(GlobalControl.IMAGE_BASE_URL + myBookingChildModal.getImage(), mybookingChildBinding.imgCar, options);
        } else {
            mybookingChildBinding.imgCar.setImageResource(R.drawable.no_image);
        }

        if (myBookingChildModal.getCar_name() != null)
            mybookingChildBinding.txtCarname.setText(myBookingChildModal.getCar_name());

        mybookingChildBinding.txtUsername.setText(myBookingChildModal.getOwner_name());

        mybookingChildBinding.ratingBar.setVisibility(View.GONE);
        mybookingChildBinding.ratingBar.setRating((int) Float.parseFloat(myBookingChildModal.getRating_count()));

        mybookingChildBinding.txtPrice.setText("" + Util.DisplayAmount(myBookingChildModal.getPrice()));

        try {
            mybookingChildBinding.txtTime.setText(Util.convertUtcTimeToLocal(myBookingChildModal.getBooking_date() + " " + myBookingChildModal.getBooking_time(), true, context));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (i1 == myBookingModals.get(i).getMyBookingChildModals().size() - 1)
            mybookingChildBinding.divider.setVisibility(View.GONE);
        else
            mybookingChildBinding.divider.setVisibility(View.VISIBLE);

        mybookingChildBinding.statusTv.setVisibility(View.VISIBLE);

        if (myBookingChildModal.getStatus().compareTo("0") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.initiated));
        } else if (myBookingChildModal.getStatus().compareTo("1") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.schedule_bookings));
        } else if (myBookingChildModal.getStatus().compareTo("2") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.completed));
        } else if (myBookingChildModal.getStatus().compareTo("3") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.cancelled));
        } else if (myBookingChildModal.getStatus().compareTo("4") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.refunded));
        } else if (myBookingChildModal.getStatus().compareTo("5") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.startwash));
        }
        else if (myBookingChildModal.getStatus().compareTo("6") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.cancel_by_service));
        }
        else if (myBookingChildModal.getStatus().compareTo("7") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.cancel_by_admin));
        } else if (myBookingChildModal.getStatus().compareTo("8") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.cancel_by_customer));
        }
        else if (myBookingChildModal.getStatus().compareTo("9") == 0) {
            mybookingChildBinding.statusTv.setText(context.getString(R.string.reached));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastViewClickTime < 1000) {
                    return;
                }
                mLastViewClickTime = SystemClock.elapsedRealtime();

                if (myBookingChildModal.getStatus().compareTo("1") == 0 || myBookingChildModal.getStatus().compareTo("5") == 0) {
                    ((MainActivity) context).displayView(AutoSpaConstant.HOMEFRAGMENT, myBookingChildModal, null);
                } else {
                    Intent intent = new Intent(context, BookingDetail.class);
                    intent.putExtra("bookingId", myBookingModals.get(i).getMyBookingChildModals().get(i1).getId());
                    intent.putExtra("bookingDriverId", myBookingModals.get(i).getMyBookingChildModals().get(i1).getBooking_Sp_id());
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
