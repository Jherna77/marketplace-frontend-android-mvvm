package com.jhernandez.frontend.bazaar.ui.common.util;

import static com.jhernandez.frontend.bazaar.core.constants.Values.BASE_URL;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jhernandez.frontend.bazaar.R;
import com.jhernandez.frontend.bazaar.domain.callback.ConfirmationCallback;
import com.jhernandez.frontend.bazaar.domain.error.ApiErrorResponse;
import com.jhernandez.frontend.bazaar.domain.error.ValidationError;
import com.jhernandez.frontend.bazaar.domain.model.Message;
import com.jhernandez.frontend.bazaar.domain.model.Order;
import com.jhernandez.frontend.bazaar.domain.model.User;

import java.text.Normalizer;
import java.util.List;

/* Utility class for common view-related functionalities.
 * Provides methods for displaying dialogs, toasts, formatting strings,
 * handling keyboard visibility, and animating UI elements.
 */
public class ViewUtils {

    public record SelectionDialogData<T>(List<T> items, List<String> itemNames,
                                         boolean[] checkedItems) {
    }

    // Show a Toast dialog
    public static void showToast(Context context, String message) {
        Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT)
                .show();
    }

    // Overload with resource id parameter
    public static void showToast(Context context, Integer resId) {
        showToast(context, context.getString(resId));
    }

    // Show an error dialog
    public static void errorDialog(Context context, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.error_title))
                .setMessage(message)
                .setIcon(R.drawable.ic_error)
                .setNegativeButton(context.getString(R.string.ok_resp), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Overload with resource id parameter
    public static void errorDialog(Context context, Integer resId) {
        errorDialog(context, context.getString(resId));
    }

    // Overload with ApiErrorResponse parameter
    public static void errorDialog(Context context, ApiErrorResponse error) {
        if (error != null) {
            String message = ErrorUtils.getMessage(context, error.getApiError());
            errorDialog(context, message);
        }
    }

    // Overload with ValidationError parameter
    public static void errorDialog(Context context, ValidationError error) {
        if (error != null) {
            String message = ErrorUtils.getMessage(context, error);
            errorDialog(context, message);
        }
    }

    // Show an information dialog
    public static void infoDialog(Context context, String title, String msg) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(R.drawable.ic_info)
                .setNegativeButton(context.getString(R.string.ok_resp), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Show a confirmation dialog
    public static void confirmActionDialog(Context context, String title, String msg, ConfirmationCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(R.drawable.ic_warning)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.confirm_resp), (dialog, which) -> callback.onResult(true))
                .setNegativeButton(context.getString(R.string.cancel_resp), (dialog, which) -> callback.onResult(false))
                .create()
                .show();
    }

    // Create a progress dialog
    public static AlertDialog createProgressDialog(Context context, String title) {
        return (new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(context.getString(R.string.pd_msg))
                .setIcon(R.drawable.ic_wait)
                .setView(new ProgressBar(context))
                .setCancelable(false))
                .create();
    }

    public static AlertDialog createProgressDialog(Context context) {
        return (new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.loading_title))
                .setMessage(context.getString(R.string.pd_msg))
                .setIcon(R.drawable.ic_wait)
                .setView(new ProgressBar(context))
                .setCancelable(false))
                .create();
    }

    // Show terms and conditions
    public static void showTerms(Context context) {
        infoDialog(
                context,
                context.getString(R.string.terms_title),
                context.getString(R.string.terms_description) + "\n\n" +
                        context.getString(R.string.terms_admin_account) + "\n" +
                        context.getString(R.string.terms_admin_account_bot) + "\n" +
                        context.getString(R.string.terms_admin_account_protection) + "\n" +
                        context.getString(R.string.terms_admin_account_session) + "\n" +
                        context.getString(R.string.terms_admin_account_email) + "\n\n" +
                        context.getString(R.string.terms_admin_account_resolution));
    }

    // Show privacy policy
    public static void showPrivacy(Context context) {
        infoDialog(
                context,
                context.getString(R.string.privacy_title),
                context.getString(R.string.privacy_description) + "\n" +
                        context.getString(R.string.privacy_data) + "\n" +
                        context.getString(R.string.privacy_ratings) + "\n" +
                        context.getString(R.string.privacy_payment) + "\n" +
                        context.getString(R.string.privacy_profile));
    }

    // Show about BaZaaR...
    public static void showAbout(Context context) {
        infoDialog(
                context,
                context.getString(R.string.about_title),
                context.getString(R.string.about_description));
    }

    public static void showImageOnImageView(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(BASE_URL + imageUrl)
                .placeholder(R.drawable.img_image)
                .error(R.drawable.img_error_image)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void showImageOnImageView(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.img_image)
                .error(R.drawable.img_error_image)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void showErrorOnTextView(Context context, ApiErrorResponse error, TextView textView) {
        if (error != null) {
            String message = ErrorUtils.getMessage(context, error);
            textView.setText(message);
            rumbleElement(textView);
        }
    }

    public static void showErrorOnTextView(Context context, ValidationError error, TextView textView) {
        if (error != null) {
            String message = ErrorUtils.getMessage(context, error);
            textView.setText(message);
            rumbleElement(textView);
        }
    }

    public static void showSuccessToast(Context context, Boolean success) {
        if (success) {
            showToast(context, R.string.toast_success_action);
        }
    }

    public static void showErrorToast(Context context, ApiErrorResponse error) {
        if (error != null) showToast(context, ErrorUtils.getMessage(context, error));
    }

    public static void showErrorToast(Context context, ValidationError error) {
        if (error != null) showToast(context, ErrorUtils.getMessage(context, error));
    }

    public static void showCancelToast(Context context, Boolean cancelled) {
        if (cancelled) {
            showToast(context, R.string.toast_canceled_action);
        }
    }

    public static void showNotImplementedToast(Context context) {
        showToast(context, R.string.toast_not_implemented);
    }

    public static String normalize(String text) {
        String normalized = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String nameFormatter(User user) {
        return user.name() + " " + user.surnames();
    }

    public static String nameEmailFormatter(User user) {
        return user.name() + " (" + user.email() + ")";
    }

    public static String addressFormatter(User user) {
        return user.address() + ", " + cityFormatter(user);
    }

    public static String cityFormatter(User user) {
        return user.city() + ", " + user.province() + " (" + user.zipCode() + ")";
    }

    public static String orderFormatter(Context context, Order order) {
        return order.orderDate() + " - " + OrderStatusUtils.getLabel(context, order.status())
                + " - " + order.item().getProduct().name();
    }

    public static String messageFormatter(Message message) {
        return message.messageDate() + " - " + message.content();
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void selectCard(Context context, MaterialCardView card) {
        card.setStrokeWidth(4);
        card.setStrokeColor((ContextCompat.getColor(context, R.color.bazaar)));
    }

    public static void unselectCard(MaterialCardView card) {
        card.setStrokeWidth(0);
    }

    public static void rumbleElement(View element) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator translateX = ObjectAnimator.ofFloat(
                element,
                "translationX",
                0f, -5f, 5f, -2f, 2f, 0f
        );
        translateX.setDuration(100);
        translateX.setRepeatCount(5);

        animatorSet.play(translateX);
        animatorSet.start();
    }

    public static void animateIcon(View iconView) {
        ObjectAnimator bounce = ObjectAnimator.ofFloat(iconView, "translationY", 0, -20, 0);
        bounce.setDuration(500);
        bounce.setInterpolator(new BounceInterpolator());
        bounce.start();
    }

}
