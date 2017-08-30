package xtz.tquant.stra.stralet

import java.time.LocalDateTime

import xtz.tquant.api.scala.DataApi
import xtz.tquant.api.scala.DataApi.Bar
import xtz.tquant.api.scala.TradeApi


trait Stralet {

    private var _sc : StraletContext = _

    def sc : StraletContext = _sc

    def onInit(sc: StraletContext) : Unit = {
        this._sc = sc
    }

    def onFini() : Unit = {}

    def onQuote(q: DataApi.MarketQuote) : Unit = {}

    def onBar(bar: Seq[Bar]) : Unit = {}

    def onTimer(id: Int, data: Any) : Unit = {}

    def onEvent(evt: String, data : Any) : Unit = {}

    def onOrderStatus(order: TradeApi.Order) : Unit = {}

    def onOrderTrade (trade : TradeApi.Trade) : Unit = {}
}

trait StraletContext {

    /**
      *
      * @return (date, time_ms)
      */
    def getTimeAsInt : (Int, Int)

    def getTime : LocalDateTime

    def setTimer(id: Int, delay: Int, data: Any)

    def killTimer(id: Int)

    def postEvent(evt: String, data : Any)

    def getTradeApi : TradeApi

    def getDataApi : DataApi

    def log(data : Any) : Unit

    def getParameters[T: Manifest](name : String, def_value: T) : T
}
