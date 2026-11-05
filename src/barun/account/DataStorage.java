package barun.account;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 가계부 데이터의 파일 입출력을 담당하는 클래스입니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class DataStorage {
    private String filePath = "transactions.dat";

    /**
     * 내역 리스트를 파일에 저장합니다.
     * @param list 저장할 Transaction 리스트
     * @return 정상 저장 여부
     */
    public boolean saveTransactions(List<Transaction> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
            return true;
        } catch (Exception e) { 
            return false; 
        }
    }

    /**
     * 파일에서 내역 리스트를 불러옵니다.
     * @return 불러온 Transaction 리스트
     */
    @SuppressWarnings("unchecked")
    public List<Transaction> loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<Transaction>) ois.readObject();
        } catch (Exception e) { 
            return new ArrayList<>(); 
        }
    }
}
