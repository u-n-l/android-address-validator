package com.unl.addressvalidator.data

import java.lang.Enum
import kotlin.Exception
import kotlin.String

/**
 * [EnvironmentType] is used to store [Enum] of Tiles or styles for our Map.
 *
 * @constructor Create empty Tile enum
 */
object EnvironmentType {
    const val PROD : String = "PRODUCTION"
    const val SANDBOX : String ="SANDBOX"
}
