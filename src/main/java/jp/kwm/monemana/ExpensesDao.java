package jp.kwm.monemana;

import jp.kwm.monemana.HomeController.Expense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

// ExpensesDaoクラス
@Service
public class ExpensesDao {

    /* フィールド */
    private final JdbcTemplate jdbcTemplate;    // SQLを実行するインスタンス

    /**
     * コンストラクタ
     *
     * @param jdbcTemplate SQLを実行するインスタンス
     */
    @Autowired
    ExpensesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * データベースから1ヶ月分のイベントを取得してListで返す
     *
     * @param month     対象の月
     * @param lastDay   対象の月の末日
     * @return          イベントのList
     */
    public List<Expense> findMonthAll(String month, String lastDay) {

        // 実行するSQLを文字列で生成
        String query = "SELECT * FROM expenses WHERE date BETWEEN '" + month + "-01' AND '" + month + "-" + lastDay + "'";

        // SQLで得たListを格納
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // Listの中身をMapからレコードに変換（新しく生成）
        List<Expense> expenses = result.stream()
                .map((Map<String, Object> row) -> new Expense(
                        row.get("id").toString(),
                        (Boolean) row.get("expenseBool"),
                        (Date) row.get("date"),
                        (int) row.get("amount"),
                        row.get("category").toString(),
                        row.get("account").toString(),
                        row.get("memo").toString()))
                .toList();

        // レコードのListを返す
        return expenses;
    }

    /**
     *　データベースにレコードを追加
     *
     * @param expense   追加するレコード
     */
    public void add(Expense expense) {

        // レコードをデータベースで使用できる形に変換
        SqlParameterSource param = new BeanPropertySqlParameterSource(expense);

        // データベースに挿入するインスタンス
        SimpleJdbcInsert insert =
                new SimpleJdbcInsert(jdbcTemplate)
                        // 使用するテーブルを指定
                        .withTableName("expenses");

        // 変換したレコードを使用して挿入を実行
        insert.execute(param);
    }

    /**
     * データベースからレコードを削除
     *
     * @param id    削除するレコードのID
     */
    public int delete(String id){

        // SQLに引数をバインドして更新操作を実行
        int num = jdbcTemplate.update("DELETE FROM expenses WHERE id = ?", id);

        // 更新した行数を返す
        return num;
    }
}
