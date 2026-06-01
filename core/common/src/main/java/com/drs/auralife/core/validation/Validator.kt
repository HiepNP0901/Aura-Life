@file:Suppress("unused")

package com.drs.auralife.core.validation

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.drs.auralife.core.common.R

class Validator(
    private val context: Context,
) {
    // Validator for required fields
    val requiredValidator = MultiValidator(
        listOf(
            RequiredValidator(errorText = getString(context, R.string.required_field)),
        ),
    )

    // Validator for double values
    val doubleValidator = MultiValidator(
        listOf(
            RequiredValidator(errorText = getString(context, R.string.required_field)),
            DoubleValidator(errorText = getString(context, R.string.must_be_a_number)),
        ),
    )

    // Validator for non-zero values
    val checkValueValidator = MultiValidator(
        listOf(
            RequiredValidator(errorText = getString(context, R.string.required_field)),
            CheckValueValidator(errorText = getString(context, R.string.other_than_0)),
        ),
    )

    // Validator for passwords with minimum length
    val passwordValidator = MultiValidator(
        listOf(
            RequiredValidator(errorText = getString(context, R.string.required_field)),
            MinLengthValidator(
                min = 6,
                errorText = getString(context, R.string.invalid_password),
            ),
        ),
    )

    // Validator for email addresses
    val emailValidator = MultiValidator(
        listOf(
            RequiredValidator(errorText = getString(context, R.string.required_field)),
            EmailValidator(errorText = getString(context, R.string.invalid_email)),
        ),
    )

    // Validator for confirming password match
    fun confirmPasswordValidator(password: String?): MultiValidator =
        MultiValidator(
            listOf(
                RequiredValidator(errorText = getString(context, R.string.required_field)),
                MatchValidator(password, errorText = getString(context, R.string.not_match_password)),
            ),
        )
}
