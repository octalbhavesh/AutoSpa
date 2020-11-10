package com.poshwash.driver.Delegate;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface MyApiEndpointInterface {

    @POST("change_password")
    Call<ResponseBody> changePassword(@Body Map<String, Object> changePasswordRequest);

    @POST("driver_login")
    Call<ResponseBody> driver_login(@Body Map<String, Object> loginRequest);

    @POST("forget_password_sp")
    Call<ResponseBody> forget_password_sp(@Body Map<String, Object> forgotRequest);

    @Multipart
    @POST("editprofile")
    Call<ResponseBody> editProfile(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("editprofile")
    Call<ResponseBody> editProfileWithoutImage(@PartMap Map<String, RequestBody> partMap);

    @POST("update_location_sp")
    Call<ResponseBody> updateLocation(@Body Map<String, Object> updateLocation);

    @POST("pages_view")
    Call<ResponseBody> static_content(@Body Map<String, Object> static_contents);

    @POST("booking_detail")
    Call<ResponseBody> booking_detail(@Body Map<String, Object> booking_detail);

    @POST("start_wash")
    Call<ResponseBody> startWash(@Body Map<String, Object> vechicle_list);

    @POST("start_moving_on")
    Call<ResponseBody> start_moving_on(@Body Map<String, Object> start_moving_on);

    @POST("cancel_booking_user")
    Call<ResponseBody> cancelBookingUser(@Body Map<String, Object> cancel_booking);

    @POST("my_bookings")
    Call<ResponseBody> my_bookings(@Body Map<String, Object> mybooking);

    @POST("cancel_booking_listing")
    Call<ResponseBody> cancelBookingListing(@Body Map<String, Object> cancel_booking_listing);

    @POST("transaction_list")
    Call<ResponseBody> myTransaction(@Body Map<String, Object> mytransaction);

    @POST("notification_list")
    Call<ResponseBody> showNotification(@Body Map<String, Object> shownotification);

    @POST("cancel_booking_list_sp")
    Call<ResponseBody> cancel_booking_list_sp(@Body Map<String, Object> shownotification);

    @POST("my_profile_sp")
    Call<ResponseBody> getProfile(@Body Map<String, Object> getProfile);

    @POST("logout")
    Call<ResponseBody> logOut(@Body Map<String, Object> logOut);

    @POST("accept_booking")
    Call<ResponseBody> accept_booking(@Body Map<String, Object> findNearLocation);

    @POST("cancel_booking")
    Call<ResponseBody> autoCancelBooking(@Body Map<String, Object> findNearLocation);

    @POST("customer_rating")
    Call<ResponseBody> customer_rating(@Body Map<String, Object> findNearLocation);

    @POST("payment_confirmation")
    Call<ResponseBody> paymentConfirmation(@Body Map<String, Object> payment_confirmation);

    @POST("complete_wash")
    Call<ResponseBody> complete_wash(@Body Map<String, Object> completeWash);

    @POST("update_notification_setting")
    Call<ResponseBody> update_notification_setting(@Body Map<String, Object> notificationStatus);

    @POST("notification_status")
    Call<ResponseBody> notificationSettings(@Body Map<String, Object> notificationStatus);

    @POST("update_status")
    Call<ResponseBody> update_status(@Body Map<String, Object> spStatus);

    @POST("verify_otp")
    Call<ResponseBody> verifyOtp(@Body Map<String, Object> verifyOtp);

    @POST("resend_otp")
    Call<ResponseBody> resendOtp(@Body Map<String, Object> resendOtp);

    @POST("get_fair_data")
    Call<ResponseBody> getFairData(@Body Map<String, Object> get_fair_data);

    @POST("pending_request_sp")
    Call<ResponseBody> pendingRequestSp(@Body Map<String, Object> pending_request_sp);

    @POST("generate_otp")
    Call<ResponseBody> generateOtp(@Body Map<String, Object> generate_otp);

    @POST("update_contact_number")
    Call<ResponseBody> updateContactNumber(@Body Map<String, Object> generate_otp);

    @POST("cancel_booking_by_sp")
    Call<ResponseBody> cancelBookingBySp(@Body Map<String, Object> cancel_booking_by_sp);

    @POST("get_booking_detail")
    Call<ResponseBody> getBookingDetail(@Body Map<String, Object> get_booking_detail);

    @POST("notification_status")
    Call<ResponseBody> readNotification(@Body Map<String, Object> read_notification);

    @POST("remove_notification")
    Call<ResponseBody> removeNotification(@Body Map<String, Object> removeNotification);

    @POST("get_not")
    Call<ResponseBody> get_not(@Body Map<String, Object> removeNotification);

    @POST("remove_notification_all")
    Call<ResponseBody> removeNotificationAll(@Body Map<String, Object> removeNotification);

    @POST("remove_profile_pic")
    Call<ResponseBody> removeProfilePic(@Body Map<String, Object> removeNotification);

    @POST("deactivate_user")
    Call<ResponseBody> deactivateUser(@Body Map<String, Object> deactivateUser);

}