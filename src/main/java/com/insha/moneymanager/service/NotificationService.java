package com.insha.moneymanager.service;

import com.insha.moneymanager.dto.ExpenseDTO;
import com.insha.moneymanager.entity.ProfileEntity;
import com.insha.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontEndurl;

//    @Scheduled(cron = "0 * * * * *", zone = "IST") // every minute for testing purpose
    @Scheduled(cron = "0 0 22 * * *", zone = "IST") // every day at 10 PM IST
    public void sendDailyIncomeExpenseReminder() {
        log.info("job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for(ProfileEntity profile : profiles) {
            String body = "Hi" + profile.getName() + ",<br><br>" +
                    "This is friendly reminder to add your income and expenses for today in money manager app.<br>" +
                    "<a href="+frontEndurl+" style='display: inline-block; padding: 10px 20px; background-color: #4CAF50;color:#fff; text-decoration: none; border-radius: 5px; font-weight:bold;'>Go to Money manager</a><br><br>" +
                    "<br><br>Best regards,<br>Money Manager Team";
            emailService.sendMail(profile.getEmail(), "Daily Reminder: Add Your Income and Expenses", body);
        }
    }

//    @Scheduled(cron = "0 * * * * *", zone = "IST") // every minute for testing purpose
    @Scheduled(cron = "0 0 23 * * *", zone = "IST") // every day at 11 PM IST
    public void sendDailyExpenseSummary() {
        log.info("job started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for(ProfileEntity profile : profiles){
            List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if(!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='width: 100%; border-collapse: collapse;'>");
                table.append("<tr style='background-color: #f2f2f2;'><th style='border: 1px solid #ddd; padding: 8px;'>S.No</th><th style='border: 1px solid #ddd; padding: 8px;'>Name</th><th style='border: 1px solid #ddd; padding: 8px;'>Category</th><th style='border: 1px solid #ddd; padding: 8px;'>Amount</th></tr>");
                int i = 1;
                for(ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center;'>").append(i++).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("<td style='border: 1px solid #ddd; padding: 8px; text-align: right;'>").append(expense.getAmount()).append("</td>")
                            .append("</tr>");
                }
                table.append("</table>");
                String body = "Hi " + profile.getName() + ",<br><br>" +
                        "Here is the summary of your expenses for today:<br><br>" +
                        table +
                        "<br><br>Best regards,<br>Money Manager Team";

                emailService.sendMail(profile.getEmail(), "Your Daily Expense Summary", body);
            }

        }
        log.info("job ended: sendDailyExpenseSummary()");
    }
}
