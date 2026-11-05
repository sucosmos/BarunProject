package barun.account;

import java.util.Date;

/**
 * 수입 내역을 나타내는 클래스입니다. Transaction을 상속받습니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class Income extends Transaction {
    private String source;

    /**
     * Income 생성자
     * @param date 수입 날짜
     * @param category 수입 카테고리
     * @param amount 수입 금액
     * @param memo 상세 메모
     * @param source 수입처
     */
    public Income(Date date, String category, int amount, String memo, String source) {
        super(date, category, amount, memo);
        this.source = source;
    }

    @Override
    public boolean isExcludedFromBudget() { 
        return false; 
    }
}
