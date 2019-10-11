package io.github.takusan23.litebarometer.Fragment

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import io.github.takusan23.litebarometer.BarometerRecyclerViewAdapter
import io.github.takusan23.litebarometer.Database.BarometerSQLiteHelper
import io.github.takusan23.litebarometer.MainActivity
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.fragment_barometer_list.*

class BarometerListFragment : Fragment() {

    lateinit var adapter: BarometerRecyclerViewAdapter
    val recyclerViewList = arrayListOf<ArrayList<String>>()

    //データベース
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var barometerSQLiteHelper: BarometerSQLiteHelper

    var color = Color.BLACK


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barometer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //recyclerview
        fragment_barometer_list_recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        fragment_barometer_list_recyclerview.layoutManager = layoutManager
        adapter = BarometerRecyclerViewAdapter(recyclerViewList)
        fragment_barometer_list_recyclerview.adapter = adapter

        //データベース読み込み
        initDB()
        loadDB()

        fragment_barometer_delete_button.setOnClickListener {
            Snackbar.make(it, getString(R.string.delete_history_mes), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.db_clear)) {
                    //履歴削除
                    deleteDB()
                }.show()
        }

        //折れ線グラフ用意
        initLineChart()

    }

    private fun initLineChart() {
        history_linechart.data = getLineChart()
        //グラフの説明。右下に出る
        val description = Description()
        description.text = getString(R.string.barometer_history)
        description.textColor = color
        history_linechart.description = description
        //ハイライト機能
        history_linechart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                //選択時
                val selectValue = (e?.y).toString()
                //選択したときの内容をRecyclerViewに入れる
                recyclerViewList.clear()
                adapter.notifyDataSetChanged()
                selectLoadDB(selectValue)
            }
        })
    }

    fun getLineChart(): LineData {
        val item = mutableListOf<Entry>()

        //DB
        val cursor =
            sqLiteDatabase.query(
                "barometer_db",
                arrayOf("value_float", "unix"),
                null,
                null,
                null,
                null,
                null
            )

        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            //横軸
            val y = i.toFloat()
            //縦軸は気圧
            val pos = cursor.getString(0).toFloat()
            item.add(Entry(y, pos))
            cursor.moveToNext()
        }
        cursor.close()

        //線の色とかはダークモード有効時に見えなくなるので
        if (activity is MainActivity) {
            if ((activity as MainActivity).isDarkMode) {
                color = Color.WHITE
                //一緒にグラフの縦（左右）の軸のテキストの色
                val rightLine = history_linechart.axisLeft
                rightLine.textColor = color
                val leftLine = history_linechart.axisRight
                leftLine.textColor = color
                //このグラフが何を指してるのか表示するラベルの色
                val upLine = history_linechart.legend
                upLine.textColor = color
                //横軸？
                val xLine = history_linechart.xAxis
                xLine.textColor = color
            }
        }

        val line = LineDataSet(item, getString(R.string.barometer_history)).apply {
            axisDependency = YAxis.AxisDependency.LEFT
            //ダークモードのときだけ青のほうがかっこよかった
            if (activity is MainActivity) {
                if ((activity as MainActivity).isDarkMode) {
                    color = color
                } else {
                    color = Color.BLACK
                }
            }
            highLightColor = Color.RED
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawValues(false)
            lineWidth = 2f
        }

        val data = LineData(line)
        data.setValueTextColor(Color.BLACK)
        data.setValueTextSize(9f)
        return data
    }


    private fun deleteDB() {
        //全削除
        sqLiteDatabase.delete("barometer_db", null, null)
        adapter.notifyDataSetChanged()
    }

    private fun initDB() {
        if (!this@BarometerListFragment::barometerSQLiteHelper.isInitialized && context != null) {
            //初期化してないときはいる
            barometerSQLiteHelper = BarometerSQLiteHelper(context!!)
            sqLiteDatabase = barometerSQLiteHelper.writableDatabase
            barometerSQLiteHelper.setWriteAheadLoggingEnabled(false)
        }
    }

    fun loadDB() {
        val cursor = sqLiteDatabase.query(
            "barometer_db",
            arrayOf("create_at", "value", "value_float"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            val item = arrayListOf<String>()
            item.add("")
            item.add(cursor.getString(0))
            item.add(cursor.getString(1))
            item.add(cursor.getString(2))
            recyclerViewList.add(0, item)
            cursor.moveToNext()
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }

    fun selectLoadDB(value: String) {
        val cursor = sqLiteDatabase.query(
            "barometer_db",
            arrayOf("create_at", "value", "value_float"),
            "value_float=?",  //選択条件。?にその下の配列の内容が入る
            arrayOf(value), //検索中身
            null,
            null,
            null
        )
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            val item = arrayListOf<String>()
            item.add("")
            item.add(cursor.getString(0))
            item.add(cursor.getString(1))
            item.add(cursor.getString(2))
            recyclerViewList.add(0, item)
            cursor.moveToNext()
        }
        cursor.close()
        adapter.notifyDataSetChanged()
    }


}