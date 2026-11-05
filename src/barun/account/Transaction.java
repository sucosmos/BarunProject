package barun.account;

import java.io.Serializable;
import java.util.Date;

/**
 * 가계부 수입/지출 내역의 기본이 되는 추상 클래스입니다.
 * <br>
 * 모든 내역은 이 클래스를 상속받아 구현됩니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public abstract class Transaction implements Serializable {
    protected Date date;
    protected String category;
    protected int amount;
    protected String memo;

    /**
     * Transaction 생성자
     * @param date 내역 날짜
     * @param category 카테고리 (예: 식비, 교통비 등)
     * @param amount 금액
     * @param memo 상세 메모
     */
    public Transaction(Date date, String category, int amount, String memo) {
        this.date = date; 
        this.category = category; 
        this.amount = amount; 
        this.memo = memo;
    }

    /**
     * 내역의 금액을 반환합니다.
     * @return 금액 (int)
     */
    public int getAmount() { return amount; }

    /**
     * 카테고리를 반환합니다.
     * @return 카테고리 (String)
     */
    public String getCategory() { return category; }

    /**
     * 날짜를 반환합니다.
     * @return 날짜 (Date)
     */
    public Date getDate() { return date; }

    /**
     * 예산 통계에서 제외할지 여부를 반환하는 추상 메서드입니다.
     * @return 통계 제외 여부 (boolean)
     */
    public abstract boolean isExcludedFromBudget();
    
    /**
     * 메모를 반환합니다.
     * @return 메모 (String)
     */
    public String getMemo() { return memo; }
}
