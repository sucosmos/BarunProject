package barun.account;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

/**
 * 바른 가계부의 Swing 기반 사용자 인터페이스(GUI) 클래스입니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class BarunGUI extends JFrame {
    private AccountManager manager;
    
    private JProgressBar budgetBar;
    private JProgressBar savingsBar;
    private JLabel budgetLabel;
    private JLabel savingsLabel;
    
    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    private JPanel listContainer;
    private JPanel calendarGridPanel;
    
    private JLabel monthLabel;
    private JLabel listTab;
    private JLabel calTab;
    
    private int currentYear;
    private int currentMonth;
    
    private List<String> categories;

    public BarunGUI(AccountManager manager) {
        this.manager = manager;
        
        categories = new ArrayList<>();
        categories.add("식비");
        categories.add("교통비");
        categories.add("문화생활");
        categories.add("생활용품");
        categories.add("기타");
        categories.add("+ 새 카테고리 추가");
        
        // 기존 내역 스캔하여 사용자 카테고리 복구
        for (Transaction t : manager.getTransactions()) {
            if (!categories.contains(t.getCategory()) && !"+ 새 카테고리 추가".equals(t.getCategory())) {
                categories.add(categories.size() - 1, t.getCategory());
            }
        }
        
        Calendar now = Calendar.getInstance();
        currentYear = now.get(Calendar.YEAR);
        currentMonth = now.get(Calendar.MONTH) + 1;

        setTitle("바른 가계부 - Final Release");
        setSize(580, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);
        
        JScrollPane listScroll = new JScrollPane(listContainer);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getVerticalScrollBar().setUnitIncrement(16);
        listScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        mainCardPanel.add(listScroll, "LIST");
        
        calendarGridPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarGridPanel.setBackground(new Color(230, 230, 230));
        calendarGridPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JPanel calWrapper = new JPanel(new BorderLayout());
        calWrapper.setBackground(Color.WHITE);
        
        JPanel dayOfWeekPanel = new JPanel(new GridLayout(1, 7));
        dayOfWeekPanel.setBackground(Color.WHITE);
        dayOfWeekPanel.setPreferredSize(new Dimension(0, 35));
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            JLabel dLabel = new JLabel(days[i], SwingConstants.CENTER);
            dLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            if(i == 0) dLabel.setForeground(Color.RED);
            if(i == 6) dLabel.setForeground(Color.BLUE);
            dayOfWeekPanel.add(dLabel);
        }
        calWrapper.add(dayOfWeekPanel, BorderLayout.NORTH);
        calWrapper.add(calendarGridPanel, BorderLayout.CENTER);
        mainCardPanel.add(calWrapper, "CALENDAR");
        
        add(mainCardPanel, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshData();
    }
    
    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = Color.WHITE;
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0,0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if(thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        
        JButton prevBtn = new JButton(" < ");
        prevBtn.setContentAreaFilled(false);
        prevBtn.setBorderPainted(false);
        prevBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        prevBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevBtn.addActionListener(e -> changeMonth(-1));
        
        JButton nextBtn = new JButton(" > ");
        nextBtn.setContentAreaFilled(false);
        nextBtn.setBorderPainted(false);
        nextBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextBtn.addActionListener(e -> changeMonth(1));
        
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        
        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextBtn, BorderLayout.EAST);
        
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        tabPanel.setBackground(Color.WHITE);
        
        listTab = new JLabel("<html><u>일일</u></html>");
        listTab.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        listTab.setForeground(Color.BLACK);
        listTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        calTab = new JLabel("달력");
        calTab.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        calTab.setForeground(Color.GRAY);
        calTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        listTab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(mainCardPanel, "LIST");
                listTab.setText("<html><u>일일</u></html>");
                listTab.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                listTab.setForeground(Color.BLACK);
                calTab.setText("달력");
                calTab.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                calTab.setForeground(Color.GRAY);
            }
        });
        
        calTab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(mainCardPanel, "CALENDAR");
                calTab.setText("<html><u>달력</u></html>");
                calTab.setFont(new Font("맑은 고딕", Font.BOLD, 14));
                calTab.setForeground(Color.BLACK);
                listTab.setText("일일");
                listTab.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                listTab.setForeground(Color.GRAY);
            }
        });
        
        tabPanel.add(listTab);
        tabPanel.add(calTab);
        
        JPanel statusContainer = new JPanel(new BorderLayout());
        statusContainer.setBackground(Color.WHITE);
        
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 5, 2));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        budgetLabel = new JLabel("월 목표 예산 현황");
        budgetLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        statusPanel.add(budgetLabel);
        
        budgetBar = new JProgressBar(0, 100);
        budgetBar.setStringPainted(true);
        statusPanel.add(budgetBar);

        savingsLabel = new JLabel("목표 저축 금액 달성도");
        savingsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        statusPanel.add(savingsLabel);
        
        savingsBar = new JProgressBar(0, 100);
        savingsBar.setStringPainted(true);
        statusPanel.add(savingsBar);
        
        statusContainer.add(statusPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton setBudgetBtn = new JButton("예산 설정");
        setBudgetBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        setBudgetBtn.addActionListener(e -> showBudgetSettingDialog());
        
        JButton setSavingsBtn = new JButton("저축 목표 설정");
        setSavingsBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        setSavingsBtn.addActionListener(e -> showSavingsGoalSettingDialog());
        
        btnPanel.add(setBudgetBtn);
        btnPanel.add(setSavingsBtn);
        statusContainer.add(btnPanel, BorderLayout.EAST);
        
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.add(navPanel, BorderLayout.NORTH);
        headerWrapper.add(tabPanel, BorderLayout.CENTER);
        headerWrapper.add(new JSeparator(), BorderLayout.SOUTH);
        
        topPanel.add(headerWrapper, BorderLayout.NORTH);
        topPanel.add(statusContainer, BorderLayout.CENTER);
        topPanel.add(new JSeparator(), BorderLayout.SOUTH);
        
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        JButton addBtn = new JButton("+ 내역 추가");
        addBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addBtn.setBackground(new Color(255, 100, 100));
        addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(130, 40));
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addBtn.addActionListener(e -> showAddTransactionDialog());
        bottomPanel.add(addBtn);
        
        return bottomPanel;
    }

    private void changeMonth(int offset) {
        currentMonth += offset;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        } else if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        refreshData();
    }

    private void refreshData() {
        monthLabel.setText(currentYear + "년 " + currentMonth + "월");
        
        Map<String, List<Transaction>> dailyTx = new TreeMap<>(Collections.reverseOrder());
        SimpleDateFormat keySdf = new SimpleDateFormat("yyyy-MM-dd");
        
        int totalExpenseThisMonth = 0;
        
        for (Transaction t : manager.getTransactions()) {
            Calendar c = Calendar.getInstance();
            c.setTime(t.getDate());
            if (c.get(Calendar.YEAR) == currentYear && (c.get(Calendar.MONTH) + 1) == currentMonth) {
                String key = keySdf.format(t.getDate());
                dailyTx.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
                
                if (!t.isExcludedFromBudget()) {
                    totalExpenseThisMonth += t.getAmount();
                }
            }
        }

        int budget = manager.getMonthlyBudget();
        budgetBar.setMaximum(budget);
        budgetBar.setValue(totalExpenseThisMonth);
        double budgetRatio = (budget > 0) ? ((double) totalExpenseThisMonth / budget * 100) : 0;
        budgetLabel.setText(String.format("월 목표 예산 현황 (%,d원 / %,d원) - %.1f%% 소진", totalExpenseThisMonth, budget, budgetRatio));

        if (budgetRatio >= 100) budgetBar.setForeground(new Color(220, 53, 69)); 
        else if (budgetRatio >= 80) budgetBar.setForeground(new Color(255, 140, 0));
        else budgetBar.setForeground(new Color(40, 167, 69));

        if (manager.isActiveSavingsMonth(currentYear, currentMonth)) {
            savingsBar.setVisible(true);
            int savedThisMonth = budget - totalExpenseThisMonth;
            if (savedThisMonth < 0) savedThisMonth = 0; 
            int monthlyTarget = manager.getMonthlySavingsTarget();
            
            savingsBar.setMaximum(monthlyTarget > 0 ? monthlyTarget : 1);
            savingsBar.setValue(savedThisMonth);
            double savingsRatio = (monthlyTarget > 0) ? ((double) savedThisMonth / monthlyTarget * 100) : 0;
            if (savingsRatio > 100) savingsRatio = 100;
            
            savingsLabel.setText(String.format("목표 저축 달성도: %.1f%% (%,d원 / %,d원)", savingsRatio, savedThisMonth, monthlyTarget));
            savingsLabel.setForeground(Color.BLACK);
        } else {
            savingsBar.setVisible(false);
            savingsLabel.setText("해당 월에 설정된 저축 목표가 없습니다.");
            savingsLabel.setForeground(Color.GRAY);
        }

        listContainer.removeAll();
        SimpleDateFormat dayFmt = new SimpleDateFormat("d"); 
        SimpleDateFormat dowFmt = new SimpleDateFormat("E요일");
        
        if (dailyTx.isEmpty()) {
            JLabel emptyLabel = new JLabel("이번 달 등록된 지출 내역이 없습니다.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
            listContainer.add(emptyLabel);
        } else {
            for (Map.Entry<String, List<Transaction>> entry : dailyTx.entrySet()) {
                List<Transaction> dayList = entry.getValue();
                Transaction firstTx = dayList.get(0);
                
                int dailyTotal = 0;
                for (Transaction t : dayList) {
                    if (!t.isExcludedFromBudget()) dailyTotal += t.getAmount();
                }
                
                JPanel dateHeader = new JPanel(new BorderLayout());
                dateHeader.setBackground(new Color(248, 249, 250));
                dateHeader.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
                dateHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                
                JPanel dateLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                dateLeft.setBackground(new Color(248, 249, 250));
                JLabel dNum = new JLabel(dayFmt.format(firstTx.getDate()) + "일");
                dNum.setFont(new Font("맑은 고딕", Font.BOLD, 18));
                
                JLabel dowLabel = new JLabel(" " + dowFmt.format(firstTx.getDate()) + " ");
                dowLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
                dowLabel.setOpaque(true);
                dowLabel.setBackground(new Color(170, 170, 170));
                dowLabel.setForeground(Color.WHITE);
                
                dateLeft.add(dNum);
                dateLeft.add(dowLabel);
                
                JLabel dTotal = new JLabel(String.format("합계: -%,d원", dailyTotal));
                dTotal.setFont(new Font("맑은 고딕", Font.BOLD, 13));
                dTotal.setForeground(new Color(230, 70, 70));
                
                dateHeader.add(dateLeft, BorderLayout.WEST);
                dateHeader.add(dTotal, BorderLayout.EAST);
                listContainer.add(dateHeader);
                
                for (Transaction t : dayList) {
                    JPanel rowPanel = new JPanel(new BorderLayout());
                    rowPanel.setBackground(Color.WHITE);
                    rowPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); 
                    
                    rowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    rowPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            showEditTransactionDialog(t);
                        }
                    });
                    
                    JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 3));
                    infoPanel.setBackground(Color.WHITE);
                    
                    JLabel catLabel = new JLabel(t.getCategory());
                    catLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
                    JLabel memoLabel = new JLabel(t.getMemo() + (t.isExcludedFromBudget() ? " (예산 제외)" : ""));
                    memoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                    memoLabel.setForeground(Color.GRAY);
                    
                    infoPanel.add(catLabel);
                    infoPanel.add(memoLabel);
                    
                    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                    rightPanel.setBackground(Color.WHITE);
                    
                    JLabel amtLabel = new JLabel(String.format("-%,d원", t.getAmount()));
                    amtLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
                    amtLabel.setForeground(new Color(230, 70, 70));
                    
                    JButton delBtn = new JButton("X");
                    delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 11));
                    delBtn.setForeground(Color.LIGHT_GRAY);
                    delBtn.setBorderPainted(false);
                    delBtn.setContentAreaFilled(false);
                    delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    delBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this, "이 내역을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            manager.deleteTransaction(t);
                            refreshData();
                        }
                    });
                    
                    rightPanel.add(amtLabel);
                    rightPanel.add(delBtn);
                    
                    rowPanel.add(infoPanel, BorderLayout.CENTER);
                    rowPanel.add(rightPanel, BorderLayout.EAST);
                    
                    listContainer.add(rowPanel);
                    listContainer.add(new JSeparator());
                }
            }
        }
        
        calendarGridPanel.removeAll();
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth - 1, 1);
        int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Calendar today = Calendar.getInstance();
        
        for (int i = 0; i < 42; i++) {
            JPanel dayCell = new JPanel(new BorderLayout());
            dayCell.setBackground(Color.WHITE);
            dayCell.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
            
            if (i >= startDayOfWeek && i < startDayOfWeek + daysInMonth) {
                int dateNum = i - startDayOfWeek + 1;
                
                boolean isToday = (currentYear == today.get(Calendar.YEAR) && 
                                   currentMonth == (today.get(Calendar.MONTH) + 1) && 
                                   dateNum == today.get(Calendar.DAY_OF_MONTH));
                
                if (isToday) {
                    dayCell.setBackground(new Color(245, 245, 245));
                    dayCell.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                }
                
                JPanel dateWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
                dateWrapper.setOpaque(false);
                
                JLabel dateLabel = new JLabel(String.valueOf(dateNum), SwingConstants.CENTER);
                dateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
                
                if(i % 7 == 0) dateLabel.setForeground(Color.RED);
                else if(i % 7 == 6) dateLabel.setForeground(Color.BLUE);
                else dateLabel.setForeground(Color.BLACK);
                
                dateWrapper.add(dateLabel);
                dayCell.add(dateWrapper, BorderLayout.NORTH);
                
                boolean isPast = false;
                if (currentYear < today.get(Calendar.YEAR)) {
                    isPast = true;
                } else if (currentYear == today.get(Calendar.YEAR)) {
                    if (currentMonth < today.get(Calendar.MONTH) + 1) {
                        isPast = true;
                    } else if (currentMonth == today.get(Calendar.MONTH) + 1 && dateNum < today.get(Calendar.DAY_OF_MONTH)) {
                        isPast = true;
                    }
                }
                
                String key = String.format("%04d-%02d-%02d", currentYear, currentMonth, dateNum);
                int dailyTotal = 0;
                List<Transaction> cellTx = new ArrayList<>();
                
                if (dailyTx.containsKey(key)) {
                    cellTx = dailyTx.get(key);
                    for(Transaction t : cellTx) {
                        if (!t.isExcludedFromBudget()) dailyTotal += t.getAmount();
                    }
                }
                
                if (dailyTotal > 0) {
                    JLabel amtLabel = new JLabel("-" + String.format("%,d", dailyTotal), SwingConstants.CENTER);
                    amtLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
                    amtLabel.setForeground(new Color(230, 70, 70));
                    dayCell.add(amtLabel, BorderLayout.CENTER);
                    
                    final List<Transaction> targetList = cellTx;
                    final String targetDateStr = key;
                    dayCell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    dayCell.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent evt) {
                            showDailyDetailDialog(targetDateStr, targetList);
                        }
                    });
                } else if (isPast) { 
                    JLabel stampLabel = new JLabel("★무지출", SwingConstants.CENTER);
                    stampLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
                    stampLabel.setForeground(new Color(40, 167, 69));
                    dayCell.add(stampLabel, BorderLayout.CENTER);
                }
            }
            calendarGridPanel.add(dayCell);
        }
        
        listContainer.revalidate();
        listContainer.repaint();
        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private void showDailyDetailDialog(String dateStr, List<Transaction> txList) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        int sum = 0;
        for(Transaction t : txList) {
            sum += t.getAmount();
            JLabel item = new JLabel(String.format("• [%s] %s : %,d원 %s", 
                    t.getCategory(), t.getMemo(), t.getAmount(), t.isExcludedFromBudget() ? "(예산외)":""));
            item.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            item.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel.add(item);
        }
        
        JLabel total = new JLabel(String.format("\n총 사용 금액: %,d원", sum));
        total.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        total.setForeground(new Color(230, 70, 70));
        total.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.add(total);
        
        JOptionPane.showMessageDialog(this, new JScrollPane(panel), dateStr + " 상세 내역", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBudgetSettingDialog() {
        JTextField budgetField = new JTextField(String.valueOf(manager.getMonthlyBudget()));
        Object[] message = { "월 목표 예산 금액:", budgetField };

        int option = JOptionPane.showConfirmDialog(this, message, "월 예산 설정", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                manager.setMonthlyBudget(Integer.parseInt(budgetField.getText().replace(",", "")));
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자만 정확히 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSavingsGoalSettingDialog() {
        JTextField amountField = new JTextField(String.valueOf(manager.getSavingsGoalAmount()));
        JTextField startField = new JTextField(manager.getSavingsStartMonth());
        JTextField endField = new JTextField(manager.getSavingsEndMonth());

        Object[] message = {
            "총 목표 저축 금액:", amountField,
            "저축 시작월 (YYYY-MM):", startField,
            "저축 마감월 (YYYY-MM):", endField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "저축 목표 설정", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                manager.setSavingsGoalAmount(Integer.parseInt(amountField.getText().replace(",", "")));
                manager.setSavingsStartMonth(startField.getText());
                manager.setSavingsEndMonth(endField.getText());
                refreshData();
                JOptionPane.showMessageDialog(this, "저축 목표가 업데이트되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "입력 형식을 확인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 카테고리 콤보박스 단순 추가 핸들러
    private void handleCategorySelection(JComboBox<String> categoryBox) {
        if ("+ 새 카테고리 추가".equals(categoryBox.getSelectedItem())) {
            String newCat = JOptionPane.showInputDialog(this, "새 카테고리 이름을 입력하세요:", "카테고리 추가", JOptionPane.PLAIN_MESSAGE);
            if (newCat != null && !newCat.trim().isEmpty()) {
                categories.add(categories.size() - 1, newCat.trim());
                categoryBox.removeItem("+ 새 카테고리 추가");
                categoryBox.addItem(newCat.trim());
                categoryBox.addItem("+ 새 카테고리 추가");
                categoryBox.setSelectedItem(newCat.trim());
            } else {
                categoryBox.setSelectedIndex(0);
            }
        }
    }

    private void showAddTransactionDialog() {
        JTextField dateField = new JTextField(String.format("%04d-%02d-%02d", currentYear, currentMonth, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        
        JComboBox<String> categoryBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryBox.addActionListener(e -> handleCategorySelection(categoryBox));
        
        JTextField memoField = new JTextField();
        JTextField amountField = new JTextField();
        JCheckBox excludeBox = new JCheckBox("예산 미포함(통계 제외)");
        
        JButton dutchBtn = new JButton("더치페이 계산");
        dutchBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        dutchBtn.addActionListener(e -> {
            JTextField totField = new JTextField();
            JTextField pplField = new JTextField("2");
            Object[] msg = { "결제 총액:", totField, "참석 인원:", pplField };
            int res = JOptionPane.showConfirmDialog(this, msg, "더치페이 1차 산출", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int tot = Integer.parseInt(totField.getText().replace(",", ""));
                    int ppl = Integer.parseInt(pplField.getText());
                    int perPerson = manager.calculateDutchPay(tot, ppl);
                    amountField.setText(String.valueOf(perPerson));
                    JOptionPane.showMessageDialog(this, "1인당 금액(" + perPerson + "원)이 반영되었습니다.\n오차가 있다면 금액 칸에서 직접 수정하세요.", "더치페이", JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel amtPanel = new JPanel(new BorderLayout(5, 0));
        amtPanel.add(amountField, BorderLayout.CENTER);
        amtPanel.add(dutchBtn, BorderLayout.EAST);

        Object[] message = {
            "날짜 (YYYY-MM-DD):", dateField,
            "카테고리:", categoryBox,
            "메모:", memoField,
            "최종 금액:", amtPanel,
            excludeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "새 내역 등록", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                String dateStr = dateField.getText();
                String category = (String) categoryBox.getSelectedItem();
                String memo = memoField.getText();
                int amount = Integer.parseInt(amountField.getText().replace(",", ""));
                
                boolean isExcluded = excludeBox.isSelected();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dateStr);

                manager.recordTransaction(new Expense(date, category, amount, memo, isExcluded));
                
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                currentYear = c.get(Calendar.YEAR);
                currentMonth = c.get(Calendar.MONTH) + 1;
                
                refreshData(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 형식을 다시 확인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditTransactionDialog(Transaction t) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateField = new JTextField(sdf.format(t.getDate()));
        
        JComboBox<String> categoryBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryBox.setSelectedItem(t.getCategory());
        categoryBox.addActionListener(e -> handleCategorySelection(categoryBox));
        
        JTextField memoField = new JTextField(t.getMemo());
        JTextField amountField = new JTextField(String.valueOf(t.getAmount()));
        JCheckBox excludeBox = new JCheckBox("예산 미포함(통계 제외)", t.isExcludedFromBudget());

        JButton dutchBtn = new JButton("더치페이 계산");
        dutchBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        dutchBtn.addActionListener(e -> {
            JTextField totField = new JTextField();
            JTextField pplField = new JTextField("2");
            Object[] msg = { "결제 총액:", totField, "참석 인원:", pplField };
            int res = JOptionPane.showConfirmDialog(this, msg, "더치페이 1차 산출", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int tot = Integer.parseInt(totField.getText().replace(",", ""));
                    int ppl = Integer.parseInt(pplField.getText());
                    int perPerson = manager.calculateDutchPay(tot, ppl);
                    amountField.setText(String.valueOf(perPerson));
                    JOptionPane.showMessageDialog(this, "1인당 금액(" + perPerson + "원)이 반영되었습니다.\n오차가 있다면 금액 칸에서 직접 수정하세요.", "더치페이", JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel amtPanel = new JPanel(new BorderLayout(5, 0));
        amtPanel.add(amountField, BorderLayout.CENTER);
        amtPanel.add(dutchBtn, BorderLayout.EAST);

        Object[] message = {
            "날짜 (YYYY-MM-DD):", dateField,
            "카테고리:", categoryBox,
            "메모:", memoField,
            "최종 금액:", amtPanel,
            excludeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "내역 수정", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                String dateStr = dateField.getText();
                String category = (String) categoryBox.getSelectedItem();
                String memo = memoField.getText();
                int amount = Integer.parseInt(amountField.getText().replace(",", ""));
                
                boolean isExcluded = excludeBox.isSelected();
                Date date = sdf.parse(dateStr);

                manager.updateTransaction(t, new Expense(date, category, amount, memo, isExcluded));
                refreshData(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 형식을 다시 확인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}