package it.massimoregoli.mpacdemo.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.massimoregoli.mpacdemo.R
import it.massimoregoli.mpacdemo.activities.chart.BarChartActivity
import it.massimoregoli.mpacdemo.activities.chart.LineChartActivity
import it.massimoregoli.mpacdemo.activities.chart.PieChartActivity
import it.massimoregoli.mpacdemo.adapter.chart.BarInsertDataAdapter
import it.massimoregoli.mpacdemo.adapter.chart.LineInsertDataAdapter
import it.massimoregoli.mpacdemo.adapter.chart.PieInsertDataAdapter


class InsertDataActivity : AppCompatActivity() {
    private var idGraph = 0
    private lateinit var adapter: LineInsertDataAdapter
    private lateinit var bAdapter: BarInsertDataAdapter
    private lateinit var pAdapter: PieInsertDataAdapter
    private lateinit var holder: Holder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_data)

        //getting the intent and its data
        val intentData = intent
        idGraph = intentData.getIntExtra("value", 6)
        holder = Holder(idGraph, this)
    }

    inner class Holder(id: Int, var context: Context) : View.OnClickListener {
        private var layoutManager: RecyclerView.LayoutManager
        private var rvInsertLine = findViewById<RecyclerView>(R.id.rvInsertLine)
        private fun setAdapter(id: Int) {
            when (id) {
                0 -> {
                    adapter = LineInsertDataAdapter()
                    rvInsertLine.adapter = adapter
                }
                1 -> {
                    bAdapter = BarInsertDataAdapter()
                    rvInsertLine.adapter = bAdapter
                }
                2 -> {
                    pAdapter = PieInsertDataAdapter()
                    rvInsertLine.adapter = pAdapter
                }
            }
        }

        override fun onClick(v: View) {
            //adding the right CartView for insertion of the chart data on the basis of the previous clicked chart
            //if the button btnAddCard was clicked
            if (v.id == R.id.btnAddCard) {
                when (idGraph) {
                    0 -> adapter.addCard()
                    1 -> bAdapter.addCard()
                    2 -> pAdapter.addCard()
                }
            } else if (v.id == R.id.btnGenerate) {
                var intent: Intent? = null
                var b = true
                when (idGraph) {
                    0 -> if (check(adapter.xAxis) && check(adapter.yAxis)) {
                        intent = Intent(this@InsertDataActivity, LineChartActivity::class.java)
                        intent.putExtra("names", adapter.names)
                        intent.putExtra("xAxises", adapter.xAxis)
                        intent.putExtra("yAxises", adapter.yAxis)
                        intent.putExtra("n", adapter.itemCount)
                    } else {
                        b = false
                    }
                    1 -> if (check(bAdapter.yAxis)) {
                        intent = Intent(this@InsertDataActivity, BarChartActivity::class.java)
                        intent.putExtra("groups", bAdapter.names)
                        intent.putExtra("xAxises", bAdapter.xAxis)
                        intent.putExtra("yAxises", bAdapter.yAxis)
                        intent.putExtra("n", bAdapter.itemCount)
                    } else {
                        b = false
                    }
                    2 -> if (check(pAdapter.getvaluesSlice()) && check2(pAdapter.getvaluesSlice())) {
                        intent = Intent(this@InsertDataActivity, PieChartActivity::class.java)
                        intent.putExtra("names", pAdapter.names)
                        intent.putExtra("values", pAdapter.getvaluesSlice())
                        intent.putExtra("n", pAdapter.itemCount)
                    } else {
                        b = false
                    }
                }
                if (b) startActivity(intent) else Toast.makeText(
                    context,
                    context.resources.getText(R.string.toast_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun check(strings: Array<String?>): Boolean {
            for (i in strings.indices) {
                if (strings[i]?.length == 0) {
                    return false
                }
                for (j in 0 until (strings[i]?.length ?: 0)) {
                    val a = strings[i]?.replace(",", "0")?.replace(".", "0")?.replace(" ", "0")
                    if (!a?.get(j)?.let { Character.isDigit(it) }!!) {
                        return false
                    }
                }
            }
            return true
        }

        private fun check2(strings: Array<String?>): Boolean {
            for (i in strings.indices) {
                if (strings[i]?.isEmpty() == true) {
                    return false
                }
                for (j in 0 until strings[i]!!.length - 1) {
                    var a = strings[i]
                    while (a?.startsWith(" ") == true) {
                        a = a.substring(1)
                    }
                    if (!(Character.isDigit(a?.get(j)!!) || a[j] == '.') && Character.isDigit(
                            a[j + 1]
                        )
                    ) {
                        return false
                    }
                }
            }
            return true
        }

        init {

            //attaching the views by their id
            val btnGenerate: Button = findViewById(R.id.btnGenerate)
            val btnAddCard: Button = findViewById(R.id.btnAddCard)

            //setting the OnClickListeners on the buttons
            btnGenerate.setOnClickListener(this)
            btnAddCard.setOnClickListener(this)

            //setting LinearLayoutManager for the RecyclerView of insertion chart data
            layoutManager = object : LinearLayoutManager(this@InsertDataActivity) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            rvInsertLine.layoutManager = layoutManager

            //setting adapter for the RecyclerView on the basis of the clicked chart
            setAdapter(id)
        }
    }
}