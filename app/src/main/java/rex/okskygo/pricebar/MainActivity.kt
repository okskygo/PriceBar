package rex.okskygo.pricebar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.leftPrice
import kotlinx.android.synthetic.main.activity_main.priceBar
import kotlinx.android.synthetic.main.activity_main.rightPrice
import rex.okskygo.pricebar.library.PriceDto

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        priceBar.step = 1000
        priceBar.thumbStep = 100
        priceBar.maxPrice = 30000
        val list = mutableListOf<PriceDto>()
        for (i in 0 until 500) {
            val price = (0..29000).random()
            val priceCount = (1..2).random()
            list.add(PriceDto(price, priceCount))
        }
        priceBar.prices = list
        priceBar.onChangeListener = {
            leftPrice.text = "\$${it.left}"
            rightPrice.text = "\$${it.right}"
        }
    }
}
