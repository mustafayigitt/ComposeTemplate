package com.ytapps.composetemplate.core.googleplay

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val manager = ReviewManagerFactory.create(context)

        fun requestReview(activity: Activity) {
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown.
                        Timber.tag("ReviewManager").d("Review flow completed")
                    }
                } else {
                    Timber.tag("ReviewManager").e(task.exception, "Review request failed")
                }
            }
        }
    }
