package jp.kwm.monemana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

// HomeControllerクラス
@Controller
public class HomeController {

    /* フィールド */
    record Expense(String id,                               // ID（キー値）
                   boolean expenseBool,                     // 支出と収入を識別
                   Date date,                               // イベントの日付
                   int amount,                              // 金額
                   String category,                         // カテゴリー
                   String account,                          // アカウント
                   String memo) {
    }                           // メモ

    private List<Expense> expenses = new ArrayList<>();     // レコードを格納するArrayList
    private final ExpensesDao dao;                          // データベースにアクセスするインスタンス
    Calendar cal = Calendar.getInstance();                  // アプリの日時情報

    /**
     * コンストラクタ
     *
     * @param dao データベースにアクセスするインスタンス
     */
    @Autowired
    HomeController(ExpensesDao dao) {
        this.dao = dao;
    }

    /**
     * カレンダーとイベント一覧を表示
     *
     * @param model 　Viewへ変数を渡すインスタンス
     * @return View（ベース名を指定）
     */
    @GetMapping("/calendar")
    String monthExpenses(Model model) {

        // Calendarインスタンスから年と月をint型配列に格納
        int[] yearMonth = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1};

        // Viewへ年と月を同時に渡す
        model.addAttribute("yearMonth", yearMonth);

        // データベースから1ヶ月分のイベントを取得
        List<Expense> expenses = dao.findMonthAll(yearMonth[0] + "-" + yearMonth[1], String.valueOf(cal.getActualMaximum(Calendar.DATE)));

        // Viewへ1ヶ月分のイベントを渡す
        model.addAttribute("expenseList", expenses);

        // 適用するView（ベース名）
        return "homeCalendar";
    }

    /**
     * 1ヶ月前へ移動
     *
     * @return /calendarヘ転送
     */
    @GetMapping("/prev")
    String prevMonth() {

        // Calendarインスタンスから1月引く
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);

        // /calendarへ転送
        return "redirect:/calendar";
    }

    /**
     * 1ヶ月後へ移動
     *
     * @return  /calendarへ転送
     */
    @GetMapping("/next")
    String nextMonth() {

        // Calendarインスタンスに1月足す
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);

        // /calendarへ転送
        return "redirect:/calendar";
    }

    /**
     * イベントを追加
     *
     * @param expenseBoolStr    費用か収入か
     * @param date              日付
     * @param amount            金額
     * @param category          カテゴリー
     * @param account           アカウント
     * @param memo              メモ
     * @return                  /calendarへ転送
     */
    @GetMapping("/add")
    String addExpense(@RequestParam("expenseBool") String expenseBoolStr,
                      @RequestParam("date") Date date,
                      @RequestParam("amount") int amount,
                      @RequestParam("category") String category,
                      @RequestParam("account") String account,
                      @RequestParam("memo") String memo) {

        // 費用か収入を文字列からbooleanに変換
        boolean expenseBool = true;
        // 収入ならfalse
        if (expenseBoolStr.equals("収入")) {
            expenseBool = false;
        }

        // IDにランダムな8桁の英数字を格納
        String id = UUID.randomUUID().toString().substring(0, 8);

        // 入力された値から新たにレコードを生成
        Expense item = new Expense(id, expenseBool, date, amount, category, account, memo);

        // データベースへレコードを追加
        dao.add(item);

        // ArrayListへレコードを追加
        expenses.add(item);

        // /calendarへ転送
        return "redirect:/calendar";
    }


}
