@file:Suppress("unused")

package com.drs.auralife.core.utils

abstract class EditTextValidator<T>(
    val errorText: String,
) {
    abstract fun isValid(value: T): Boolean

    open operator fun invoke(value: T): String? = if (isValid(value)) null else errorText
}

abstract class TextEditTextValidator(
    errorText: String,
) : EditTextValidator<String>(errorText) {
    open val ignoreEmptyValues: Boolean = true

    override operator fun invoke(value: String): String? = if (ignoreEmptyValues && value.isEmpty()) null else super.invoke(value)

    fun hasMatch(
        pattern: String,
        input: String,
        caseSensitive: Boolean = true,
    ): Boolean {
        val regex = if (caseSensitive) Regex(pattern) else Regex(pattern, RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(input)
    }
}

class RequiredValidator(
    errorText: String,
) : TextEditTextValidator(errorText) {
    override val ignoreEmptyValues: Boolean = false

    override fun isValid(value: String): Boolean = value.trim().isNotEmpty()
}

class DoubleValidator(
    errorText: String,
) : TextEditTextValidator(errorText) {
    override val ignoreEmptyValues: Boolean = false

    override fun isValid(value: String): Boolean =
        try {
            value.toDouble()
            true
        } catch (_: NumberFormatException) {
            false
        }
}

class CheckValueValidator(
    errorText: String,
) : TextEditTextValidator(errorText) {
    override val ignoreEmptyValues: Boolean = false

    override fun isValid(value: String): Boolean =
        try {
            value.toDouble() != 0.0
        } catch (_: NumberFormatException) {
            false
        }
}

class MinLengthValidator(
    private val min: Int,
    errorText: String,
) : TextEditTextValidator(errorText) {
    override val ignoreEmptyValues: Boolean = false

    override fun isValid(value: String): Boolean = value.length >= min
}

class EmailValidator(
    errorText: String,
) : TextEditTextValidator(errorText) {
    private val emailPattern: String = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"

    override fun isValid(value: String): Boolean = hasMatch(emailPattern.toString(), value)
}

class MatchValidator(
    private val password: String?,
    errorText: String,
) : TextEditTextValidator(errorText) {
    override fun isValid(value: String): Boolean = value == password
}

class NotMatchValidator(
    private val password: String?,
    errorText: String,
) : TextEditTextValidator(errorText) {
    override fun isValid(value: String): Boolean = value != password
}

class MultiValidator(
    private val validators: List<EditTextValidator<String>>,
) : EditTextValidator<String>("") {
    private var _errorText: String = ""

    override fun isValid(value: String): Boolean {
        for (validator in validators) {
            if (validator.invoke(value) != null) {
                _errorText = validator.errorText
                return false
            }
        }
        return true
    }

    override operator fun invoke(value: String): String? = if (isValid(value)) null else _errorText
}
