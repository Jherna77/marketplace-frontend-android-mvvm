package com.jhernandez.frontend.bazaar.ui.common.util;


import static com.jhernandez.frontend.bazaar.core.constants.Values.CANCELLED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.CONFIRMED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.DELIVERED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.PENDING;
import static com.jhernandez.frontend.bazaar.core.constants.Values.RETURNED;
import static com.jhernandez.frontend.bazaar.core.constants.Values.SHIPPED;

import android.content.Context;

import com.jhernandez.frontend.bazaar.R;

/* Utility class for handling order status labels.
 * Provides methods to get user-friendly labels for different order statuses.
 * It maps order status strings to string resources.
 */
public class OrderStatusUtils {

    public static String getLabel(Context context, String status) {
        return context.getString(
                switch (status) {
                    case PENDING -> R.string.status_pending;
                    case CONFIRMED -> R.string.status_confirmed;
                    case SHIPPED -> R.string.status_shipped;
                    case DELIVERED -> R.string.status_delivered;
                    case CANCELLED -> R.string.status_cancelled;
                    case RETURNED -> R.string.status_returned;
                    default -> R.string.unknown_status;
                });
    }

}
