package com.abika.expense.application.service;

import com.abika.expense.application.port.in.ExpenseSavedListener;
import com.abika.expense.application.port.out.BudgetAlertRepository;
import com.abika.expense.application.port.out.DeviceTokenRepository;
import com.abika.expense.application.port.out.PushSender;
import com.abika.expense.application.port.out.TransactionRepository;
import com.abika.expense.domain.BudgetAlert;
import com.abika.expense.domain.Category;
import com.abika.expense.domain.DeviceToken;
import com.abika.expense.domain.Transaction;

import java.util.List;
import java.util.Locale;

public class BudgetCheckService implements ExpenseSavedListener {

    static final String TITLE = "Budget terlampaui";

    private static final System.Logger LOG = System.getLogger(BudgetCheckService.class.getName());
    private static final Locale INDONESIA = Locale.forLanguageTag("id-ID");

    private final TransactionRepository transactions;
    private final BudgetAlertRepository alerts;
    private final DeviceTokenRepository devices;
    private final PushSender pushSender;

    public BudgetCheckService(TransactionRepository transactions, BudgetAlertRepository alerts,
                              DeviceTokenRepository devices, PushSender pushSender) {
        this.transactions = transactions;
        this.alerts = alerts;
        this.devices = devices;
        this.pushSender = pushSender;
    }

    @Override
    public void onExpenseSaved(Transaction saved, Category category) {
        Long budget = category.monthlyBudget();
        if (budget == null) {
            return;
        }
        long spent = transactions.sumByCategoryAndMonth(category.id(), saved.month());
        if (spent <= budget) {
            return;
        }
        if (!alerts.insertIfAbsent(new BudgetAlert(saved.userId(), category.id(), saved.month()))) {
            return;
        }
        List<String> tokens = devices.findByUser(saved.userId()).stream().map(DeviceToken::fcmToken).toList();
        if (tokens.isEmpty()) {
            return;
        }
        // shortcut: the push goes out before the DB commit; a rollback after this point would leave a stray notification. Move to an after-commit hook if that ever matters.
        try {
            pushSender.send(tokens, TITLE, body(category.name(), spent, budget));
        } catch (RuntimeException e) {
            LOG.log(System.Logger.Level.WARNING, "Budget push failed for category " + category.id(), e);
        }
    }

    static String body(String categoryName, long spent, long budget) {
        return "Pengeluaran %s bulan ini Rp%s melebihi budget Rp%s".formatted(categoryName, rupiah(spent), rupiah(budget));
    }

    private static String rupiah(long amount) {
        return String.format(INDONESIA, "%,d", amount);
    }
}
