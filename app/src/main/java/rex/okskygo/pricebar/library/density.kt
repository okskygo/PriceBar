package rex.okskygo.pricebar.library

import android.content.res.Resources

val Int.toDp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toSp: Float
  get() = (this * Resources.getSystem().displayMetrics.scaledDensity)

val Float.toDp: Float

  get() = (this / Resources.getSystem().displayMetrics.density)

val Float.toPx: Float
  get() = (this * Resources.getSystem().displayMetrics.density)

val Float.toSp: Float
  get() = (this * Resources.getSystem().displayMetrics.scaledDensity)
