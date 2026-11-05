package barun.account;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * 바른 가계부의 핵심 비즈니스 로직을 처리하는 관리자 클래스입니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class AccountManager {
    private List<Transaction> transactions;
    private int monthlyBudget = 1000000;
    
    private int savingsGoalAmount = 5000000;
    private String savingsStartMonth = "2026-11";
    private String savingsEndMonth = "2026-12";
    
    private DataStorage dataStorage;

    public AccountManager() {
        dataStorage = new DataStorage();
        transactions = dataStorage.loadTransactions();
    }

    public boolean recordTransaction(Transaction t) {
        transactions.add(t);
        saveData();
        return true;
    }

    public void updateTransaction(Transaction oldTx, Transaction newTx) {
        int index = transactions.indexOf(oldTx);
        if (index != -1) {
            transactions.set(index, newTx);
            saveData();
        }
    }

    public void deleteTransaction(Transaction t) {
        transactions.remove(t);
        saveData();
    }

    public int calculateDutchPay(int totalAmount, int peopleCount) {
        if (peopleCount <= 0) return 0;
        return totalAmount / peopleCount;
    }
    
    public List<Transaction> getTransactions() { return transactions; }
    
    public void saveData() { dataStorage.saveTransactions(transactions); }

    public int getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(int budget) { this.monthlyBudget = budget; }

    public int getSavingsGoalAmount() { return savingsGoalAmount; }
    public void setSavingsGoalAmount(int amount) { this.savingsGoalAmount = amount; }

    public String getSavingsStartMonth() { return savingsStartMonth; }
    public void setSavingsStartMonth(String month) { this.savingsStartMonth = month; }

    public String getSavingsEndMonth() { return savingsEndMonth; }
    public void setSavingsEndMonth(String month) { this.savingsEndMonth = month; }

    // 설정된 YYYY-MM 문자열을 기반으로 개월 수 자동 계산
    public int getSavingsDurationMonths() {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
            Date start = sdf.parse(savingsStartMonth);
            Date end = sdf.parse(savingsEndMonth);
            
            Calendar startCal = Calendar.getInstance(); startCal.setTime(start);
            Calendar endCal = Calendar.getInstance(); endCal.setTime(end);
            
            int diffYear = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH) + 1;
            
            return diffMonth > 0 ? diffMonth : 1;
        } catch (Exception e) {
            return 1;
        }
    }
    
    public int getMonthlySavingsTarget() {
        int duration = getSavingsDurationMonths();
        if (duration <= 0) return 0;
        return savingsGoalAmount / duration;
    }

    // 현재 보고 있는 화면의 연/월이 저축 기간에 포함되는지 확인
    public boolean isActiveSavingsMonth(int year, int month) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
            Date start = sdf.parse(savingsStartMonth);
            Date end = sdf.parse(savingsEndMonth);
            Calendar calStart = Calendar.getInstance(); calStart.setTime(start);
            Calendar calEnd = Calendar.getInstance(); calEnd.setTime(end);
            
            int startVal = calStart.get(Calendar.YEAR) * 100 + calStart.get(Calendar.MONTH) + 1;
            int endVal = calEnd.get(Calendar.YEAR) * 100 + calEnd.get(Calendar.MONTH) + 1;
            int currentVal = year * 100 + month;
            
            return currentVal >= startVal && currentVal <= endVal;
        } catch(Exception e) {
            return false;
        }
    }
}