package io.github.takusan23.litebarometer.Fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.github.takusan23.litebarometer.BarometerRecyclerViewAdapter
import io.github.takusan23.litebarometer.Database.BarometerSQLiteHelper
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.fragment_barometer_list.*

class BarometerListFragment : Fragment() {

    lateinit var adapter: BarometerRecyclerViewAdapter
    val recyclerViewList = arrayListOf<ArrayList<String>>()

    //データベース
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var barometerSQLiteHelper: BarometerSQLiteHelper


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


}