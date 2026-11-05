package barun.account;

import java.util.Date;

/**
 * 지출 내역을 나타내는 클래스입니다. Transaction을 상속받습니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class Expense extends Transaction {
    private boolean isExcluded;

    /**
     * Expense 생성자
     * @param date 지출 날짜
     * @param category 지출 카테고리
     * @param amount 지출 금액
     * @param memo 상세 메모
     * @param isExcluded 예산 미포함(통계 제외) 여부
     */
    public Expense(Date date, String category, int amount, String memo, boolean isExcluded) {
        super(date, category, amount, memo);
        this.isExcluded = isExcluded;
    }

    @Override
    public boolean isExcludedFromBudget() { 
        return isExcluded; 
    }
}
