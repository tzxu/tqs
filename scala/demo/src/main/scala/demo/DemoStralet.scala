package demo

import com.acqusta.tquant.api.scala.DataApi.{Bar, MarketQuote}
import com.acqusta.tquant.api.scala.TradeApi.{Order, Trade}
import com.acqusta.tquant.api.scala.{DataApi, TradeApi}
import com.acqusta.tquant.stra.realtime.Run
import com.tictactec.ta.lib.MInteger
import com.acqusta.tquant.stra.stralet.{Stralet, StraletContext}

class DemoStralet extends Stralet {

    val talib = new com.tictactec.ta.lib.Core()
    var stk_account = ""
    var trade_api : TradeApi = _
    var data_api  : DataApi = _

    var universe : Seq[String] = _

    override def onInit(sc: StraletContext): Unit = {
        super.onInit(sc)

        trade_api = sc.getTradeApi
        data_api  = sc.getDataApi

        this.stk_account = sc.getParameters[String]("stk_account", "")

        sc.log("DemoStralet onInit", sc.getTime)

        universe = sc.getParameters[Seq[String]]("universe",Seq[String]())
        data_api.subscribe( universe )

        val (positions, _) = trade_api.queryPositions(this.stk_account)
        val (balance, _) = trade_api.queryBalance(this.stk_account)

    }

    override def onFini() = {
        sc.log("DemoStralet onFini", sc.getTime)

        val (positions, _) = trade_api.queryPositions(this.stk_account)
        val (balance, _) = trade_api.queryBalance(this.stk_account)
    }

    override def onQuote(q: MarketQuote): Unit = {
//        sc.log("quote", q.code, q.date, q.time, sc.getTimeAsInt)
//
//        {
//            universe.foreach{ x =>
//                val (q, msg) = sc.getDataApi.quote(x)
//                if (q == null)
//                    sc.log(s"       quote failed: $x $msg")
//                else
//                    sc.log("       ", q.code, q.date, q.time)
//            }
//        }
    }

    override def onBar(cycle : String, bar : Bar): Unit = {

//        sc.log("bar", bar.code, bar.date, bar.time)
//
//        {
//            universe.foreach{ x =>
//                val (bars, msg) = sc.getDataApi.bar(x, cycle)
//                if (bars != null)
//                    sc.log("   ", bars.last.code, bars.last.date, bars.last.time)
//                else
//                    sc.log("   ", x, msg)
//            }
//        }

        if (cycle != "1m") return

        val (bars, msg) = sc.getDataApi.bar(bar.code, "1m")
        if (bars == null) {
            sc.log("dapi.bar failed: " + msg)
            return
        }

        // MA5 > MA60 BUY
        // MA60 < MA5 SELL
        if (bars.isEmpty || bars.size < 60) return

        val (tmp_positions, _) = trade_api.queryPositions(this.stk_account)
        val (balance, _) = trade_api.queryBalance(this.stk_account)

        val positions = tmp_positions.map ( x => x.code -> x).toMap

        var enable_balance = balance.enable_balance

        val code = bars.head.code
        val close_price = bars.map (_.close).toArray

        val ma5 = new Array[Double](close_price.length)
        val ma30 = new Array[Double](close_price.length)
        val beg_idx = new MInteger
        val ind_length = new MInteger

        talib.sma(0, close_price.length -1 , close_price, 5, beg_idx, ind_length, ma5)
        val ma5_length = ind_length.value

        talib.sma(0, close_price.length -1 , close_price, 30, beg_idx, ind_length, ma30)
        val ma30_length = ind_length.value

        if (ma30(ma30_length -1) > ma5(ma5_length-1)) {
            if (ma30(ma30_length - 2) <= ma5(ma5_length - 2)) {
                // up cross, buy some
                val (q, msg) = data_api.quote(code)
                assert ( q != null, s"quote failed: $code $msg")
                var cost = q.last * 100
                if ( cost < enable_balance) {
                    enable_balance -= cost
                    trade_api.placeOrder(stk_account, code, q.last, 100, "Buy")
                }
            }
        } else if (ma30(ma30_length -1) < ma5(ma5_length-1)) {
            if (ma30(ma30_length - 2) >= ma5(ma5_length - 2)) {
                // dead cross, sell some
                val pos = positions.getOrElse(code, null)
                if (pos != null && pos.enable_size >= 100) {
                    val (q, _) = data_api.quote(code)
                    trade_api.placeOrder(stk_account, code, q.last, 100, "Sell")
                }
            }
        }
    }

    override def onOrderStatus(order: Order): Unit = {
        sc.log("onOrder: " + order)
    }

    override def onOrderTrade(trade: Trade): Unit = {
        sc.log("onTrade: " + trade)
    }

    override def onEvent(evt: String, data: Any): Unit = {
    }

    override def onTimer(id : Int, data : Any): Unit = {
//        val (date, time) = sc.getTimeAsInt()
//        sc.log(s"onCycle $date $time")
    }
}

object DemoStralet extends App {

    if (args.isEmpty) {
        println("Usage: DemoStralet [backtest|realtime]")
        System.exit(0)
    }

    args(0) match {
        case "backtest" => com.acqusta.tquant.stra.backtest.Run.runPath("etc/demostralet.conf", "etc/backtest.conf")
        case "realtime" => Run.runPath("etc/demostralet.conf", "etc/realtime.conf")
    }

}